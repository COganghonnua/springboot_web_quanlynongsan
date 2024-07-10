package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Order;
import com.example.Buoi2.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/chart")
public class ChartController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/revenue")
    public String getRevenueChart(Model model) {
        List<Order> orders = orderRepository.findAll();

        // Tính tổng doanh thu theo từng tháng
        Map<String, Double> revenueByMonth = orders.stream()
                .collect(Collectors.groupingBy(
                        order -> new SimpleDateFormat("yyyy-MM").format(order.getOrderDate()),
                        Collectors.summingDouble(Order::getTotalAmount)
                ));

        // Format doanh thu thành chuỗi để tránh lỗi NumberFormatException
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        Map<String, String> formattedRevenueByMonth = revenueByMonth.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> decimalFormat.format(entry.getValue())
                ));

        model.addAttribute("revenueByMonth", formattedRevenueByMonth);
        model.addAttribute("orders", orders);

        return "chart/revenue";
    }
}
