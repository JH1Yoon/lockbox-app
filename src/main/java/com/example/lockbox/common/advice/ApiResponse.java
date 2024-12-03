package com.example.lockbox.common.advice;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class ApiResponse<T> {
    private String status;
    private String message;
    private T data;

    public ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.status = "success";
        this.message = message;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public T getData() {
        return data;
    }
}


