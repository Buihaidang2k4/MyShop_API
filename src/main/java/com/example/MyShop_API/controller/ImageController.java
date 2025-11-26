package com.example.MyShop_API.controller;


import com.example.MyShop_API.dto.request.ImageDTO;
import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.Image;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.service.Image.IImageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("${api.prefix}/images")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ImageController {
    IImageService imageService;

    @PostMapping("/upload/product/{productId}")
    ResponseEntity<ApiResponse> saveImages(@RequestPart("files") List<MultipartFile> files, @PathVariable("productId") Long productId) {
        try {
            List<ImageDTO> imageDTOs = imageService.saveImage(productId, files);


            return ResponseEntity.ok(new ApiResponse(200, "Upload success!", imageDTOs));
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Upload failed!", null));
        }
    }

    @GetMapping("/image/download/{imageId}")
    ResponseEntity<Resource> downloadImage(@PathVariable Long imageId) throws SQLException {
        Image image = imageService.getImageById(imageId);
        ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                .body(resource);
    }

    @GetMapping("/product/{productId}/images")
    ResponseEntity<ApiResponse> getImagesByProductId(@PathVariable Long productId) {
        List<ImageDTO> ds = imageService.getImageByProductId(productId);
       
        return ResponseEntity.ok(new ApiResponse(200, "Get success!", ds));
    }

    @PutMapping("/image/{imageId}/update")
    ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestPart("file") MultipartFile file) throws SQLException, IOException {
        try {
            Image image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.updateImage(imageId, file);
                return ResponseEntity.ok(new ApiResponse(200, "Update success!", null));
            }
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "Update failed!", null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Update failed!", null));
    }

    @DeleteMapping("/image/{imageId}/delete")
    ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId) {
        try {
            Image image = imageService.getImageById(imageId);
            if (image != null) {
                imageService.deleteImageById(imageId);
                return ResponseEntity.ok(new ApiResponse(200, "Delete success!", null));
            }
        } catch (AppException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(404, "Delete failed!", null));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(500, "Delete failed!", null));
    }
}
