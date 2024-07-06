package com.example.Buoi2.controller;

import com.example.Buoi2.entity.Discount;
import com.example.Buoi2.services.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @GetMapping
    public String listDiscounts(Model model) {
        List<Discount> discounts = discountService.getAllDiscounts();
        model.addAttribute("discounts", discounts);
        return "discounts/list";
    }

    @GetMapping("/add")
    public String showAddDiscountForm(Model model) {
        model.addAttribute("discount", new Discount());

        return "discounts/add";
    }

    @PostMapping("/add")
    public String addDiscount(@ModelAttribute Discount discount, Model model) {

        if (discount.getStartDate() == null || discount.getEndDate() == null) {
            model.addAttribute("error", "Start Date and End Date are required.");
            return "discounts/add";
        }

        discountService.addDiscount(discount);
        return "redirect:/discounts";
    }



    @GetMapping("/edit/{id}")
    public String showEditDiscountForm(@PathVariable Long id, Model model) {
        Discount discount = discountService.getDiscountById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid discount Id:" + id));
        model.addAttribute("discount", discount);
        return "discounts/edit";
    }

    @PostMapping("/update/{id}")
    public String updateDiscount(@PathVariable Long id, @ModelAttribute Discount discount) {
        discount.setId(id);
        discountService.updateDiscount(discount);
        return "redirect:/discounts";
    }

    @GetMapping("/delete/{id}")
    public String deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscountById(id);
        return "redirect:/discounts";
    }
}
