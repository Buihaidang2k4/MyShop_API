package com.example.MyShop_API.service.Image;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface IImageService {
    Image getImageById(Long id);

    void deleteImageById(Long id);

    List<ImageDTO> saveImage(Long productId, List<MultipartFile> files);

    void updateImage(Long productId, MultipartFile file) throws IOException, SQLException;
}
