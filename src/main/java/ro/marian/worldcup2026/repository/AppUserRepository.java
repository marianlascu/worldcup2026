package ro.marian.worldcup2026.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ro.marian.worldcup2026.model.AppUser;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Optional<AppUser> findByUsername(String username);

}