package com.example.MyShop_API.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileService {

    String uploadImage(String path, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(originalFilename.substring(originalFilename.lastIndexOf('.')));

        // tạo thư mục nếu chưa tồn tại
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs(); // tạo cả cây thư mục nếu cần
        }

        // đường dẫn đầy đủ tới file
        String filePath = path + File.separator + fileName;

        // copy file vào thư mục
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }


    InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String filePath = path + File.separator + fileName;
        InputStream inputStream = new FileInputStream(filePath);
        return inputStream;
    }
}
