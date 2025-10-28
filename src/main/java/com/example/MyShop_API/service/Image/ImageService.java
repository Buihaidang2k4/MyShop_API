package com.example.MyShop_API.service.Image;

import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.entity.Image;
import com.example.MyShop_API.entity.Product;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.ImageMapper;
import com.example.MyShop_API.repo.ImageRepository;
import com.example.MyShop_API.service.product.ProductService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageService implements IImageService {
    ImageRepository imageRepository;
    ProductService productService;
    ImageMapper imageMapper;

    @Override
    public Image getImageById(Long id) {
        return imageRepository.findById(id).orElseThrow(() ->
                new AppException(ErrorCode.IMAGE_NOT_FOUND));
    }

    @Override
    public List<ImageDTO> getImageByProductId(Long productId) {
        List<Image> ds = imageRepository.findImageByProductId(productId);
        if (ds.isEmpty()) throw new AppException(ErrorCode.IMAGE_NOT_FOUND);

        return ds.stream().map(imageMapper::toImageDTO).toList();
    }

    @Override
    public void deleteImageById(Long id) {
        imageRepository.findById(id).ifPresentOrElse(imageRepository::delete, () -> {
            throw new AppException(ErrorCode.IMAGE_NOT_FOUND, id);
        });
    }

    @Override
    public List<ImageDTO> saveImage(Long productId, List<MultipartFile> files) {
        Product product = productService.getProductById(productId);

        List<ImageDTO> saveImageDTO = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                Image image = Image.builder()
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .image(new SerialBlob(file.getBytes()))
                        .product(product)
                        .build();

                String buildDownLoadUrl = "api/v1/images/image/download/";
                String downloadUrl = buildDownLoadUrl + image.getId();
                image.setDownloadUrl(downloadUrl);

                Image savedImage = imageRepository.save(image);
                savedImage.setDownloadUrl(downloadUrl + savedImage.getId());
                imageRepository.saveAndFlush(savedImage);

                ImageDTO imageDTO = imageMapper.toImageDTO(savedImage);
                saveImageDTO.add(imageDTO);


            } catch (IOException | SQLException e) {
                throw new RuntimeException(e.getMessage());
            }

        }
        return saveImageDTO;
    }

    @Override
    public void updateImage(Long productId, MultipartFile file) {
        try {
            Image image = getImageById(productId);
            image.setFileName(file.getOriginalFilename());
            image.setFileType(file.getContentType());
            image.setImage(new SerialBlob(file.getBytes()));
            imageRepository.save(image);
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
