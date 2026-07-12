package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.service.InfraestruturaService;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoSOAP;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/infraestruturas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class InfraestruturaController {

    private final InfraestruturaService infraestruturaService;

    @GetMapping("/listar-todas")
    public ResponseEntity<?> listarTodasInfraestruturas() {
        log.info(" [CONTROLLER] Requisição para listar todas as infraestruturas");

        try {
            List<InfraInfoSOAP> infras = infraestruturaService.listarTodasInfras();

            if (infras.isEmpty()) {
                return ResponseEntity.ok("Nenhuma infraestrutura disponível no momento");
            }

            return ResponseEntity.ok(infras);

        } catch (Exception e) {
            log.error(" Erro ao listar infraestruturas: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao listar infraestruturas: " + e.getMessage());
        }
    }

    @GetMapping("/{nome}")
    public ResponseEntity<?> obterInfoInfra(@PathVariable String nome) {
        log.info(" [CONTROLLER] Requisição para obter info da infra: {}", nome);

        try {
            InfraInfoSOAP infra = infraestruturaService.obterInfoInfra(nome);
            return ResponseEntity.ok(infra);

        } catch (Exception e) {
            log.error(" Erro ao obter info da infra {}: {}", nome, e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao obter informações: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/proximas")
    public ResponseEntity<?> listarInfrasProximas(
            @RequestParam double lat,
            @RequestParam double lon) {
        log.info(" [CONTROLLER] Listando infras próximas a lat={}, lon={}", lat, lon);

        try {
            List<InfraInfoSOAP> infras = infraestruturaService.listarTodasInfras();

            infras.forEach(infra -> {
                if (infra.getLatitude() != null && infra.getLongitude() != null) {
                    double distancia = HaversineUtil.calcularDistancia(
                            lat, lon,
                            infra.getLatitude(),
                            infra.getLongitude());
                    infra.setDistancia(distancia);
                }
            });

            infras.sort(Comparator.comparingDouble(InfraInfoSOAP::getDistancia));

            return ResponseEntity.ok(infras);

        } catch (Exception e) {
            log.error(" Erro ao listar infras próximas: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao listar infraestruturas: " + e.getMessage());
        }
    }

    @SuppressWarnings("null")
    @GetMapping("/estatisticas")
    public ResponseEntity<?> obterEstatisticas() {
        log.info(" [CONTROLLER] Requisição para estatísticas do sistema");

        try {
            List<InfraInfoSOAP> infras = infraestruturaService.listarTodasInfras();

            int totalInfras = infras.size();
            int totalLocais = infras.stream().mapToInt(InfraInfoSOAP::getTotalLocais).sum();
            int totalAnuncios = infras.stream().mapToInt(InfraInfoSOAP::getTotalAnuncios).sum();
            int totalEntregas = infras.stream().mapToInt(InfraInfoSOAP::getTotalEntregas).sum();
            int totalConexoes = infras.stream().mapToInt(InfraInfoSOAP::getTotalConexoes).sum();

            return ResponseEntity.ok(java.util.Map.of(
                    "totalInfraestruturas", totalInfras,
                    "totalLocais", totalLocais,
                    "totalAnuncios", totalAnuncios,
                    "totalEntregas", totalEntregas,
                    "totalConexoes", totalConexoes));

        } catch (Exception e) {
            log.error(" Erro ao obter estatísticas: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erro ao obter estatísticas: " + e.getMessage());
        }
    }
}