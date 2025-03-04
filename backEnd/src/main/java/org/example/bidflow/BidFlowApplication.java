package org.example.bidflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BidFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(BidFlowApplication.class, args);
    }

}
