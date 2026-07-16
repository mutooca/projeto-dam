package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.CriarLocalRequest;
import com.anunciosloc.anunciosloc_server.dto.EditarLocalRequest;
import com.anunciosloc.anunciosloc_server.security.KerberosAuthInterceptor;
import com.anunciosloc.anunciosloc_server.uddi.dto.LocalInfoSOAP;
import com.anunciosloc.anunciosloc_server.service.LocalService;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/api/locais")
@RequiredArgsConstructor
public class LocalController {

    private final LocalService localService;

    @PostMapping("/criar")
    public ResponseEntity<?> criarLocal(
            @Valid @RequestBody CriarLocalRequest request,
            @RequestParam double lat,
            @RequestParam double lon) {
        log.info(" [LOCAL-CONTROLLER] POST /api/locais/criar");
        log.info("   Query lat={}, lon={}", lat, lon);
        log.info("   Body nome={}, email={}, latitude={}, longitude={}, raio={}, ssidWifi={}",
                request.getNome(),
                request.getEmailUtilizador(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRaio(),
                request.getSsidWifi());
        try {
            var resultado = localService.criarLocal(request, lat, lon);
            log.info(" [LOCAL-CONTROLLER] Local criado com sucesso: idLocal={}, mensagem={}",
                    resultado.getIdLocal(), resultado.getMensagem());
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error(" [LOCAL-CONTROLLER] Erro ao criar local: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{idLocal}")
    public ResponseEntity<?> eliminarLocal(
            @PathVariable String idLocal,
            @RequestParam(required = false) String emailUtilizador,
            HttpServletRequest httpRequest) {

        String authenticatedEmail = resolverIdentidade(httpRequest, emailUtilizador);
        if (authenticatedEmail == null) {
            return naoAutenticado();
        }

        log.info(" [LOCAL] Eliminando local: {} por {} (qualquer utilizador autenticado pode remover)",
                idLocal, authenticatedEmail);

        try {
            String resultado = localService.eliminarLocal(idLocal, authenticatedEmail);
            return ResponseEntity.ok(Map.of(
                    "sucesso", true,
                    "mensagem", resultado));
        } catch (Exception e) {
            log.error(" Erro ao eliminar local: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()));
        }
    }

    @PutMapping("/{idLocal}")
    public ResponseEntity<?> editarLocal(
            @PathVariable String idLocal,
            @Valid @RequestBody EditarLocalRequest request,
            HttpServletRequest httpRequest) {

        String authenticatedEmail = resolverIdentidade(httpRequest, null);
        if (authenticatedEmail == null) {
            return naoAutenticado();
        }

        log.info(" [LOCAL-CONTROLLER] PUT /api/locais/{} por {} (qualquer utilizador autenticado pode editar)",
                idLocal, authenticatedEmail);
        log.info("   Body nome={}, latitude={}, longitude={}, raio={}, ssidWifi={}",
                request.getNome(), request.getLatitude(), request.getLongitude(),
                request.getRaio(), request.getSsidWifi());

        try {
            var resultado = localService.editarLocal(idLocal, request, authenticatedEmail);
            log.info(" [LOCAL-CONTROLLER] Local editado com sucesso: idLocal={}, mensagem={}",
                    resultado.getIdLocal(), resultado.getMensagem());
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            log.error(" [LOCAL-CONTROLLER] Erro ao editar local: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private String resolverIdentidade(HttpServletRequest httpRequest, String emailUtilizadorCliente) {
        String authenticatedEmail = (String) httpRequest.getAttribute(KerberosAuthInterceptor.ATTR_AUTHENTICATED_EMAIL);
        if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
            log.error(" [LOCAL-CONTROLLER] Não foi possível resolver a identidade autenticada");
            return null;
        }
        if (emailUtilizadorCliente != null && !emailUtilizadorCliente.isBlank()
                && !emailUtilizadorCliente.equalsIgnoreCase(authenticatedEmail)) {
            log.warn(" [LOCAL-CONTROLLER] emailUtilizador do cliente ({}) não corresponde à identidade autenticada ({}). A usar a identidade autenticada.",
                    emailUtilizadorCliente, authenticatedEmail);
        }
        return authenticatedEmail;
    }

    private ResponseEntity<?> naoAutenticado() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "sucesso", false,
                "mensagem", "Não foi possível confirmar a identidade autenticada. Faça login novamente."));
    }

    @GetMapping("/proximos")
    public ResponseEntity<?> listarLocaisProximos(
            @RequestParam double lat,
            @RequestParam double lon) {
        try {
            List<LocalInfoSOAP> locais = localService.listarLocais(lat, lon);
            return ResponseEntity.ok(locais);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
