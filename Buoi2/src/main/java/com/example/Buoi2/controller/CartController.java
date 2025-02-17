package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Discount;
import com.example.Buoi2.services.CartService;
import com.example.Buoi2.services.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private DiscountService discountService;

    @PostMapping("/add")
    public String addToCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    @ResponseBody
    public String updateCart(@RequestParam("productId") Long productId, @RequestParam("quantity") int quantity) {
        try {
            cartService.updateCartItem(productId, quantity);
            return "{\"status\":\"success\"}";
        } catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage() + "\"}";
        }
    }

    @GetMapping
    public String showCart(Model model) {
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPriceBeforeDiscount", cartService.getTotalPriceBeforeDiscount());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        model.addAttribute("discountPercentage", cartService.getDiscountPercentage());
        return "cart/cart";
    }

    @PostMapping("/cart/apply-discount")
    public String applyDiscount(@RequestParam("discountCode") String discountCode, Model model) {
        try {
            Discount discount = discountService.applyDiscount(discountCode);
            cartService.applyDiscount(discount);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        model.addAttribute("cartItems", cartService.getCartItems());
        model.addAttribute("totalPriceBeforeDiscount", cartService.getTotalPriceBeforeDiscount());
        model.addAttribute("totalPrice", cartService.getTotalPrice());
        model.addAttribute("discountPercentage", cartService.getDiscountPercentage());
        return "cart/cart";
    }


    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }
}
