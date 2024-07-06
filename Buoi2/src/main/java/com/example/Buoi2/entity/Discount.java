package com.example.Buoi2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "discounts")
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private double percentage;

    private LocalDate startDate;
    private LocalDate endDate;

    @OneToMany(mappedBy = "discountCode", cascade = CascadeType.ALL)  // mappedBy phải khớp với discountCode trong Order
    private Set<Order> orders;
}
