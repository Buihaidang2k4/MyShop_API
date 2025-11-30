package com.example.MyShop_API.controller;

import com.example.MyShop_API.service.report.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("${api.prefix}/demo-report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    private final TemplateEngine templateEngine;

    @Value("${report.temp-dir}")
    private String tempDir;

    @PostMapping("/report-order/{orderId}")
    public ResponseEntity<byte[]> generatePdfOrder(@PathVariable Long orderId) throws IOException {
        String html = reportService.getReportHtml(orderId);
        byte[] pdfBytes = reportService.convertHtmlToPdf(html);

        // Tạo tên file động theo thời gian hoặc ID
        String fileName = "report-" + System.currentTimeMillis() + ".pdf";
        Path filePath = Paths.get(tempDir, fileName);
        // Lưu xuống thư mục tempDir
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, pdfBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=document.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
