package pl.com.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.com.app.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}
