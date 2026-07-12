package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.uddi.dto.CriarLocalRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.LocalInfoSOAP;
import com.anunciosloc.anunciosloc_server.service.LocalService;
import lombok.RequiredArgsConstructor;
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
            @Valid @RequestBody CriarLocalRequestSOAP request,
            @RequestParam double lat,
            @RequestParam double lon) {
        try {
            String resultado = localService.criarLocal(request, lat, lon);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{idLocal}")
    public ResponseEntity<?> eliminarLocal(
            @PathVariable String idLocal,
            @RequestParam String emailUtilizador) {

        log.info(" [LOCAL] Eliminando local: {} por {}", idLocal, emailUtilizador);

        try {
            String resultado = localService.eliminarLocal(idLocal, emailUtilizador);
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