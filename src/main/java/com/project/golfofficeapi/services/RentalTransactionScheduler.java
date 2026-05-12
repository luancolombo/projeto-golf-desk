package com.project.golfofficeapi.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class RentalTransactionScheduler {

    private final Logger logger = Logger.getLogger(RentalTransactionScheduler.class.getName());
    private final RentalTransactionService rentalTransactionService;

    public RentalTransactionScheduler(RentalTransactionService rentalTransactionService) {
        this.rentalTransactionService = rentalTransactionService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Lisbon")
    public void returnOpenRentalsAtMidnight() {
        logger.info("Running midnight rental return job");
        rentalTransactionService.returnAll();
    }
}
