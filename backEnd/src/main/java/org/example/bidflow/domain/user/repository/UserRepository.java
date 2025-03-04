package org.example.bidflow.domain.user.repository;

import org.example.bidflow.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;;

public interface UserRepository extends JpaRepository<User, Long> {
}
