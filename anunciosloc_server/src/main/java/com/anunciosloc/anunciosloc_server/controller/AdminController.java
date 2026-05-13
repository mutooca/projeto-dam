package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {
    
    private final InfraestruturaRepository infraRepository;
    private final UtilizadorRepository utilizadorRepository;
    
    @PostMapping("/infraestruturas")
    public ResponseEntity<?> registarInfraestrutura(@RequestBody @NonNull Infraestrutura infra) {
        try {
            Infraestrutura saved = infraRepository.save(infra);
            return ResponseEntity.ok("Infraestrutura registada com ID: " + saved.getId());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @DeleteMapping("/infraestruturas/{id}")
    public ResponseEntity<?> eliminarInfraestrutura(@PathVariable @NonNull Long id) {
        try {
            infraRepository.deleteById(id);
            return ResponseEntity.ok("Infraestrutura eliminada");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/estatisticas")
    public ResponseEntity<?> getEstatisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUtilizadores", utilizadorRepository.count());
        stats.put("totalInfraestruturas", infraRepository.count());
        stats.put("totalAnuncios", infraRepository.findAll().stream()
            .mapToInt(Infraestrutura::getTotalAnuncios).sum());
        stats.put("totalEntregas", infraRepository.findAll().stream()
            .mapToInt(Infraestrutura::getTotalEntregas).sum());
        return ResponseEntity.ok(stats);
    }
    
    @GetMapping("/utilizadores")
    public ResponseEntity<?> listarUtilizadores() {
        return ResponseEntity.ok(utilizadorRepository.findAll());
    }
}