package com.example.Buoi2.services;

import com.example.Buoi2.entity.CartItem;
import com.example.Buoi2.entity.Discount;
import com.example.Buoi2.entity.Product;
import com.example.Buoi2.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {

    private List<CartItem> cartItems = new ArrayList<>();
    private Discount discount;
    private double discountPercentage = 0.0;

    @Autowired
    private ProductRepository productRepository;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
        }

        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                int newQuantity = item.getQuantity() + quantity;
                if (newQuantity > product.getQuantity()) {
                    throw new IllegalArgumentException("Not enough stock for product: " + product.getName());
                }
                item.setQuantity(newQuantity);
                item.setPrice(product.getDiscountedPrice());
                return;
            }
        }

        cartItems.add(new CartItem(product, quantity, product.getDiscountedPrice()));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void updateCartItem(Long productId, int quantity) {
        for (CartItem item : cartItems) {
            if (item.getProduct().getId().equals(productId)) {
                if (quantity > item.getProduct().getQuantity()) {
                    throw new IllegalArgumentException("Not enough stock for product: " + item.getProduct().getName());
                }
                item.setQuantity(quantity);
                item.setPrice(item.getProduct().getDiscountedPrice());
                return;
            }
        }
        throw new IllegalArgumentException("Product not found in cart: " + productId);
    }

    public void clearCart() {
        cartItems.clear();
        discountPercentage = 0.0;
        discount = null;
    }

    public void applyDiscount(Discount discount) {
        this.discount = discount;
        this.discountPercentage = discount.getPercentage();
    }

    public double getTotalPriceBeforeDiscount() {
        double totalPrice = 0.0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice() * item.getQuantity();
        }
        return totalPrice;
    }

    public double getTotalPrice() {
        double totalPriceBeforeDiscount = getTotalPriceBeforeDiscount();
        return totalPriceBeforeDiscount * (1 - this.discountPercentage / 100);
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }
}
