package com.example.MyShop_API.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),

    INVALID_KEY(1000, "Invalid key", HttpStatus.BAD_REQUEST),

    USER_EXISTED(1001, "User already exists", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1002, "User not found", HttpStatus.NOT_FOUND),
    USER_INVALID(1003, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1004, "Password must be at least {min} characters", HttpStatus.BAD_REQUEST),

    UNAUTHORIZED(1005, "You do not have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1006, "Authentication required", HttpStatus.UNAUTHORIZED),

    ADDRESS_NOT_EXISTED(1007, "Address not found", HttpStatus.NOT_FOUND),
    ADDRESS_EXISTED(1008, "Address already exists", HttpStatus.BAD_REQUEST),

    CART_NOT_EXISTED(1009, "Cart not found", HttpStatus.NOT_FOUND),
    CART_EXISTED(1010, "Cart already exists", HttpStatus.BAD_REQUEST),

    ROLE_NOT_ALLOWED(1011, "Role not allowed", HttpStatus.FORBIDDEN),

    PROFILE_EXISTED(1012, "Profile already exists", HttpStatus.BAD_REQUEST),

    PRODUCT_NOT_EXISTED(1013, "Product not found", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1014, "Product is out of stock", HttpStatus.BAD_REQUEST),
    PRODUCT_IS_NOT_ENOUGH(1015, "Requested: %d, Available: %d", HttpStatus.BAD_REQUEST),

    CATEGORY_NOT_EXISTED(1016, "Category not found", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1017, "Category already exists", HttpStatus.BAD_REQUEST),

    PAYMENT_EXISTED(1018, "Payment already exists", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_EXISTED(1019, "Payment not found", HttpStatus.NOT_FOUND),

    ORDER_ITEM_NOT_EXISTED(1020, "Order item not found", HttpStatus.NOT_FOUND),
    ORDER_ITEM_EXISTED(1021, "Order item already exists", HttpStatus.BAD_REQUEST),

    ORDER_EXISTED(1022, "Order already exists", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1023, "Order not found", HttpStatus.NOT_FOUND),
    
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
