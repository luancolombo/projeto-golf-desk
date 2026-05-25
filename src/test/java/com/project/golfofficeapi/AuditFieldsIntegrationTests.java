package com.project.golfofficeapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.golfofficeapi.model.User;
import com.project.golfofficeapi.repository.BookingRepository;
import com.project.golfofficeapi.repository.CashRegisterClosureItemRepository;
import com.project.golfofficeapi.repository.CashRegisterClosureRepository;
import com.project.golfofficeapi.repository.TeeTimeRepository;
import com.project.golfofficeapi.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuditFieldsIntegrationTests {

    private static final String MANAGER_EMAIL = "manager@golfoffice.dev";
    private static final String MANAGER_PASSWORD = "admin123";
    private static final long FORGED_USER_ID = 999999L;

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final TeeTimeRepository teeTimeRepository;
    private final BookingRepository bookingRepository;
    private final CashRegisterClosureRepository cashRegisterClosureRepository;
    private final CashRegisterClosureItemRepository cashRegisterClosureItemRepository;

    @Autowired
    AuditFieldsIntegrationTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            UserRepository userRepository,
            TeeTimeRepository teeTimeRepository,
            BookingRepository bookingRepository,
            CashRegisterClosureRepository cashRegisterClosureRepository,
            CashRegisterClosureItemRepository cashRegisterClosureItemRepository
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.teeTimeRepository = teeTimeRepository;
        this.bookingRepository = bookingRepository;
        this.cashRegisterClosureRepository = cashRegisterClosureRepository;
        this.cashRegisterClosureItemRepository = cashRegisterClosureItemRepository;
    }

    @Test
    void shouldFillBookingCreatedByWithAuthenticatedUser() throws Exception {
        User manager = findManager();
        String token = loginAndExtractAccessToken();
        Long teeTimeId = null;
        Long bookingId = null;

        try {
            String teeTimeResponse = mockMvc.perform(post("/tee-time")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newTeeTimePayload()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            teeTimeId = objectMapper.readTree(teeTimeResponse).get("id").asLong();

            String bookingResponse = mockMvc.perform(post("/booking")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newBookingPayload(teeTimeId)))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.createdBy").value(manager.getId()))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode bookingJson = objectMapper.readTree(bookingResponse);
            bookingId = bookingJson.get("id").asLong();

            assertThat(bookingJson.get("createdBy").asLong()).isEqualTo(manager.getId());
            assertThat(bookingJson.get("createdBy").asLong()).isNotEqualTo(FORGED_USER_ID);
            assertThat(bookingRepository.findById(bookingId).orElseThrow().getCreatedBy()).isEqualTo(manager.getId());
        } finally {
            deleteBookingFixture(bookingId, teeTimeId);
        }
    }

    @Test
    void shouldFillCashRegisterClosureClosedByWithAuthenticatedUser() throws Exception {
        User manager = findManager();
        String token = loginAndExtractAccessToken();
        Long closureId = null;

        try {
            String response = mockMvc.perform(post("/cash-register-closure/close")
                            .header("Authorization", "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(newCashRegisterClosurePayload(uniquePastBusinessDate())))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.closedBy").value(manager.getId()))
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            JsonNode closureJson = objectMapper.readTree(response);
            closureId = closureJson.get("id").asLong();

            assertThat(closureJson.get("closedBy").asLong()).isEqualTo(manager.getId());
            assertThat(closureJson.get("closedBy").asLong()).isNotEqualTo(FORGED_USER_ID);
            assertThat(cashRegisterClosureRepository.findById(closureId).orElseThrow().getClosedBy()).isEqualTo(manager.getId());
        } finally {
            deleteCashRegisterClosureFixture(closureId);
        }
    }

    private User findManager() {
        return userRepository.findByEmail(MANAGER_EMAIL)
                .orElseThrow(() -> new AssertionError("Development manager user seed not found"));
    }

    private String loginAndExtractAccessToken() throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new LoginRequest(MANAGER_EMAIL, MANAGER_PASSWORD))))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String accessToken = objectMapper.readTree(response).get("accessToken").asText();
        assertThat(accessToken).isNotBlank();
        return accessToken;
    }

    private String newTeeTimePayload() {
        LocalDate playDate = LocalDate.now().plusYears(20).plusDays(ThreadLocalRandom.current().nextInt(1, 5000));
        int hour = ThreadLocalRandom.current().nextInt(7, 19);
        int minute = ThreadLocalRandom.current().nextInt(0, 6) * 10;

        return """
                {
                  "playDate": "%s",
                  "startTime": "%02d:%02d:00",
                  "maxPlayers": 4,
                  "bookedPlayers": 0,
                  "status": "AVAILABLE",
                  "baseGreenFee": 80.00
                }
                """.formatted(playDate, hour, minute);
    }

    private String newBookingPayload(Long teeTimeId) {
        return """
                {
                  "code": null,
                  "status": "CREATED",
                  "totalAmount": 0.00,
                  "createdBy": %d,
                  "teeTimeId": %d
                }
                """.formatted(FORGED_USER_ID, teeTimeId);
    }

    private String newCashRegisterClosurePayload(LocalDate businessDate) {
        return """
                {
                  "businessDate": "%s",
                  "closedBy": %d,
                  "notes": "Audit integration test closure"
                }
                """.formatted(businessDate, FORGED_USER_ID);
    }

    private LocalDate uniquePastBusinessDate() {
        LocalDate businessDate;

        do {
            businessDate = LocalDate.now()
                    .minusYears(80)
                    .minusDays(ThreadLocalRandom.current().nextInt(1, 20000));
        } while (cashRegisterClosureRepository.findByBusinessDate(businessDate).isPresent());

        return businessDate;
    }

    private void deleteBookingFixture(Long bookingId, Long teeTimeId) {
        if (bookingId != null && bookingRepository.existsById(bookingId)) {
            bookingRepository.deleteById(bookingId);
        }

        if (teeTimeId != null && teeTimeRepository.existsById(teeTimeId)) {
            teeTimeRepository.deleteById(teeTimeId);
        }
    }

    private void deleteCashRegisterClosureFixture(Long closureId) {
        if (closureId == null || !cashRegisterClosureRepository.existsById(closureId)) {
            return;
        }

        cashRegisterClosureItemRepository.deleteAll(cashRegisterClosureItemRepository.findByCashRegisterClosureId(closureId));
        cashRegisterClosureRepository.deleteById(closureId);
    }

    private record LoginRequest(String email, String password) {
    }
}
