package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.LoginRequest;
import com.anunciosloc.anunciosloc_server.dto.LogoutRequest;
import com.anunciosloc.anunciosloc_server.dto.RegistarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.client.TicketResponse;
import com.anunciosloc.anunciosloc_server.client.TicketResponseAdmin;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {

            String clientNonce = UUID.randomUUID().toString();
            TicketResponse response = authService.login(request.getEmail(),request.getPalavraChave() ,clientNonce);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> loginAdmin(@RequestBody LoginRequest request) {
        try {
            String clientNonce = UUID.randomUUID().toString();
            TicketResponseAdmin response = authService.loginAdmin(request.getEmail(), request.getPalavraChave(), clientNonce);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @PostMapping("/registar")
    public ResponseEntity<?> registar(@Valid @RequestBody RegistarUtilizadorRequest request) {
        try {
           Utilizador user = authService.registar(request);
            return ResponseEntity.ok("Utilizador registado: " + user.getEmail() + " (Role: " + user.getRole() + ")");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequest request) {
        try {
            authService.logout(request.getSessionId());
            return ResponseEntity.ok("Logout realizado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}