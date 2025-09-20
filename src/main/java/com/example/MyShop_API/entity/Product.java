package com.example.MyShop_API.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long productId;

    @NotBlank
    @Size(min = 3, message = "Product name must contain least 3 char")
    String productName;

    String image;

    @NotBlank
    @Size(min = 6, message = "Product description must contain at least 6 characters")
    String description;

    Integer quantity;
    double price;
    double discount;
    double specialPrice;

    @ManyToOne
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    List<OrderItem> orderItems = new ArrayList<>();
}
