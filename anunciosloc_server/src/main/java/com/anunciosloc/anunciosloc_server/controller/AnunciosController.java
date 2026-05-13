package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.service.AnunciosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull; 
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnunciosController {
    
    private final AnunciosService service;
    
    // Ativar utilizador (após registo)
    @PostMapping("/ativar/{email}")
    public ResponseEntity<?> ativarUtilizador(@PathVariable String email, @RequestParam String password) {
        try {
            service.ativarUtilizador(email, password);
            return ResponseEntity.ok("Utilizador ativado com sucesso");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/saldo/{email}")
    public ResponseEntity<?> obterSaldo(@PathVariable String email) {
        try {
            SaldoResponse saldo = service.obterSaldo(email);
            return ResponseEntity.ok(saldo);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    // Listar infraestruturas (público - verifica no interceptor)
    @GetMapping("/infraestruturas")
    public ResponseEntity<?> listarInfraestruturas(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam int k) {
        try {
            List<InfraestruturaResponse> infraestruturas = service.listarInfraestruturas(lat, lon, k);
            return ResponseEntity.ok(infraestruturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/infraestrutura/{id}")
    public ResponseEntity<?> obterInfoInfraestrutura(
            @PathVariable @NonNull Long id,
            @RequestParam double latUsuario,
            @RequestParam double lonUsuario) {
        try {
            InfraestruturaResponse infra = service.obterInfoInfraestrutura(id, latUsuario, lonUsuario);
            return ResponseEntity.ok(infra);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @PostMapping("/mensagem")
    public ResponseEntity<?> postarMensagem(@RequestBody MensagemRequest request) {
        try {
            MensagemResponse msg = service.postarMensagem(
                request.getEmailUtilizador(),
                request.getInfraestruturaId(),
                request.getTitulo(),
                request.getConteudo()
            );
            return ResponseEntity.ok(msg);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/mensagens/{email}/{infraId}")
    public ResponseEntity<?> receberMensagens(@PathVariable String email, @PathVariable @NonNull Long infraId) {
        try {
            List<MensagemResponse> mensagens = service.receberMensagens(email, infraId);
            return ResponseEntity.ok(mensagens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/local/{infraId}/mensagens")
    public ResponseEntity<?> listarMensagensPorLocal(@PathVariable @NonNull Long infraId) {
        try {
            List<MensagemResponse> mensagens = service.listarMensagensPorLocal(infraId);
            return ResponseEntity.ok(mensagens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    
    @GetMapping("/minhas-mensagens/{email}")
    public ResponseEntity<?> listarMinhasMensagens(@PathVariable String email) {
        try {
            List<MensagemResponse> mensagens = service.listarMensagensPorUtilizador(email);
            return ResponseEntity.ok(mensagens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


        // Listar mensagens que o utilizador JÁ visualizou (histórico)
    @GetMapping("/historico/{email}")
    public ResponseEntity<?> listarHistoricoVisualizacoes(@PathVariable String email) {
        try {
            List<MensagemResponse> mensagens = service.listarMensagensVisualizadas(email);
            return ResponseEntity.ok(mensagens);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}