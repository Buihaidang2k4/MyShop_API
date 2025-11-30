package com.example.MyShop_API.controller;

import com.example.MyShop_API.dto.response.ApiResponse;
import com.example.MyShop_API.entity.Order;
import com.example.MyShop_API.repo.OrderRepository;
import com.example.MyShop_API.service.report.ReportExcelService;
import com.example.MyShop_API.service.report.ReportPdfService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("${api.prefix}/report")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportController {
    ReportPdfService reportPdfService;
    ReportExcelService reportExcelService;
    OrderRepository orderRepository;

    @NonFinal
    @Value("${report.temp-dir}")
    private String tempDir;

    @PostMapping("/report-order-pdf/{orderId}")
    public ResponseEntity<byte[]> generatePdfOrder(@PathVariable Long orderId) throws IOException {
        String html = reportPdfService.getReportHtml(orderId);
        byte[] pdfBytes = reportPdfService.convertHtmlToPdf(html);

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

    @GetMapping("/export/orders")
    public ResponseEntity<ApiResponse> exportOrders() throws IOException {
        List<Order> orders = orderRepository.findAll();
        reportExcelService.exportOrders(orders);

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Xuất file Excel thành công",
                null
        );
        return ResponseEntity.ok(response);
    }

}
