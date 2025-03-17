package org.example.bidflow.domain.user.repository;

import org.example.bidflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByEmailOrNickname(String email, String nickname);
    Optional<User> findByUserUUID(String userUUID);
    Optional<User> findByEmail(String email);
}
