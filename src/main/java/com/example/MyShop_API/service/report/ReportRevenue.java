package com.example.MyShop_API.service.report;

import com.example.MyShop_API.dto.request.*;
import com.example.MyShop_API.service.report.revenue_report.IRevenueReport;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReportRevenue implements IRevenueReport {


    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue(int year) {
        return List.of();
    }

    @Override
    public List<QuarterlyRevenueDTO> getQuarterlyRevenue(int year) {
        return List.of();
    }

    @Override
    public List<AnnualRevenueDTO> getAnnualRevenue(int fromYear, int toYear) {
        return List.of();
    }

    @Override
    public List<ProductRevenueDTO> getRevenueByProduct(int year, int month) {
        return List.of();
    }

    @Override
    public List<CategoryRevenueDTO> getRevenueByCategory(int year, int month) {
        return List.of();
    }
}
