package org.example.bidflow.domain.winner.repository;

import org.example.bidflow.domain.winner.entity.Winner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WinnerRepository extends JpaRepository<Winner, Long> {
}
