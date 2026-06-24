package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.ActualizarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.dto.SaldoResponse;
import com.anunciosloc.anunciosloc_server.dto.SincronizacaoSaldoDto;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.service.UtilizadorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/utilizadores")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    @GetMapping("/{email}/saldo")
    public ResponseEntity<?> obterSaldo(@PathVariable String email) {
        try {
            SaldoResponse saldo = utilizadorService.obterSaldo(email);
            return ResponseEntity.ok(saldo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/saldos/todos")
    public ResponseEntity<?> obterTodosSaldos() {
        try {
            List<SincronizacaoSaldoDto> saldos = utilizadorService.obterTodosSaldos();
            return ResponseEntity.ok(saldos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{email}/preferencias")
    public ResponseEntity<?> atualizarPreferencias(@PathVariable String email, @RequestParam String preferencias) {
        try {
            utilizadorService.atualizarPreferencias(email, preferencias);
            return ResponseEntity.ok("Preferências atualizadas");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/atualizar")
    public ResponseEntity<?> atualizarDados(
            @Valid @RequestBody ActualizarUtilizadorRequest request) {
        try {
            Utilizador user = utilizadorService.atualizarDados(request);
            return ResponseEntity.ok(
                "Dados actualizados com sucesso para: " + user.getEmail());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}