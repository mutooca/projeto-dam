package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.PostarAnuncioRequest;
import com.anunciosloc.anunciosloc_server.model.Anuncio;
import com.anunciosloc.anunciosloc_server.service.AnuncioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/anuncios")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AnuncioController {

    private final AnuncioService anuncioService;

    
    @PostMapping
    public ResponseEntity<?> postarAnuncio(@RequestBody PostarAnuncioRequest request) {
        try {
            Anuncio anuncio = anuncioService.postarAnuncio(request);
            return ResponseEntity.ok(anuncio);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    
    @GetMapping("/receber/{email}/{infraId}")
    public ResponseEntity<?> receberAnuncios(@PathVariable String email, @PathVariable @NonNull UUID infraId) {
        try {
            List<Anuncio> anuncios = anuncioService.receberAnuncios(email, infraId);
            return ResponseEntity.ok(anuncios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/local/{localId}")
    public ResponseEntity<?> listarPorLocal(@PathVariable @NonNull UUID localId) {
        try {
            List<Anuncio> anuncios = anuncioService.listarAnunciosPorLocal(localId);
            return ResponseEntity.ok(anuncios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/utilizador/{email}")
    public ResponseEntity<?> listarPorUtilizador(@PathVariable String email) {
        try {
            List<Anuncio> anuncios = anuncioService.listarAnunciosPorUtilizador(email);
            return ResponseEntity.ok(anuncios);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<?> removerAnuncio(@PathVariable @NonNull UUID id, @RequestParam String emailGestor) {
        try {
            anuncioService.removerAnuncio(id, emailGestor);
            return ResponseEntity.ok("Anúncio removido");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}