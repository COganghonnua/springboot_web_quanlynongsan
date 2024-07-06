package com.example.Buoi2.services;

import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Discount;
import com.example.Buoi2.entity.Order;
import com.example.Buoi2.entity.OrderDetail;
import com.example.Buoi2.repository.OrderRepository;
import com.example.Buoi2.repository.OrderDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final DiscountService discountService;
    private final ProductService productService; // Thêm dòng này để inject ProductService

    public Order createOrder(String customerName, String shippingAddress, String phoneNumber, String email, String notes, List<CartItem> cartItems, String discountCode) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNotes(notes);

        double totalAmount = 0;
        for (CartItem item : cartItems) {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrder(order);
            orderDetail.setProduct(item.getProduct());
            orderDetail.setQuantity(item.getQuantity());
            orderDetail.setUnitPrice(item.getPrice());
            totalAmount += item.getPrice() * item.getQuantity();
            order.getOrderDetails().add(orderDetail);

            // Gọi phương thức giảm số lượng sản phẩm
            productService.reduceProductQuantity(item.getProduct().getId(), item.getQuantity());
        }

        if (discountCode != null && !discountCode.isEmpty()) {
            Discount discount = discountService.applyDiscount(discountCode);
            if (discount != null) {
                double discountAmount = totalAmount * (discount.getPercentage() / 100.0);
                totalAmount -= discountAmount;
                order.setDiscountCode(discount);
                order.setDiscountAmount(discountAmount);
            }
        }

        order.setTotalAmount(totalAmount);
        return orderRepository.save(order);
    }
}
