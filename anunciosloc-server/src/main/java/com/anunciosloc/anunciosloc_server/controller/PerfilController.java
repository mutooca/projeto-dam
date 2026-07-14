package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.PerfilRequest;
import com.anunciosloc.anunciosloc_server.dto.PerfilResponse;
import com.anunciosloc.anunciosloc_server.service.PerfilService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final PerfilService perfilService;

    /**
     * Adicionar/atualizar perfil do utilizador
     * POST /api/perfil
     */
    @PostMapping
    public ResponseEntity<?> adicionarPerfil(@Valid @RequestBody PerfilRequest request) {
        log.info(" [PERFIL] Adicionando perfil para: {}", request.getEmail());

        try {
            String resultado = perfilService.adicionarPerfil(request);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", resultado
            ));
        } catch (Exception e) {
            log.error(" Erro ao adicionar perfil: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()
            ));
        }
    }

    /**
     * Consultar perfil do utilizador
     * GET /api/perfil?email=joao@teste.com
     */
    @GetMapping
    public ResponseEntity<?> consultarPerfil(@RequestParam String email) {
        log.info(" [PERFIL] Consultando perfil para: {}", email);

      

        try {
            PerfilResponse response = perfilService.consultarPerfil(email);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error(" Erro ao consultar perfil: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()
            ));
        }
    }

    /**
     * Remover uma chave específica do perfil
     * DELETE /api/perfil/{chave}?email=kama@gmail.com
     */
    @DeleteMapping("/{chave}")
    public ResponseEntity<?> removerChavePerfil(
            @PathVariable String chave,
            @RequestParam String email) {
        log.info(" [PERFIL] Removendo chave '{}' para: {}", chave, email);

        try {
            String resultado = perfilService.removerChavePerfil(email, chave);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", resultado
            ));
        } catch (Exception e) {
            log.error(" Erro ao remover chave: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()
            ));
        }
    }
}