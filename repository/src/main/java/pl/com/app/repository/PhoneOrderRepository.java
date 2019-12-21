package pl.com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.app.model.Order;
import pl.com.app.model.PhoneOrder;

import java.util.Set;

public interface PhoneOrderRepository extends JpaRepository<PhoneOrder, Long> {
    Set<PhoneOrder> findAllByOrder(Order order);
}
