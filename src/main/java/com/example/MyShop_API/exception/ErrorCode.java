package com.example.MyShop_API.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorCode {

    // ========== SYSTEM ==========
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized error", INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Invalid key", BAD_REQUEST),
    REDIS_ERROR(1028, "Redis error", INTERNAL_SERVER_ERROR),

    // ========== AUTH ==========
    UNAUTHENTICATED(1001, "Authentication required", HttpStatus.UNAUTHORIZED),           // 401
    UNAUTHORIZED(1002, "You do not have permission", FORBIDDEN),              // 403
    TOKEN_EXPIRED(1003, "Token expired", HttpStatus.UNAUTHORIZED),
    TOKEN_REVOKED(1004, "Token has been revoked or is no longer valid", HttpStatus.UNAUTHORIZED),
    ROLE_NOT_ALLOWED(1005, "Role is not allowed for this action", FORBIDDEN),

    // ========== USER ==========
    USER_EXISTED(1100, "User already exists", BAD_REQUEST),
    USER_NOT_EXISTED(1101, "User not found", NOT_FOUND),
    USER_INVALID(1102, "Username must be at least {min} characters", BAD_REQUEST),
    INVALID_PASSWORD(1103, "Password must be at least {min} characters", BAD_REQUEST),
    INVALID_CREDENTIALS(1104, "Incorrect username or password", HttpStatus.UNAUTHORIZED),

    // ========== PROFILE ==========
    PROFILE_EXISTED(1200, "Profile already exists", BAD_REQUEST),
    PROFILE_NOT_EXISTED(1201, "Profile not exists", BAD_REQUEST),

    // ========== ADDRESS ==========
    ADDRESS_NOT_EXISTED(1300, "Address not found", NOT_FOUND),
    ADDRESS_EXISTED(1301, "Address already exists", BAD_REQUEST),

    // ========== PRODUCT ==========
    PRODUCT_NOT_EXISTED(1400, "Product not found", NOT_FOUND),
    PRODUCT_OUT_OF_STOCK(1401, "Product is out of stock", BAD_REQUEST),
    PRODUCT_NOT_ENOUGH(1402, "Requested: %d, Available: %d", BAD_REQUEST),
    PRODUCT_HAS_ORDERS(1403, "Product has orders and cannot be deleted", CONFLICT),

    // ========== CATEGORY ==========
    CATEGORY_NOT_EXISTED(1500, "Category not found", NOT_FOUND),
    CATEGORY_EXISTED(1501, "Category already exists", BAD_REQUEST),

    // ========== PAYMENT ==========
    PAYMENT_EXISTED(1600, "Payment already exists", BAD_REQUEST),
    PAYMENT_NOT_EXISTED(1601, "Payment not found", NOT_FOUND),
    PAYMENT_METHOD_NOT_SUPPORT(1602, "Payment method not support", NOT_FOUND),
    PAYMENT_FAILED(1602, "Process payment failed", NOT_FOUND),
    PAYMENT_DECLINED(1602, "Payment declined", NOT_FOUND),

    // ========== CART ==========
    CART_NOT_EXISTED(1700, "Cart not found", NOT_FOUND),
    CART_EXISTED(1701, "Cart already exists", BAD_REQUEST),
    CART_ITEM_NOT_EXISTED(1702, "Cart item not found", NOT_FOUND),
    CART_ITEM_EXISTED(1703, "Cart item already exists", BAD_REQUEST),
    CART_NOT_MATCH(1704, "Cart does not match with cart item", BAD_REQUEST),
    CART_EMPTY(1704, "Your cart is empty", BAD_REQUEST),

    // ========== ORDER ==========
    ORDER_EXISTED(1800, "Order already exists", BAD_REQUEST),
    ORDER_NOT_EXISTED(1801, "Order not found", NOT_FOUND),
    ORDER_ITEM_NOT_EXISTED(1802, "Order item not found", NOT_FOUND),
    ORDER_ITEM_EXISTED(1803, "Order item already exists", BAD_REQUEST),
    ORDER_CANCEL_FAILED(1804, "Cannot cancel shipped or delivered orders", BAD_REQUEST),
    ORDER_ITEM_EMPTY(1805, "Order item is empty", BAD_REQUEST),
    ORDER_ALREADY_DELIVERED(1806, "The order has been delivered but cannot be confirmed.", BAD_REQUEST),
    ORDER_ALREADY_CANCELLED(1807, "The order has been cancelled but cannot be update status.", BAD_REQUEST),
    ORDER_STATUS_FINAL(1808, "Unable to change completed order", BAD_REQUEST),

    // ========== INVENTORY ==========
    INVENTORY_DOES_NOT_EXIST(1900, "Inventory does not exist", NOT_FOUND),
    INVENTORY_NOT_ENOUGH(1901, "Not enough stock:  available: %d  , requestQuantity: %d", BAD_REQUEST),
    CANNOT_CANCEL_RESERVATION(1902, "Cannot cancel this reservation", BAD_REQUEST),
    NOT_ENOUGH_RESERVED_STOCK(1903, "Not enough reserved stock", BAD_REQUEST),

    // ========== IMAGE ==========
    IMAGE_NOT_FOUND(2000, "Image not found id: %d", NOT_FOUND),

    // ========== VALIDATION ==========
    VALIDATION_ERROR(2100, "Input data is not valid", BAD_REQUEST),

    // ========== COUPON ==========
    COUPON_INVALID(2200, "This coupon is invalid or cannot be applied to your order.", BAD_REQUEST),
    COUPON_LIMIT_PER_USER_EXCEEDED(2200, "Coupon limit per user exceeded", BAD_REQUEST),

    // =========== REVIEW =================
    REVIEW_NOT_PURCHASED(2300, "You cannot review this item because you haven’t purchased it.", BAD_REQUEST),
    REVIEW_ALREADY_EXISTS(2301, "Review already exists", BAD_REQUEST);

    final int code;
    final String messageTemplate;
    final HttpStatus httpStatus;

    ErrorCode(int code, String messageTemplate, HttpStatus httpStatus) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.httpStatus = httpStatus;
    }
}
