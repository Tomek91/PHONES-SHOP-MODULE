package pl.com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pl.com.app.model.Phone;

import java.util.List;

public interface PhoneRepository extends JpaRepository<Phone, Long> {
    @Query("select p.producer from Phone p group by p.producer")
    List<String> findAllPhoneProducers();

    List<Phone> findAllByProducerIn(List<String> producers);
    List<Phone> findAllByIdIn(List<Long> ids);
}
