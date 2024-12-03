package com.example.lockbox.common.config;

import com.example.lockbox.common.exception.UnauthorizedException;
import com.example.lockbox.common.service.CustomUserDetailsService;
import com.example.lockbox.common.service.JwtBlackListService;
import com.example.lockbox.domain.user.entity.UserRole;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtSecurityFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtBlackListService jwtBlackListService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 인증이 필요 없는 경로들 (예: 로그인, 회원가입)
        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/users/signup") || path.startsWith("/api/v1/users/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String jwt = jwtUtil.substringToken(authorizationHeader);

            // access token이 블랙리스트에 포함되어 있는지 체크
            if (jwtBlackListService.isAccessTokenBlackListed(jwt)) {
                log.warn("로그아웃된 사용자, 블랙리스트에 포함된 토큰: {}", jwt);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "로그아웃된 사용자입니다.");
                return;
            }

            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "잘못된 JWT 토큰입니다.");
                return;
            }

            String email = claims.get("email", String.class);
            UserRole userRole = UserRole.fromString(claims.get("userRole", String.class));

            request.setAttribute("email", email);
            request.setAttribute("userRole", userRole);

            // 이메일이 존재하고, 인증되지 않은 상태라면
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication); // 인증 정보 설정
                }
            }
        }

        // 필터 체인의 다음 필터로 요청을 전달
        filterChain.doFilter(request, response);
    }
}