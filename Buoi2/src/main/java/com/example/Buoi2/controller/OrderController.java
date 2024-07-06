package com.example.Buoi2.controller;

import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Order;
import com.example.Buoi2.services.CartService;
import com.example.Buoi2.services.OrderService;
import com.example.Buoi2.services.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
}
