package com.example.MyShop_API.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", INTERNAL_SERVER_ERROR),

    INVALID_KEY(1000, "Invalid key", BAD_REQUEST),

    USER_EXISTED(1001, "User already exists", BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User not found", NOT_FOUND),
    USER_INVALID(1003, "Username must be at least {min} characters", BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", BAD_REQUEST),

    UNAUTHORIZED(1005, "You do not have permission", FORBIDDEN),
    UNAUTHENTICATED(1006, "Authentication required", HttpStatus.UNAUTHORIZED),

    ADDRESS_NOT_EXISTED(1007, "Address not found", NOT_FOUND),
    ADDRESS_EXISTED(1008, "Address already exists", BAD_REQUEST),

    CART_NOT_EXISTED(1009, "Cart not found", NOT_FOUND),
    CART_EXISTED(1010, "Cart already exists", BAD_REQUEST),

    ROLE_NOT_ALLOWED(1011, "Role not allowed", FORBIDDEN),

    PROFILE_EXISTED(1012, "Profile already exists", BAD_REQUEST),

    PRODUCT_NOT_EXISTED(1013, "Product not found", NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1014, "Product is out of stock", BAD_REQUEST),
    PRODUCT_IS_NOT_ENOUGH(1015, "Requested: %d, Available: %d", BAD_REQUEST),

    CATEGORY_NOT_EXISTED(1016, "Category not found", NOT_FOUND),
    CATEGORY_EXISTED(1017, "Category already exists", BAD_REQUEST),

    PAYMENT_EXISTED(1018, "Payment already exists", BAD_REQUEST),
    PAYMENT_NOT_EXISTED(1019, "Payment not found", NOT_FOUND),

    ORDER_ITEM_NOT_EXISTED(1020, "Order item not found", NOT_FOUND),
    ORDER_ITEM_EXISTED(1021, "Order item already exists", BAD_REQUEST),

    ORDER_EXISTED(1022, "Order already exists", BAD_REQUEST),
    ORDER_NOT_EXISTED(1023, "Order not found", NOT_FOUND),

    CART_ITEM_NOT_EXISTED(1024, "CartItem not found", NOT_FOUND),
    CART_ITEM_EXISTED(1025, "CartItem already exists", BAD_REQUEST),

    TOKEN_EXPIRED(1026, "Token expired", BAD_REQUEST),

    IMAGE_NOT_FOUND(1027, "Image not found id: %d", NOT_FOUND),
    REDIS_ERROR(1028, "Redis error", INTERNAL_SERVER_ERROR),
    TOKEN_REVOKED(1029, "Token has been revoked or is no longer valid", UNAUTHORIZED.getHttpStatus()),

    VALIDATION_ERROR(1030, "Input data is not valid", BAD_REQUEST),

    ACCOUNT_NOT_EXISTED(1031, "incorrect password account", BAD_REQUEST),

    ;


    final int code;
    final String messageTemplate;
    final HttpStatus httpStatus;

    ErrorCode(int code, String messageTemplate, HttpStatus httpStatus) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }
}
