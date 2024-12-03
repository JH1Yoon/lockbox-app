package com.example.lockbox.domain.user.controller;

import com.example.lockbox.common.advice.ApiResponse;
import com.example.lockbox.common.dto.CustomUserDetails;
import com.example.lockbox.domain.user.dto.request.UserRequestDto;
import com.example.lockbox.domain.user.dto.response.UserResponseDto;
import com.example.lockbox.domain.user.exception.DuplicateUserException;
import com.example.lockbox.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserResponseDto>> signUp(@Valid @RequestBody UserRequestDto userRequestDto)  {
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", userService.signUp(userRequestDto)));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", userService.login(userRequestDto)));
    }
    
    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok(new ApiResponse<>("로그아웃 성공"));
    }

    // 마이페이지
    @GetMapping("/myPage")
    public ResponseEntity<ApiResponse<UserResponseDto>> getMyPage(@AuthenticationPrincipal CustomUserDetails authUser) {
        return ResponseEntity.ok(ApiResponse.success("마이페이지 조회 성공", userService.getMyPage(authUser)));
    }

    // 유저 수정
    @PatchMapping("/update")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@AuthenticationPrincipal CustomUserDetails authUser,
                                                                   @Valid @RequestBody UserRequestDto userRequestDto) {
        return ResponseEntity.ok(ApiResponse.success("수정 성공 ", userService.updateUser(authUser.getEmail(), userRequestDto)));
    }

    // 유저 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal CustomUserDetails authUser,
                                                    @RequestHeader("Authorization") String token,
                                                    @Valid @RequestBody UserRequestDto userRequestDto) {
        userService.delete(authUser.getEmail(), userRequestDto.getPassword(), token);
        return ResponseEntity.ok(ApiResponse.success("탈퇴 성공"));
    }
}
