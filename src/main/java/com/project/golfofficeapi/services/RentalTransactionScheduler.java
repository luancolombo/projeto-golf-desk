package com.project.golfofficeapi.services;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.logging.Logger;

@Component
public class RentalTransactionScheduler {

    private static final ZoneId LISBON_ZONE = ZoneId.of("Europe/Lisbon");
    private final Logger logger = Logger.getLogger(RentalTransactionScheduler.class.getName());
    private final RentalTransactionService rentalTransactionService;

    public RentalTransactionScheduler(RentalTransactionService rentalTransactionService) {
        this.rentalTransactionService = rentalTransactionService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Lisbon")
    public void returnOpenRentalsAtMidnight() {
        logger.info("Running midnight rental return job");
        rentalTransactionService.returnOverdueRentals(LocalDate.now(LISBON_ZONE));
    }

    @EventListener(ApplicationReadyEvent.class)
    public void returnOverdueRentalsOnStartup() {
        logger.info("Running startup overdue rental return job");
        rentalTransactionService.returnOverdueRentals(LocalDate.now(LISBON_ZONE));
    }
}
