package com.example.MyShop_API.service;

import com.example.MyShop_API.dto.CategoryResponse;
import com.example.MyShop_API.dto.ProductRequest;
import com.example.MyShop_API.dto.ProductResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.ProductMapper;
import com.example.MyShop_API.repo.CategoryRepository;
import com.example.MyShop_API.repo.ProductRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ProductService {
    ProductRepository productRepository;
    ProductMapper productMapper;
    CategoryRepository categoryRepository;

    public List<ProductResponse> getProducts() {
        log.info("getProducts ");
        return productRepository.findAll().stream().map(productMapper::toResponse).collect(Collectors.toList());
    }

    public ProductResponse getProduct(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));
        return productMapper.toResponse(product);
    }

    public List<ProductResponse> searchProductByCategory(String categoryName) {
        log.info("searchByCategory ");
        Category findCategory = categoryRepository.getCategoriesByCategoryName(categoryName);
        if (findCategory == null)
            throw new AppException(ErrorCode.CATEGORY_NOT_EXISTED);

        var listProduct = productRepository.findAllByCategoryCategoryName(categoryName);

        return listProduct.stream().map(productMapper::toResponse).collect(Collectors.toList());
    }

    public ProductResponse addProduct(ProductRequest productRequest, Long categoryId) {
        Category findCategory = categoryRepository.findById(categoryId).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));

        Product product = productMapper.toEntity(productRequest);
        product.setCategory(findCategory);
        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    public ProductResponse updateProduct(ProductRequest productRequest, Long productId) {
                
    }
}
