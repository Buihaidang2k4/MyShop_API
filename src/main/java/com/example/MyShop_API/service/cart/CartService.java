package com.example.MyShop_API.service.cart;

import com.example.MyShop_API.dto.request.CartRequest;
import com.example.MyShop_API.dto.response.CartResponse;
import com.example.MyShop_API.entity.Cart;
import com.example.MyShop_API.entity.CartItem;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.entity.UserProfile;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.CartMapper;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.repo.CartRepository;
import com.example.MyShop_API.repo.ProductRepository;
import com.example.MyShop_API.repo.UserProfileRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService implements ICartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    UserProfileRepository userProfileRepository;
    ProductRepository productRepository;
    CartItemRepository cartItemRepository;

    @Override
    public List<CartResponse> getCarts() {
        log.info("getCarts ");
        return cartRepository.findAll().stream().map(cartMapper::toResponse).collect(Collectors.toList());
    }

    @Override
    public CartResponse getCartById(Long cartId) {
        log.info("getCartById ");
        return cartRepository.findById(cartId).map(cartMapper::toResponse).orElseThrow(
                () -> new AppException(ErrorCode.CART_NOT_EXISTED)
        );
    }

    @Override
    public Cart getCartByUserProfileId(Long userProfileId) {
        return cartRepository.findByUserProfileId(userProfileId);
    }

    @Override
    public CartResponse addCartForUserProfile(CartRequest cartRequest, Long userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart newCart = cartMapper.toEntity(cartRequest);
        newCart.setUserProfile(userProfile);
        newCart = cartRepository.save(newCart);
        return cartMapper.toResponse(newCart);
    }

    @Override
    public CartResponse addProductToCart(Long cardId, Long productId, Integer quantity) {
        Cart findCart = cartRepository.findById(cardId).orElseThrow(()
                -> new AppException(ErrorCode.CART_NOT_EXISTED));

        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(productId, cardId);

        // Kiem tra card da ton tai chua
        if (cartItem != null) {
            throw new AppException(ErrorCode.CART_EXISTED);
        }

        // kiem tra so luong ton kho
        if (findProduct.getQuantity() == 0)
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK);

        if (findProduct.getQuantity() < quantity) {
            throw new AppException(ErrorCode.PRODUCT_OUT_OF_STOCK, findProduct.getQuantity(), quantity);
        }

        CartItem newCartItem = CartItem.builder()
                .product(findProduct)
                .cart(findCart)
                .quantity(quantity)
                .unitPrice(findProduct.getPrice())
                .build();

        newCartItem.setTotalPrice();
        cartItemRepository.save(newCartItem);

        // cap nhat tong gia cua gio hang
        findCart.getCartItems().add(newCartItem);
        findCart.updateTotalAmout();
        cartRepository.save(findCart);

        return cartMapper.toResponse(findCart);
    }

    @Override
    public CartResponse updateCart(Long cartId, CartRequest cartRequest) {
        log.info("updateCartById ");
        Cart findCart = cartRepository.findById(cartId).orElseThrow(
                () -> new AppException(ErrorCode.CART_NOT_EXISTED)
        );

        cartMapper.update(cartRequest, findCart);
        return cartMapper.toResponse(findCart);
    }

    @Transactional
    @Override
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
        cartItemRepository.deleteAllByCart_CartId(cartId);
        cart.getCartItems().clear();
        cartRepository.deleteById(cartId);
    }


}
