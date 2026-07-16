package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.PerfilRequest;
import com.anunciosloc.anunciosloc_server.dto.PerfilResponse;
import com.anunciosloc.anunciosloc_server.security.KerberosAuthInterceptor;
import com.anunciosloc.anunciosloc_server.service.PerfilService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> adicionarPerfil(@Valid @RequestBody PerfilRequest request, HttpServletRequest httpRequest) {
        String authenticatedEmail = resolverIdentidade(httpRequest, request.getEmail());
        if (authenticatedEmail == null) {
            return naoAutenticado();
        }

        log.info(" [PERFIL] Adicionando perfil para: {} ({} atributo(s))", authenticatedEmail,
                request.getPerfil() != null ? request.getPerfil().size() : 0);

        PerfilRequest requestSeguro = PerfilRequest.builder()
                .email(authenticatedEmail)
                .perfil(request.getPerfil())
                .build();

        try {
            String resultado = perfilService.adicionarPerfil(requestSeguro);
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
     * Catálogo global: todos os pares chave=valor já criados por qualquer utilizador.
     * GET /api/perfil/todos
     */
    @GetMapping("/todos")
    public ResponseEntity<?> listarCatalogo() {
        log.info(" [PERFIL] Consultando catálogo global de atributos");
        try {
            return ResponseEntity.ok(perfilService.listarTodosPerfis());
        } catch (Exception e) {
            log.error(" Erro ao listar catálogo de perfis: {}", e.getMessage());
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
     * Saldo de pontos atual do utilizador (ganho quando outros abrem os seus anúncios).
     * GET /api/perfil/saldo?email=joao@teste.com
     */
    @GetMapping("/saldo")
    public ResponseEntity<?> consultarSaldo(@RequestParam String email) {
        log.info(" [PERFIL] Consultando saldo para: {}", email);
        try {
            int saldo = perfilService.obterSaldo(email);
            return ResponseEntity.ok(Map.of(
                    "email", email,
                    "saldo", saldo
            ));
        } catch (Exception e) {
            log.error(" Erro ao consultar saldo: {}", e.getMessage());
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
            @RequestParam(required = false) String email,
            HttpServletRequest httpRequest) {
        String authenticatedEmail = resolverIdentidade(httpRequest, email);
        if (authenticatedEmail == null) {
            return naoAutenticado();
        }

        log.info(" [PERFIL] Removendo chave '{}' para: {}", chave, authenticatedEmail);

        try {
            String resultado = perfilService.removerChavePerfil(authenticatedEmail, chave);
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

    private String resolverIdentidade(HttpServletRequest httpRequest, String emailCliente) {
        String authenticatedEmail = (String) httpRequest.getAttribute(KerberosAuthInterceptor.ATTR_AUTHENTICATED_EMAIL);
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            log.error(" [PERFIL] Não foi possível resolver a identidade autenticada");
            return null;
        }
        if (emailCliente != null && !emailCliente.isBlank() && !emailCliente.equalsIgnoreCase(authenticatedEmail)) {
            log.warn(" [PERFIL] email do cliente ({}) não corresponde à identidade autenticada ({}). A usar a identidade autenticada.",
                    emailCliente, authenticatedEmail);
        }
        return authenticatedEmail;
    }

    private ResponseEntity<?> naoAutenticado() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "sucesso", false,
                "mensagem", "Não foi possível confirmar a identidade autenticada. Faça login novamente."));
    }
}