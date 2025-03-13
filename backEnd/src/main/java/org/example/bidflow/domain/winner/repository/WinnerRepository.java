package org.example.bidflow.domain.winner.repository;

import org.example.bidflow.domain.winner.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WinnerRepository extends JpaRepository<Winner, Long> {
    List<Winner> findByUser_UserUUID(String userUUID);
}
