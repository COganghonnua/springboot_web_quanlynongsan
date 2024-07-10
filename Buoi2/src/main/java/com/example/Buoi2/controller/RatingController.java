package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Product;
import com.example.Buoi2.entity.Rating;
import com.example.Buoi2.entity.User;
import com.example.Buoi2.services.ProductService;
import com.example.Buoi2.services.RatingService;
import com.example.Buoi2.services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ProductService productService;

    @GetMapping("/add/{productId}")
    public String showRatingForm(@PathVariable Long productId, Model model) {
        model.addAttribute("product", productService.getProductById(productId).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId)));
        model.addAttribute("rating", new Rating());
        return "rating/ratingForm";
    }

    @PostMapping("/add")
    public String submitRating(@ModelAttribute Rating rating, @RequestParam Long productId) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        rating.setProduct(product);
        ratingService.saveRating(rating);

        return "redirect:/products";
    }
}


