package com.example.lockbox.domain.user.dto.response;

import com.example.lockbox.domain.user.entity.User;
import com.example.lockbox.domain.user.entity.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private String name;
    private String phoneNumber;
    private UserRole userRole;


    public UserResponseDto(String email, String name, String phoneNumber, UserRole userRole) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.userRole = userRole;
    }

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserRole()
        );
    }
}
