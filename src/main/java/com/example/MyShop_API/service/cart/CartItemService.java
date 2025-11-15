package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.CartItem;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.repo.CartRepository;
import com.example.MyShop_API.service.inventory.IInventoryService;
import com.example.MyShop_API.service.product.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;


@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartItemService implements ICartItemService {
    CartItemRepository cartItemRepository;
    CartRepository cartRepository;
    ICartService cartService;
    IProductService productService;
    IInventoryService inventoryService;

    @Transactional
    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);

        // Tìm cartItem nếu đã tồn tại
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        // Tạo mới item
        if (cartItem == null) {
            // Check tồn kho xem đủ số lượng đặt không nếu có đặt trước trong kho
            boolean reserved = inventoryService.reserveStock(productId, quantity);
            if (!reserved)
                throw new AppException(ErrorCode.INVENTORY_NOT_ENOUGH);

            // Tạo mới
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();

            cartItem.setTotalPrice();
            cart.addItem(cartItem);
        } else {
            // Phần tăng
            int additionalQuantity = quantity;
            boolean reserved = inventoryService.reserveStock(productId, additionalQuantity);
            if (!reserved)
                throw new AppException(ErrorCode.INVENTORY_NOT_ENOUGH);

            // Cập nhật số lượng
            cartItem.setQuantity(cartItem.getQuantity() + additionalQuantity);
            cartItem.setTotalPrice();
        }

        // Cập nhật tổng giá
        updateCartTotalPrice(cart);
        cartRepository.save(cart);
    }

    @Transactional
    @Override
    public void updateItemQuantity(Long cartId, Long cartItemId, int newQuantity) {
        Cart cart = cartService.getCartById(cartId);

        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));

        int oldQuantity = cartItem.getQuantity();

        if (newQuantity > oldQuantity) {
            // Tăng thêm
            int diff = newQuantity - oldQuantity;
            // Đặt trước thêm
            boolean reserved = inventoryService.reserveStock(cartItem.getProduct().getProductId(), diff);
            if (!reserved)
                throw new AppException(ErrorCode.INVENTORY_NOT_ENOUGH, cartItem.getProduct().getInventory().getAvailable(), newQuantity);
        } else if (newQuantity < oldQuantity) {
            int diff = oldQuantity - newQuantity;
            // Hủy vợi đơn
            inventoryService.cancelReservation(cartItem.getProduct().getProductId(), diff);
        }

        // cập nhật số lượng mới cho giỏ hàng
        cartItem.setQuantity(newQuantity);
        cartItem.setTotalPrice();

        // Cập nhật tổng giá
        updateCartTotalPrice(cart);

        cartRepository.save(cart);
    }


    @Transactional
    @Override
    public void removeItemFromCart(Long cartId, Long cartItemId) {
        Cart cart = cartService.getCartById(cartId);
        CartItem itemToRemove = getCartItem(cartId, cartItemId);

        // Trả kho
        inventoryService.cancelReservation(itemToRemove.getProduct().getProductId(), itemToRemove.getQuantity());

        // xóa khỏi giỏ
        cart.removeItem(itemToRemove);
        cartItemRepository.delete(itemToRemove);

        // Cập nhật tổng giá
        updateCartTotalPrice(cart);

        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long cartItemId) {
        Cart cart = cartService.getCartById(cartId);

        return cart.getCartItems()
                .stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst().orElseThrow(() -> new AppException(ErrorCode.CART_ITEM_NOT_EXISTED));
    }

    private void updateCartTotalPrice(Cart cart) {
        // Cong tong gia tien
        BigDecimal totalPrice = cart.getCartItems()
                .stream().map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(totalPrice);
    }

}
