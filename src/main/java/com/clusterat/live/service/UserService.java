package com.clusterat.live.service;

import com.clusterat.live.dto.UserDTO;
import com.clusterat.live.model.UserModel;
import com.clusterat.live.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTO> listUsers() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchUsers(String name) {
        return userRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
    }

    public UserDTO getUserByWppId(String wppId) {
        return userRepository.findByWppId(wppId)
                .map(this::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with wppId: " + wppId));
    }

    @Transactional
    public UserDTO createUser(UserDTO dto) {
        if (dto.getWppId() != null && userRepository.findByWppId(dto.getWppId()).isPresent()) {
            throw new IllegalArgumentException("User with wppId already exists: " + dto.getWppId());
        }

        UserModel user = UserModel.builder()
                .name(dto.getName())
                .wppId(dto.getWppId())
                .build();

        UserModel saved = userRepository.save(user);
        log.info("User created with id: {} and name: {}", saved.getId(), saved.getName());
        return toDTO(saved);
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO dto) {
        UserModel user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        if (dto.getName() != null) {
            user.setName(dto.getName());
        }

        if (dto.getWppId() != null && !dto.getWppId().equals(user.getWppId())) {
            if (userRepository.findByWppId(dto.getWppId()).isPresent()) {
                throw new IllegalArgumentException("User with wppId already exists: " + dto.getWppId());
            }
            user.setWppId(dto.getWppId());
        }

        UserModel updated = userRepository.save(user);
        log.info("User updated with id: {} and name: {}", updated.getId(), updated.getName());
        return toDTO(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    private UserDTO toDTO(UserModel model) {
        return UserDTO.builder()
                .id(model.getId())
                .name(model.getName())
                .wppId(model.getWppId())
                .createdAt(model.getCreatedAt())
                .build();
    }
}

