package com.example.lockbox.domain.user.exception;

import com.example.lockbox.common.exception.BadRequestException;

public class PasswordNotMatchException extends BadRequestException {
    public PasswordNotMatchException(String message) { super(message); }
}
