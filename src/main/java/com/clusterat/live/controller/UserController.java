package com.clusterat.live.controller;

import com.clusterat.live.dto.AnalysisResponseDTO;
import com.clusterat.live.dto.UserDTO;
import com.clusterat.live.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<AnalysisResponseDTO> list(@RequestParam(required = false) String name) {
        List<UserDTO> users;
        if (name != null && !name.isEmpty()) {
            users = userService.searchUsers(name);
        } else {
            users = userService.listUsers();
        }
        return ResponseEntity.ok(AnalysisResponseDTO.builder()
                .success(true)
                .message("Users fetched")
                .data(users)
                .build());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<AnalysisResponseDTO> getById(@PathVariable Long userId) {
        try {
            UserDTO user = userService.getUserById(userId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("User fetched")
                    .data(user)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @GetMapping("/wpp/{wppId}")
    public ResponseEntity<AnalysisResponseDTO> getByWppId(@PathVariable String wppId) {
        try {
            UserDTO user = userService.getUserByWppId(wppId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("User fetched")
                    .data(user)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<AnalysisResponseDTO> create(@RequestBody UserDTO dto) {
        try {
            UserDTO created = userService.createUser(dto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AnalysisResponseDTO.builder()
                            .success(true)
                            .message("User created")
                            .data(created)
                            .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @PutMapping("/{userId}")
    public ResponseEntity<AnalysisResponseDTO> update(@PathVariable Long userId,
                                                      @RequestBody UserDTO dto) {
        try {
            UserDTO updated = userService.updateUser(userId, dto);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("User updated")
                    .data(updated)
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<AnalysisResponseDTO> delete(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok(AnalysisResponseDTO.builder()
                    .success(true)
                    .message("User deleted")
                    .build());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(AnalysisResponseDTO.builder()
                            .success(false)
                            .message(ex.getMessage())
                            .build());
        }
    }
}

