package com.project.golfofficeapi;

import com.project.golfofficeapi.services.BookingPlayerServiceIntegrationTests;
import com.project.golfofficeapi.services.BookingServiceIntegrationTests;
import com.project.golfofficeapi.services.CashRegisterClosureServiceIntegrationTests;
import com.project.golfofficeapi.services.PaymentReceiptServiceIntegrationTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        GolfOfficeApiApplicationTests.class,
        AgendaControllerIntegrationTests.class,
        SecurityMockMvcIntegrationTests.class,
        BookingServiceIntegrationTests.class,
        BookingPlayerServiceIntegrationTests.class,
        PaymentReceiptServiceIntegrationTests.class,
        CashRegisterClosureServiceIntegrationTests.class
})
class AllIntegrationSuite {
}
