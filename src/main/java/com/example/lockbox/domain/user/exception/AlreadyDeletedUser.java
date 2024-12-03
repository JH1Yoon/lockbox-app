package com.example.lockbox.domain.user.exception;

public class AlreadyDeletedUser extends RuntimeException {
    public AlreadyDeletedUser(String message) {
        super(message);
    }
}
