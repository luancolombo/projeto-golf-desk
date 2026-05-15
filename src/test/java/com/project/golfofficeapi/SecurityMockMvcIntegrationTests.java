package com.project.golfofficeapi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.golfofficeapi.enums.UserRole;
import com.project.golfofficeapi.model.User;
import com.project.golfofficeapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityMockMvcIntegrationTests {

    private static final String MANAGER_EMAIL = "manager@golfoffice.dev";
    private static final String MANAGER_PASSWORD = "admin123";
    private static final String RECEPTIONIST_EMAIL = "receptionist.security.test@golfoffice.dev";
    private static final String RECEPTIONIST_PASSWORD = "reception123";

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    SecurityMockMvcIntegrationTests(
            MockMvc mockMvc,
            ObjectMapper objectMapper,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @BeforeEach
    void setUp() {
        userRepository.findByEmail(RECEPTIONIST_EMAIL)
                .map(this::activateReceptionist)
                .orElseGet(this::createReceptionist);
    }

    @Test
    void shouldLoginAndReturnAccessAndRefreshTokens() throws Exception {
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload(MANAGER_EMAIL, MANAGER_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value(MANAGER_EMAIL))
                .andExpect(jsonPath("$.role").value("MANAGER"));
    }

    @Test
    void shouldAllowPublicEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldRejectProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/player"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Unauthorized"))
                .andExpect(jsonPath("$.details").value("uri=/player"));
    }

    @Test
    void shouldAllowProtectedEndpointWithValidToken() throws Exception {
        String token = loginAndExtractAccessToken(MANAGER_EMAIL, MANAGER_PASSWORD);

        mockMvc.perform(get("/player")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldDenyAccessWhenRoleIsNotAllowed() throws Exception {
        String token = loginAndExtractAccessToken(RECEPTIONIST_EMAIL, RECEPTIONIST_PASSWORD);

        mockMvc.perform(post("/rental-item")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Security Test Buggy",
                                  "type": "Material",
                                  "totalStock": 1,
                                  "availableStock": 1,
                                  "rentalPrice": 10.00,
                                  "active": true
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Access denied"))
                .andExpect(jsonPath("$.details").value("uri=/rental-item"));
    }

    private String loginAndExtractAccessToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginPayload(email, password)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode json = objectMapper.readTree(response);
        String accessToken = json.get("accessToken").asText();

        assertThat(accessToken).isNotBlank();
        return accessToken;
    }

    private String loginPayload(String email, String password) throws Exception {
        return objectMapper.writeValueAsString(new LoginRequest(email, password));
    }

    private User activateReceptionist(User user) {
        user.setName("Security Test Receptionist");
        user.setPassword(passwordEncoder.encode(RECEPTIONIST_PASSWORD));
        user.setRole(UserRole.RECEPTIONIST);
        user.setActive(true);
        return userRepository.save(user);
    }

    private User createReceptionist() {
        LocalDateTime now = LocalDateTime.now();

        User user = new User();
        user.setName("Security Test Receptionist");
        user.setEmail(RECEPTIONIST_EMAIL);
        user.setPassword(passwordEncoder.encode(RECEPTIONIST_PASSWORD));
        user.setRole(UserRole.RECEPTIONIST);
        user.setActive(true);
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        return userRepository.save(user);
    }

    private record LoginRequest(String email, String password) {
    }
}
