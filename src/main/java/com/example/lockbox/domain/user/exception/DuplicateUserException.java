package com.example.lockbox.domain.user.exception;

import com.example.lockbox.common.exception.BadRequestException;

public class DuplicateUserException extends BadRequestException {
    public DuplicateUserException(String message) {
        super(message);
    }
}