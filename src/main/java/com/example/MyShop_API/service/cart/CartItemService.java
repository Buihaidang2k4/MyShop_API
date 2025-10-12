package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.CartItem;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.repo.CartRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.service.product.IProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        Product product = productService.getProductById(productId);

        // Tìm sản phẩm có productId khớp thì lấy ra, nếu không tìm thấy ,tạo mới một CartItem
        CartItem cartItem = cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getProductId().equals(productId))
                .findFirst().orElse(null);

        if (cartItem == null) {
            cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .unitPrice(product.getPrice())
                    .build();
        } else {
            // Neu san pham da co tang so luong
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        // them cartitem vao gio hang
        cartItem.setCart(cart);
        cartItem.setTotalPrice();
        cart.addItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCart(Long cartId, Long cartItemId) {
        Cart cart = cartService.getCartById(cartId);
        CartItem itemToRemove = getCartItem(cartId, cartItemId);

        cart.removeItem(itemToRemove);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long cartItemId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        cart.getCartItems().stream()
                .filter(item -> item.getCartItemId().equals(cartItemId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getUnitPrice());
                    item.setTotalPrice();
                });

        // Cong tong gia tien
        BigDecimal totalPrice = cart.getCartItems()
                .stream().map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalPrice(totalPrice);
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

}
