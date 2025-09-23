package com.example.MyShop_API.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Uncategorized error", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1003, "User not existed", HttpStatus.NOT_FOUND),
    USER_INVALID(1004, "Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    INVALID_PASSWORD(1005, "PASSWORD must be at least {min} characters", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1006, "You do not have permission", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1007, "UNAUTHENTICATED", HttpStatus.UNAUTHORIZED),
    ADDRESS_NOT_EXISTED(1008, "Address not existed", HttpStatus.NOT_FOUND),
    ADDRESS_EXISTED(1009, "Address existed", HttpStatus.BAD_REQUEST),
    CART_NOT_EXISTED(1010, "Cart not existed", HttpStatus.NOT_FOUND),
    CART_EXISTED(1010, "Cart existed", HttpStatus.BAD_REQUEST),
    ROLE_NOT_ALLOWED(1011, "Role not allowed", HttpStatus.NOT_FOUND),
    PROFILE_EXISTED(1012, "Profile existed", HttpStatus.BAD_REQUEST),
    PRODUCT_NOT_EXISTED(1013, "Product not existed", HttpStatus.NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1014, "The product quantity is out of stock.", HttpStatus.BAD_REQUEST),
    PRODUCT_IS_NOT_ENOUGH(1014, "The product quantity is out of stock: Requested: %d, Available: %d", HttpStatus.BAD_REQUEST),
    CATEGORY_NOT_EXISTED(1015, "Category not existed", HttpStatus.NOT_FOUND),
    CATEGORY_EXISTED(1016, "Category existed", HttpStatus.BAD_REQUEST),
    PAYMENT_EXISTED(1017, "Payment existed", HttpStatus.BAD_REQUEST),
    PAYMENT_NOT_EXISTED(1018, "Payment not existed", HttpStatus.NOT_FOUND),
    ORDER_ITEM_NOT_EXISTED(1018, "OrderItem not existed", HttpStatus.NOT_FOUND),
    ORDER_ITEM_EXISTED(1018, "OrderItem existed", HttpStatus.BAD_REQUEST),
    ORDER_EXISTED(1019, "Order existed", HttpStatus.BAD_REQUEST),
    ORDER_NOT_EXISTED(1020, "Order not existed", HttpStatus.NOT_FOUND),
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
