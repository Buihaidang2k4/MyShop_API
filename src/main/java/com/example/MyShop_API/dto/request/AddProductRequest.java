package com.example.MyShop_API.dto.request;

import com.example.MyShop_API.entity.Category;
import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AddProductRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 7, max = 200, message = "Tên sản phẩm tối thiểu 7 ký tự và tối đa 200 ký tự")
    String productName;

    @Size(max = 100, message = "Xuất xứ tối đa 100 ký tự")
    String origin;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    String description;

    @NotNull(message = "Danh mục không được để trống")
    Category category;

    @NotNull(message = "Số lượng không được để trống")
    @PositiveOrZero(message = "Số lượng phải >= 0")
    Integer quantity;

    @NotNull(message = "Giá sản phẩm không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    BigDecimal price;

    @DecimalMin(value = "0.0", inclusive = true, message = "Giảm giá phải >= 0")
    @DecimalMax(value = "100.0", inclusive = true, message = "Giảm giá phải <= 100")
    BigDecimal discount; // % giảm giá, có thể null

    Integer soldCount = 0;
    Integer reviewCount = 0;
    Double avgRating = 0.0;
}
