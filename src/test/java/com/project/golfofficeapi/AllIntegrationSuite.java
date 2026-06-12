package com.project.golfofficeapi;

import com.project.golfofficeapi.services.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        GolfOfficeApiApplicationTests.class,
        AgendaControllerIntegrationTests.class,
        AuditFieldsIntegrationTests.class,
        CashRegisterClosureLockIntegrationTests.class,
        SecurityMockMvcIntegrationTests.class,
        BookingServiceIntegrationTests.class,
        BookingPlayerServiceIntegrationTests.class,
        PaymentReceiptServiceIntegrationTests.class,
        RentalDamageReportServiceTests.class,
        CashRegisterClosureServiceIntegrationTests.class
})
class AllIntegrationSuite {
}
