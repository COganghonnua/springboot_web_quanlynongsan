package com.example.Buoi2.repository;


import com.example.Buoi2.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
   /* @Query("SELECT new map(DATE(o.createdDate) as date, SUM(o.totalAmount) as totalAmount) " +
            "FROM Order o " +
            "GROUP BY DATE(o.createdDate)")
    List<Map<String, Object>> getRevenueStatistics();*/
}