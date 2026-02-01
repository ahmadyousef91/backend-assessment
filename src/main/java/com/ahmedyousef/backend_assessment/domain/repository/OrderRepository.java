package com.ahmedyousef.backend_assessment.domain.repository;


import com.ahmedyousef.backend_assessment.domain.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUserId(Long userId);

}