package com.example.MyShop_API.service.product;

import com.example.MyShop_API.anotation.AllAccess;
import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.ProductMapper;
import com.example.MyShop_API.repo.CartItemRepository;
import com.example.MyShop_API.repo.CategoryRepository;
import com.example.MyShop_API.repo.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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


    public Product addProduct(AddProductRequest request) {
        Category category = Optional.ofNullable(categoryRepository.findByCategoryName(request.getCategory().getCategoryName()))
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .categoryName(request.getCategory().getCategoryName())
                            .build();
                    return categoryRepository.save(newCategory);
                });
        request.setCategory(category);

        // Chuyen doi
        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setSpecialPrice(
                request.getPrice()
                        .multiply(BigDecimal.ONE.subtract(request.getDiscount().divide(BigDecimal.valueOf(100)))));

        return productRepository.save(product);
    }

    public Product updateProduct(AddProductRequest addProductRequest, Long productId) {
        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        productMapper.update(addProductRequest, findProduct);

        Category category = categoryRepository.findByCategoryName(addProductRequest.getCategory().getCategoryName());
        if (category == null) {
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);
        }
        findProduct.setCategory(category);

        Product updatedProduct = productRepository.save(findProduct);
        return updatedProduct;
    }

    public void deleteProductById(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );
        cartItemRepository.deleteCartItemByProductProductId(productId);
        productRepository.deleteById(productId);
    }

    @Override
    public List<Product> getProducts() {
        log.info("getProducts ");
        return productRepository.findAll();
    }

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

    @Override
    public Product getProductByName(String productName) {
        Product product = productRepository.getProductByProductName(productName);
        if (product == null) {
            throw new AppException(ErrorCode.PRODUCT_NOT_EXISTED);
        }
        return product;
    }

    @Override
    public List<Product> searchProductByCategory(String category) {
        log.info("searchByCategory ");
        Category findCategory = categoryRepository.getCategoriesByCategoryName(category);
        if (findCategory == null)
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);

        return productRepository.findAllByCategory((findCategory));
    }


}
