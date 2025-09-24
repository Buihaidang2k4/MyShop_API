package com.example.MyShop_API.service;

import com.example.MyShop_API.dto.request.ProductRequest;
import com.example.MyShop_API.dto.response.ProductResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    FileService fileService;
    CartItemRepository cartItemRepository;

    @NonFinal
    @Value("${project.image}")
    String path;


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

        // cap nhat gia dac biet
        var specialPrice = product.getPrice() - (product.getPrice() * (product.getDiscount() * 0.01));
        product.setSpecialPrice(specialPrice);

        productRepository.save(product);
        return productMapper.toResponse(product);
    }

    public ProductResponse updateProduct(ProductRequest productRequest, Long productId) {
        Product findProduct = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        productMapper.update(productRequest, findProduct);

        // cap nhat danh muc
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(
                () -> new AppException(ErrorCode.CATEGORY_NOT_EXISTED));
        findProduct.setCategory(category);

//        cap nhat gia
        var specialPrice = findProduct.getPrice() - (findProduct.getDiscount() * 0.01 * findProduct.getPrice());
        findProduct.setSpecialPrice(specialPrice);

        findProduct = productRepository.save(findProduct);
        return productMapper.toResponse(findProduct);
    }

    public ProductResponse updateProductImage(Long productId, MultipartFile image) throws IOException {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED));

        String fileName = fileService.uploadImage(path, image);

        product.setImage(fileName);
        product = productRepository.save(product);

        return productMapper.toResponse(product);
    }

    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(
                () -> new AppException(ErrorCode.PRODUCT_NOT_EXISTED)
        );
        cartItemRepository.deleteCartItemByProductProductId(productId);
        productRepository.deleteById(productId);
    }
}
