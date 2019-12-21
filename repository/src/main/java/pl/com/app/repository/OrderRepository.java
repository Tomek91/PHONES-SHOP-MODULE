package pl.com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.app.model.Order;
import pl.com.app.model.User;

import java.util.Optional;
import java.util.Set;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByUserId_EqualsAndIsCompletedFalse(Long userId);
    Set<Order> findAllByUserAndIsCompletedTrueOrderByDateTime(User user);
}
