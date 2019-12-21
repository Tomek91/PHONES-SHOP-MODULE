package pl.com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.app.model.ProfileParameters;

public interface ProfileParametersRepository extends JpaRepository<ProfileParameters, Long> {
}
