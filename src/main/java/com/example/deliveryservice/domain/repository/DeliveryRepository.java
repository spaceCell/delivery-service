package com.example.deliveryservice.domain.repository;

import com.example.deliveryservice.domain.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
