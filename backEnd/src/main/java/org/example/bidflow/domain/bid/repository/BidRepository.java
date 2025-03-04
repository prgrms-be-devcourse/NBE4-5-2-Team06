package org.example.bidflow.domain.bid.repository;

import org.example.bidflow.domain.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
