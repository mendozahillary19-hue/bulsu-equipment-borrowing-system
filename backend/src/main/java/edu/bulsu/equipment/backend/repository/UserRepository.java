package edu.bulsu.equipment.backend.repository;

import edu.bulsu.equipment.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentId(String studentId);
    Optional<User> findByEmail(String email);
    boolean existsByStudentId(String studentId);
    boolean existsByEmail(String email);
}
