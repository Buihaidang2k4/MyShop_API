package com.example.MyShop_API.service.product;

import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.ProductMapper;
import com.example.MyShop_API.repo.*;
import com.example.MyShop_API.service.inventory.IInventoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService implements IProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;
    CartItemRepository cartItemRepository;
    IInventoryService inventoryService;
    OrderItemRepository orderItemRepository;

    @Transactional
    public Product addProduct(AddProductRequest request) {

        // Tìm danh mục , nếu không có tạo mới
        Category category = Optional.ofNullable(categoryRepository.findByCategoryName(request.getCategory().getCategoryName()))
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .categoryName(request.getCategory().getCategoryName())
                            .description(request.getCategory().getDescription())
                            .createAt(LocalDate.now())
                            .build();
                    return categoryRepository.save(newCategory);
                });

        // mapper
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setCreateAt(LocalDate.now());
        product.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));

        // Lưu khởi tạo id
        product = productRepository.save(product);

        // Khởi tạo tồn kho cho product
        inventoryService.initializeInventory(product, request.getQuantity());

        return product;
    }

    @Transactional
    public Product updateProduct(AddProductRequest request, Long productId) {
        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        // Check category
        Category category = categoryRepository.findByCategoryName(request.getCategory().getCategoryName());
        if (category == null)
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);

        findProduct.setCategory(category);
        findProduct.setUpdateAt(LocalDate.now());

        productMapper.update(request, findProduct);

        // Cập nhật tồn kho
        inventoryService.updateStock(productId, request.getQuantity());

        // Tính lại giá sản phẩm đặc biệt nếu có mã giảm giá
        findProduct.setSpecialPrice(calculateSpecialPrice(request.getPrice(), request.getDiscount()));

        return productRepository.save(findProduct);
    }

    @Transactional
    public void deleteProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );

        if (orderItemRepository.existsByProductProductId(productId)) {
            throw new AppException(ErrorCode.PRODUCT_HAS_ORDERS);
        }

        cartItemRepository.deleteCartItemByProductProductId(productId);
        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> getProducts() {
        log.info("=======getProducts======== ");
        return productRepository.findAll();
    }

    // ============== GET ALL ===================
    @Override
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Product getProductById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        return product;
    }

    // ============ GET ALL BY PRODUCT NAME =============
    @Override
    public Product getProductByName(String productName) {
        Product product = productRepository.getProductByProductName(productName);
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        return product;
    }

    // ============== GET ALL BY CATEGORY NAME ============
    @Override
    public Page<Product> searchProductByCategory(String categoryName, Pageable pageable) {
        log.info("searchByCategory ");

        Category findCategory = categoryRepository.getCategoriesByCategoryName(categoryName);
        if (findCategory == null)
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);

        return productRepository.findByCategory_CategoryName(categoryName, pageable);
    }


    private BigDecimal calculateSpecialPrice(BigDecimal price, BigDecimal discount) {
        return price.multiply(BigDecimal.ONE.subtract(discount.divide(BigDecimal.valueOf(100))));
    }
}
