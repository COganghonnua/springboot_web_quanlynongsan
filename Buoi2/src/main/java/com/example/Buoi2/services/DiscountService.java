package com.example.Buoi2.services;

import com.example.Buoi2.entity.Discount;
import com.example.Buoi2.repository.DiscountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountService {

    @Autowired
    private DiscountRepository discountRepository;

    public List<Discount> getAllDiscounts() {
        return discountRepository.findAll();
    }

    public void addDiscount(Discount discount) {
        discountRepository.save(discount);
    }

    public Optional<Discount> getDiscountById(Long id) {
        return discountRepository.findById(id);
    }

    public void updateDiscount(Discount discount) {
        discountRepository.save(discount);
    }

    public void deleteDiscountById(Long id) {
        discountRepository.deleteById(id);
    }

    public Discount applyDiscount(String code) {
        Optional<Discount> discountOpt = discountRepository.findByCode(code);

        if (discountOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid discount code");
        }

        Discount discount = discountOpt.get();
        LocalDate now = LocalDate.now();
        if ((discount.getStartDate() != null && discount.getStartDate().isAfter(now)) ||
                (discount.getEndDate() != null && discount.getEndDate().isBefore(now))) {
            throw new IllegalArgumentException("Discount code is not valid at this time");
        }

        return discount;
    }

}
