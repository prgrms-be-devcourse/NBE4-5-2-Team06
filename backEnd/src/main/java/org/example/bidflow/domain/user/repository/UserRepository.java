package org.example.bidflow.domain.user.repository;

import org.example.bidflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmailOrNickname(String email,String nickname);

    Optional<User> findByUserUuid(String userUuid);
}
