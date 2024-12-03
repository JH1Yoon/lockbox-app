package com.example.lockbox.domain.user.dto.request;

import com.example.lockbox.domain.user.entity.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserRequestDto {
    @Email(message = "유효한 email 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해야합니다.")
    private String password;

    private UserRole userRole;
}
