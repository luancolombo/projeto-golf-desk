package com.project.golfofficeapi.services;

import com.project.golfofficeapi.model.User;
import com.project.golfofficeapi.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedUserService {

    private static final String ANONYMOUS_USER = "anonymousUser";

    private final UserRepository repository;

    public AuthenticatedUserService(UserRepository repository) {
        this.repository = repository;
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated user not found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        String email = extractEmail(principal);

        return repository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Authenticated user not found"));
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public Long getCurrentUserIdOrNull() {
        try {
            return getCurrentUserId();
        } catch (AuthenticationCredentialsNotFoundException exception) {
            return null;
        }
    }

    public String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }

    private String extractEmail(Object principal) {
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }

        if (principal instanceof String value && !ANONYMOUS_USER.equals(value)) {
            return value;
        }

        throw new AuthenticationCredentialsNotFoundException("Authenticated user not found");
    }
}
