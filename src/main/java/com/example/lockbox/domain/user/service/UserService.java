package com.example.lockbox.domain.user.service;

import com.example.lockbox.common.config.JwtUtil;
import com.example.lockbox.common.dto.CustomUserDetails;
import com.example.lockbox.common.service.JwtBlackListService;
import com.example.lockbox.domain.user.dto.request.UserRequestDto;
import com.example.lockbox.domain.user.dto.response.UserResponseDto;
import com.example.lockbox.domain.user.entity.User;
import com.example.lockbox.domain.user.entity.UserRole;
import com.example.lockbox.domain.user.exception.AlreadyDeletedUser;
import com.example.lockbox.domain.user.exception.DuplicateUserException;
import com.example.lockbox.domain.user.exception.PasswordNotMatchException;
import com.example.lockbox.domain.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JwtBlackListService jwtBlackListService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 회원가입
     *
     * @param userRequestDto
     * @return UserResponseDto
     */
    @Transactional
    public UserResponseDto signUp(UserRequestDto userRequestDto) {
        Optional<User> existingUser = userRepository.findByEmail(userRequestDto.getEmail());

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // 이미 탈퇴한 유저라면, 복구 처리 후 재가입 허용
            if (user.getIsDelete()) {
                user.recoverUser();
                user.updatePassword(passwordEncoder.encode(userRequestDto.getPassword()));
                return UserResponseDto.from(user);
            }

            throw new DuplicateUserException("이미 가입된 유저입니다.");
        }

        // 새 유저 생성
        String password = passwordEncoder.encode(userRequestDto.getPassword());
        UserRole role = Optional.ofNullable(userRequestDto.getUserRole()).orElse(UserRole.USER);

        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(password)
                .name(userRequestDto.getName())
                .phoneNumber(userRequestDto.getPhoneNumber())
                .userRole(role)
                .build();

        userRepository.save(user); // 새 유저는 저장
        return UserResponseDto.from(user);
    }

    /**
     * 로그인
     *
     * @param userRequestDto
     * @return String
     */
    @Transactional
    public String login(UserRequestDto userRequestDto) {
        User user = userRepository.findByEmailOrThrow(userRequestDto.getEmail());

        if (user.getIsDelete()) {
            throw new AlreadyDeletedUser("이미 탈퇴한 유저입니다.");
        }

        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 맞지않습니다.");
        }

        String accessToken = jwtUtil.createToken(user.getEmail(), user.getUserRole());
        String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getUserRole());

        redisTemplate.opsForValue().set(
                "RT:" + user.getEmail(),
                refreshToken,
                jwtUtil.getTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );

        return accessToken;
    }

    /**
     * 로그아웃
     *
     * @param token
     */
    public void logout(String token) {

        jwtBlackListService.addAccessTokenToBlackList(token);
        jwtUtil.invalidToken(token);

        String email = jwtUtil.getUserEmailFromToken(token);
        redisTemplate.delete("RT:" + email);

        SecurityContextHolder.clearContext();
    }

    /**
     * 마이페이지 조회
     *
     * @param authUser
     * @return UserResponseDto
     */
    public UserResponseDto getMyPage(CustomUserDetails authUser) {
        // A토큰이 유효하다면 유저 정보를 반환
        User user = userRepository.findByEmailOrThrow(authUser.getEmail());
        return UserResponseDto.from(user);
    }

    /**
     * 유저 수정
     *
     * @param email
     * @param userRequestDto
     * @return UserResponseDto
     */
    @Transactional
    public UserResponseDto updateUser(String email, @Valid UserRequestDto userRequestDto) {
        User user = userRepository.findByEmailOrThrow(email);

        String newPassword = userRequestDto.getPassword();
        if (newPassword != null && !newPassword.isEmpty() && !passwordEncoder.matches(newPassword, user.getPassword())) {
            newPassword = passwordEncoder.encode(newPassword);
            user.updatePassword(newPassword);
        }

        if (userRequestDto.getName() != null) {
            user.updateName(userRequestDto.getName());
        }

        if (userRequestDto.getPhoneNumber() != null) {
            user.updatePhoneNumber(userRequestDto.getPhoneNumber());
        }

        userRepository.save(user);
        return UserResponseDto.from(user);
    }

    /**
     * 유저 탈퇴
     *
     * @param email
     * @param password
     */
    @Transactional
    public void delete(String email, String password, String token) {
        User user = userRepository.findByEmailOrThrow(email);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 맞지않습니다.");
        }

        if (user.getIsDelete()) {
            throw new AlreadyDeletedUser("유저가 이미 삭제되었습니다.");
        }

        if (token != null) {
            jwtBlackListService.addAccessTokenToBlackList(token);
            jwtUtil.invalidToken(token);
        } else {
            log.warn("탈퇴 시 토큰이 존재하지 않습니다. 사용자 로그아웃 상태일 가능성 있음.");
        }

        redisTemplate.delete("RT:" + email);

        user.deleteUser();
    }
}