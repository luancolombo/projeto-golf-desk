package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.CashRegisterClosureDTO;
import com.project.golfofficeapi.exceptions.BusinessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class CashRegisterClosureServiceIntegrationTests {

    private final CashRegisterClosureService service;

    @Autowired
    CashRegisterClosureServiceIntegrationTests(CashRegisterClosureService service) {
        this.service = service;
    }

    @Test
    void shouldPreviewAndCloseCashRegisterForBusinessDate() {
        LocalDate businessDate = LocalDate.of(2000, 1, 1);

        CashRegisterClosureDTO preview = service.preview(businessDate);

        assertThat(preview.getId()).isNull();
        assertThat(preview.getBusinessDate()).isEqualTo(businessDate);
        assertThat(preview.getStatus()).isEqualTo("OPEN");
        assertThat(preview.getNetTotal()).isEqualByComparingTo("0.00");
        assertThat(preview.getItems()).isEmpty();

        CashRegisterClosureDTO request = new CashRegisterClosureDTO();
        request.setBusinessDate(businessDate);
        request.setClosedBy(null);
        request.setNotes("Integration test closure");

        CashRegisterClosureDTO closed = service.close(request);

        assertThat(closed.getId()).isNotNull();
        assertThat(closed.getBusinessDate()).isEqualTo(businessDate);
        assertThat(closed.getStatus()).isEqualTo("CLOSED");
        assertThat(closed.getClosedAt()).isNotNull();
        assertThat(closed.getNetTotal()).isEqualByComparingTo("0.00");
        assertThat(closed.getNotes()).isEqualTo("Integration test closure");

        assertThatThrownBy(() -> service.close(request))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("already closed");
    }
}
