package com.example.MyShop_API.service.product;

import com.example.MyShop_API.dto.request.AddProductRequest;
import com.example.MyShop_API.dto.response.ProductResponse;
import com.example.MyShop_API.entity.Category;
import com.example.MyShop_API.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    List<Product> getProducts();

    Product getProductById(Long id);

    Product getProductByName(String productName);

    List<Product> searchProductByCategory(String category);

    Product addProduct(AddProductRequest addProductRequest);

    Product updateProduct(AddProductRequest addProductRequest, Long productId);

    void deleteProductById(Long productId);
}
