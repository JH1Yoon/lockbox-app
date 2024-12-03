package com.example.lockbox.domain.user.dto.response;

import com.example.lockbox.domain.user.entity.User;
import com.example.lockbox.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String email;

    private UserRole userRole;

    public UserResponseDto(String email, UserRole userRole) {
        this.email = email;
        this.userRole = userRole;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getUserRole()
        );
    }
}
