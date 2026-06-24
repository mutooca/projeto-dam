package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    // Obter perfil completo do utilizador
    @GetMapping
    public ResponseEntity<?> obterPerfil(@RequestParam String email) {
        try {
            return ResponseEntity.ok(perfilService.obterPerfil(email));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Adicionar par chave-valor ao perfil
    @PostMapping("/par")
    public ResponseEntity<?> adicionarPar(
            @RequestParam String email,
            @Valid @RequestBody PerfilParDto request) {
        try {
            return ResponseEntity.ok(perfilService.adicionarPar(email, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Remover par por chave
    @DeleteMapping("/par")
    public ResponseEntity<?> removerPar(
            @RequestParam String email,
            @RequestParam String chave) {
        try {
            perfilService.removerPar(email, chave);
            return ResponseEntity.ok("Par '" + chave + "' removido do perfil");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Listar todas as chaves públicas do sistema
    @GetMapping("/chaves")
    public ResponseEntity<List<String>> listarChavesPublicas() {
        return ResponseEntity.ok(perfilService.listarChavesPublicas());
    }
}