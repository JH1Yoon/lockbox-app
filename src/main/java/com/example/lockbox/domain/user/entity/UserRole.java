package com.example.lockbox.domain.user.entity;

import com.example.lockbox.common.exception.NotFoundException;

import java.util.Arrays;

public enum UserRole {
    USER, ADMIN;

    public static UserRole fromString(String string) {
        return Arrays.stream(UserRole.values())
                .filter(u -> u.toString().equalsIgnoreCase(string))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("유효하지 않은 UserRole"));
    }
}
