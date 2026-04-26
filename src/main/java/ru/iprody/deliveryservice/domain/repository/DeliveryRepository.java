package ru.iprody.deliveryservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.iprody.deliveryservice.domain.model.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
