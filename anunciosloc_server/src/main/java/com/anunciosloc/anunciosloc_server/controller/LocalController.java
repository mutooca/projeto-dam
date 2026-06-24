package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.CriarLocalRequest;
import com.anunciosloc.anunciosloc_server.dto.LocalResponse;
import com.anunciosloc.anunciosloc_server.service.LocalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locais")
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    
    @PostMapping
    public ResponseEntity<?> criarLocal(@Valid @RequestBody CriarLocalRequest request) {
        try {
            if (request.getLatUtilizador() == null || request.getLonUtilizador() == null) {
                return ResponseEntity.badRequest()
                        .body("Coordenadas do utilizador obrigatórias");
            }
            LocalResponse local = localService.criarLocalAutomatico(
                    request,
                    request.getLatUtilizador(),
                    request.getLonUtilizador()
            );
            return ResponseEntity.ok(local);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    
    @GetMapping
    public ResponseEntity<?> listarLocaisProximos(
            @RequestParam Double lat,
            @RequestParam Double lon) {
        try {
            List<LocalResponse> locais = localService.listarLocaisProximos(lat, lon);
            return ResponseEntity.ok(locais);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarLocal(
            @PathVariable UUID id,
            @RequestParam String email) {
        try {
            localService.removerLocal(id, email);
            return ResponseEntity.ok("Local eliminado com sucesso");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}