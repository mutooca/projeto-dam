package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
//import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.service.InfraestruturaService;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraDisponivelUddiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/infraestruturas")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class InfraestruturaController {

    private final InfraestruturaService infraService;

    @PostMapping
    public ResponseEntity<?> registarInfraestrutura(@Valid @RequestBody RegistarInfraRequest request) {
        try {
            Infraestrutura infra = infraService.registarInfraestrutura(request);
            return ResponseEntity.ok("Infraestrutura registada com ID: " + infra.getIdInfraestrutura());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/proximas")
    public ResponseEntity<?> listarInfraestruturasProximas(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam int k) {
        try {
            List<InfraestruturaResponse> infraestruturas = infraService.listarInfraestruturasProximas(lat, lon, k);
            return ResponseEntity.ok(infraestruturas);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/locais")
    public ResponseEntity<?> listarLocais(
            @PathVariable UUID id,
            @RequestParam Double lat,
            @RequestParam Double lon) {
        try {
            List<LocalResponse> locais = infraService.listarLocais(id, lat, lon);
            return ResponseEntity.ok(locais);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/disponiveis-uddi")
    public ResponseEntity<?> listarDisponiveisUddi() {
        try {
            List<InfraDisponivelUddiResponse> resultado = 
                    infraService.listarDisponiveisUddi();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/recolocar")
    public ResponseEntity<?> recolocarInfraestrutura(
            @PathVariable UUID id,
            @Valid @RequestBody RecolocarInfraRequest request) {
        try {
            return ResponseEntity.ok(infraService.recolocarInfraestrutura(
                    id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/redimensionar")
    public ResponseEntity<?> redimensionarInfraestrutura(
            @PathVariable UUID id,
            @Valid @RequestBody RedimensionarInfraRequest request) {
        try {
            return ResponseEntity.ok(infraService.redimensionarInfraestrutura(
                    id, request));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}