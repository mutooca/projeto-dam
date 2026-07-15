package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.LocalizacaoRequest;
import com.anunciosloc.anunciosloc_server.dto.LocalizacaoResponse;
import com.anunciosloc.anunciosloc_server.service.LocalizacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/localizacao")
@RequiredArgsConstructor
public class LocalizacaoController {

    private final LocalizacaoService localizacaoService;

    /**
     * Recebe localização + perfil do utilizador (chamado automaticamente pela APP)
     * POST /api/localizacao
     */
    @PostMapping
    public ResponseEntity<?> receberLocalizacao(@Valid @RequestBody LocalizacaoRequest request) {
        log.info(" [CONTROLLER] Requisição de localização de: {}", request.getEmail());

        try {
            LocalizacaoResponse response = localizacaoService.receberLocalizacao(request);
            
            if (response.isSucesso()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            log.error(" Erro ao processar localização: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "sucesso", false,
                "mensagem", e.getMessage()
            ));
        }
    }
}