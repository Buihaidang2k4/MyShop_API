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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CartService implements ICartService {
    CartRepository cartRepository;
    CartMapper cartMapper;
    UserProfileRepository userProfileRepository;
    CartItemRepository cartItemRepository;
    AtomicLong generatorId = new AtomicLong(0);

    /**
     * Get all cart
     *
     * @return {@link List<Cart>}
     */
    @Override
    public List<Cart> getCarts() {
        log.info("getCarts ");
        List<Cart> carts = cartRepository.findAll();
        return carts;
    }

    /**
     * Get cart by page
     *
     * @param pageable
     * @return
     */
    @Override
    public Page<Cart> getAllCarts(Pageable pageable) {
        return cartRepository.findAll(pageable);
    }

    /**
     * Get card by cardId
     *
     * @param cartId
     * @return cart
     */
    @Override
    public Cart getCartById(Long cartId) {
        return cartRepository.findByIdWithItems(cartId)
                .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_EXISTED));
    }

    /**
     * Get cart by userprofileId
     *
     * @param userProfileId
     * @return cart
     */
    @Override
    public Cart getCartByUserProfileId(Long userProfileId) {
        return cartRepository.findByUserProfileId(userProfileId);
    }

    /**
     * Get total price
     *
     * @param id
     * @return
     */
    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCartById(id);
        return cart.getTotalPrice();
    }

    /**
     * Create new cart
     *
     * @return cartId
     */
    @Override
    public Long initializeNewCart() {
        Cart newCart = new Cart();
        Long cartId = generatorId.incrementAndGet();
        newCart.setCartId(cartId);
        return cartRepository.save(newCart).getCartId();
    }

    /**
     * add cart -> User
     *
     * @param cartRequest
     * @param userProfileId
     * @return cart
     */
    @Override
    public CartResponse addCartForUserProfile(CartRequest cartRequest, Long userProfileId) {
        UserProfile userProfile = userProfileRepository.findById(userProfileId).orElseThrow(()
                -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Cart newCart = cartMapper.toEntity(cartRequest);
        newCart.setUserProfile(userProfile);
        newCart = cartRepository.save(newCart);
        return cartMapper.toResponse(newCart);
    }

    /**
     * update card by cardId, and body cartRequest
     *
     * @param cartId
     * @param cartRequest
     * @return
     */
    @Override
    public CartResponse updateCart(Long cartId, CartRequest cartRequest) {
        log.info("updateCartById ");
        Cart findCart = cartRepository.findById(cartId).orElseThrow(
                () -> new AppException(ErrorCode.CART_NOT_EXISTED)
        );

        cartMapper.update(cartRequest, findCart);
        return cartMapper.toResponse(findCart);
    }

    /**
     * clear cart
     *
     * @param cartId
     */
    @Transactional
    @Override
    public void clearCart(Long cartId) {
        Cart cart = getCartById(cartId);
        cartItemRepository.deleteAllByCart_CartId(cartId);
        cart.getCartItems().clear();
        cartRepository.deleteById(cartId);
    }


}
