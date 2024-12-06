package com.example.lockbox.domain.user.dto.request;

import com.example.lockbox.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @Email(message = "유효한 email 형식이어야 합니다.")
    private String email;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8자 이상, 대소문자, 숫자 및 특수문자를 포함해야 합니다.")
    private String password;

    private UserRole userRole;

    private String name;  // 이름 추가

    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$", message = "유효한 전화번호 형식이어야 합니다.")
    private String phoneNumber;  // 전화번호 추가
}
