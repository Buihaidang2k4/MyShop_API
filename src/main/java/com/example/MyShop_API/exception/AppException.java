package com.example.MyShop_API.exception;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException {
    ErrorCode errorCode;

    public AppException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.toString(), args));
        this.errorCode = errorCode;
    }
}
