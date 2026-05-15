package com.project.golfofficeapi.services;

import com.project.golfofficeapi.dto.UserDTO;
import com.project.golfofficeapi.enums.UserRole;
import com.project.golfofficeapi.exceptions.BusinessException;
import com.project.golfofficeapi.exceptions.RequiredObjectIsNullException;
import com.project.golfofficeapi.exceptions.ResourceNotFoundException;
import com.project.golfofficeapi.mapper.custom.UserMapper;
import com.project.golfofficeapi.model.User;
import com.project.golfofficeapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.logging.Logger;

@Service
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final Logger logger = Logger.getLogger(UserService.class.getName());

    public UserService(UserRepository repository, UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserDTO> findAll() {
        logger.info("Find All Users");
        return mapper.toDTOList(repository.findAll());
    }

    public UserDTO findById(Long id) {
        logger.info("Find User by ID");
        User user = findEntityById(id);
        return mapper.toDTO(user);
    }

    public User findActiveEntityByEmail(String email) {
        logger.info("Find Active User by Email");
        return repository.findByEmailAndActiveTrue(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found or inactive"));
    }

    public UserDTO create(UserDTO user) {
        if (user == null) throw new RequiredObjectIsNullException();
        logger.info("Create User");

        validateCreate(user);
        User entity = mapper.toEntity(user);
        entity.setPassword(passwordEncoder.encode(user.getPassword()));
        entity.setRole(resolveRole(user.getRole()));
        entity.setActive(user.getActive() == null || user.getActive());

        return mapper.toDTO(repository.save(entity));
    }

    public UserDTO update(UserDTO user) {
        if (user == null) throw new RequiredObjectIsNullException();
        if (user.getId() == null) throw new BusinessException("User id is required");
        logger.info("Update User");

        User entity = findEntityById(user.getId());
        validateUpdate(user);

        entity.setName(user.getName());
        entity.setEmail(user.getEmail());
        entity.setRole(resolveRole(user.getRole()));
        entity.setActive(user.getActive() == null || user.getActive());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            entity.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        return mapper.toDTO(repository.save(entity));
    }

    public void delete(Long id) {
        logger.info("Deactivate User");
        User entity = findEntityById(id);
        entity.setActive(false);
        repository.save(entity);
    }

    private User findEntityById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private void validateCreate(UserDTO user) {
        validateRequiredFields(user);

        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new BusinessException("Password is required");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already registered");
        }
    }

    private void validateUpdate(UserDTO user) {
        validateRequiredFields(user);

        if (repository.existsByEmailAndIdNot(user.getEmail(), user.getId())) {
            throw new BusinessException("Email already registered");
        }
    }

    private void validateRequiredFields(UserDTO user) {
        if (user.getName() == null || user.getName().isBlank()) {
            throw new BusinessException("Name is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessException("Email is required");
        }

        if (user.getRole() == null || user.getRole().isBlank()) {
            throw new BusinessException("Role is required");
        }

        resolveRole(user.getRole());
    }

    private UserRole resolveRole(String role) {
        try {
            return UserRole.fromString(role);
        } catch (IllegalArgumentException exception) {
            throw new BusinessException("Invalid user role");
        }
    }
}
