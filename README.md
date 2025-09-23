# 🛒 MyShop_API

MyShop_API là một RESTful API được xây dựng bằng Spring Boot để phục vụ cho hệ thống thương mại điện tử. Dự án hỗ trợ
quản lý người dùng, sản phẩm, đơn hàng, giỏ hàng, thanh toán và xác thực bảo mật.

## 🚀 Công nghệ sử dụng

- Java 17
- Spring Boot 3.3.5
- Spring Security & OAuth2
- Spring Data JPA
- MySQL
- MapStruct
- Lombok
- Swagger (SpringDoc OpenAPI)
- Dotenv (quản lý biến môi trường)

## 📦 Cài đặt

```bash
git clone https://github.com/Buihaidang2k4/MyShop_API.git
cd myshop-api
./mvnw clean install
```

## ⚙️ Cấu hình

Tạo file .env trong thư mục gốc để cấu hình biến môi trường

```bash

DB_URL=jdbc:mysql://localhost:3306/myshop
DB_USERNAME=root
DB_PASSWORD=yourpassword
JWT_SECRET=your_jwt_secret
```

## API documentation

* API documentation is available via Swagger UI at http://localhost:8080/swagger-ui/index.html

## 📚 API Controllers(Swagger-ui)

![img_1.png](img_1.png)
![img_2.png](img_2.png)
![img_3.png](img_3.png)
![img_4.png](img_4.png)
![img_5.png](img_5.png)
![img_7.png](img_7.png)
![img_6.png](img_6.png)
![img_8.png](img_8.png)
![img_9.png](img_9.png)
![img_10.png](img_10.png)
![img_11.png](img_11.png)

## ER-Diagram

![img.png](img.png)
