package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.InfraestruturaRegistoRequest;
import com.anunciosloc.anunciosloc_server.dto.InfraestruturaRegistoResponse;
import com.anunciosloc.anunciosloc_server.dto.RecolocarInfraRequest;
import com.anunciosloc.anunciosloc_server.dto.RedimensionarInfraRequest;
import com.anunciosloc.anunciosloc_server.service.InfraestruturaGestaoService;
import com.anunciosloc.anunciosloc_server.uddi.dto.UddiInstanciaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/admin/infraestruturas")
@RequiredArgsConstructor
public class InfraestruturaGestaoController {

    private final InfraestruturaGestaoService gestaoService;

    @GetMapping("/uddi/instancias")
    public ResponseEntity<?> listarInstanciasUDDI() {
        log.info(" Gestor solicitou lista de instâncias do UDDI");

        try {
            List<UddiInstanciaResponse> instancias = gestaoService.listarInstanciasUDDI();
            return ResponseEntity.ok(instancias);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao listar instâncias: " + e.getMessage());
        }
    }

    @PostMapping("/registar")
    public ResponseEntity<?> registarInfraestrutura(@Valid @RequestBody InfraestruturaRegistoRequest request) {
        log.info(" Gestor a registar infraestrutura: {}", request.getNome());

        try {
            InfraestruturaRegistoResponse response = gestaoService.registarInfraestrutura(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(" Erro ao registar infra: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/redimensionar")
    public ResponseEntity<?> redimensionarInfraestrutura(
            @PathVariable UUID id,
            @Valid @RequestBody RedimensionarInfraRequest request) {

        log.info(" [ADMIN] Redimensionando infraestrutura: {}", id);

        try {
            String resultado = gestaoService.redimensionarInfraestrutura(id, request);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", resultado));
        } catch (Exception e) {
            log.error(" Erro ao redimensionar infra: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()));
        }
    }

    @PutMapping("/{id}/recolocar")
    public ResponseEntity<?> recolocarInfraestrutura(
            @PathVariable UUID id,
            @Valid @RequestBody RecolocarInfraRequest request) {
        
        log.info(" [ADMIN] Recolocando infraestrutura: {}", id);
        
        try {
            String resultado = gestaoService.recolocarInfraestrutura(id, request);
            return ResponseEntity.ok(Map.of(
                "sucesso", true,
                "mensagem", resultado
            ));
        } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
            return ResponseEntity.status(409).body(Map.of(
                "sucesso", false,
                "mensagem", "Conflito de concorrência: A infraestrutura foi modificada por outro administrador. Recarregue e tente novamente."
            ));
        } catch (Exception e) {
            log.error(" Erro ao recolocar infra: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "mensagem", e.getMessage()
            ));
        }
    }

    @GetMapping("/verificar/{nome}")
    public ResponseEntity<?> verificarNomeExistente(@PathVariable String nome) {
        try {
            boolean existe = gestaoService.verificarNomeExistente(nome);
            return ResponseEntity.ok(java.util.Map.of(
                    "nome", nome,
                    "existe", existe));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}