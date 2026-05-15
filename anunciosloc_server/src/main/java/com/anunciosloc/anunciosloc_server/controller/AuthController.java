package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.LoginRequest;
import com.anunciosloc.anunciosloc_server.dto.RegistoRequest;
import com.anunciosloc.anunciosloc_server.client.TicketResponse;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            TicketResponse response = authService.login(request.getEmail(), request.getClientNonce());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/registar")
    public ResponseEntity<?> registar(@RequestBody RegistoRequest request) {
        try {
            Utilizador user = authService.registar(request.getEmail(), request.getPassword(), request.getRole());
            return ResponseEntity.ok("Utilizador registado: " + user.getEmail() + " (Role: " + user.getRole() + ")");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}