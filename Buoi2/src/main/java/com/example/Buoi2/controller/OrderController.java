package com.example.Buoi2.controller;

import com.example.Buoi2.DTO.CheckOutRequest;
import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Order;
import com.example.Buoi2.services.CartService;
import com.example.Buoi2.services.OrderService;
import com.example.Buoi2.services.DiscountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private DiscountService discountService;

    @GetMapping("/checkout")
    public String checkout(Model model, @RequestParam(name = "discountCode", required = false) String discountCode) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        if (discountCode != null) {
            model.addAttribute("discountCode", discountCode);
        }

        return "cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(String customerName, String shippingAddress, String phoneNumber, String email, String notes, String discountCode, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart";
        }

        Order order = orderService.createOrder(customerName, shippingAddress, phoneNumber, email, notes, cartItems, discountCode);

        double totalAmount = order.getTotalAmount();

        // Sử dụng RedirectAttributes để chuyển dữ liệu
        redirectAttributes.addFlashAttribute("order", order);
        redirectAttributes.addFlashAttribute("totalAmount", totalAmount);
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model, SessionStatus sessionStatus) {
        // Order và totalAmount được thêm qua RedirectAttributes sẽ có sẵn trong model
        Order order = (Order) model.asMap().get("order");
        Double totalAmount = (Double) model.asMap().get("totalAmount");

        if (order == null || totalAmount == null) {
            return "redirect:/cart";
        }

        model.addAttribute("message", "Your order has been successfully placed.");
        sessionStatus.setComplete();
        return "cart/order-confirmation";
    }

    @PostMapping("/submit-vnpay")
    public void submitOrderVNPay(@ModelAttribute CheckOutRequest checkOutRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CartItem> cartItems = cartService.getCartItems();

        if (cartItems.isEmpty()) {
            response.sendRedirect("/cart");
            return;
        }

        String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String vnpayUrl = orderService.checkOutWithPayOnline(
                checkOutRequest.getCustomerName(),
                checkOutRequest.getShippingAddress(),
                checkOutRequest.getPhoneNumber(),
                checkOutRequest.getEmail(),
                checkOutRequest.getNotes(),
                cartItems,
                checkOutRequest.getDiscountCode(),
                baseUrl
        );
        System.out.println("vnpayUrl: " + vnpayUrl);
        response.sendRedirect(vnpayUrl);
    }

    @GetMapping("/vnpay-payment")
    public String GetMapping(HttpServletRequest request, Model model){

        System.out.println("Vào day khong?");
        int paymentStatus =orderService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus == 1 ? "ordersuccess" : "orderfail";
    }

    @GetMapping("/list")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/list";
    }

    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/order/list";
        }
        model.addAttribute("order", order);
        return "order/detail";
    }

    @GetMapping("/edit/{id}")
    public String editOrder(@PathVariable("id") Long id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/order/list";
        }
        model.addAttribute("order", order);
        return "order/edit";
    }

    @PostMapping("/update")
    public String updateOrder(@ModelAttribute("order") Order order, RedirectAttributes redirectAttributes) {
        Order updatedOrder = orderService.updateOrder(order);
        if (updatedOrder != null) {
            redirectAttributes.addFlashAttribute("message", "Order updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update order. Please try again.");
        }
        return "redirect:/order/list";
    }
}
