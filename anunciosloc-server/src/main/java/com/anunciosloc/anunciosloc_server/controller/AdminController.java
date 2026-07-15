package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.dto.admin.AdminInfoDTO;
import com.anunciosloc.anunciosloc_server.dto.admin.DashboardEstatisticasDTO;
import com.anunciosloc.anunciosloc_server.dto.admin.InfraestruturaAdminDTO;
import com.anunciosloc.anunciosloc_server.dto.admin.UtilizadorAdminDTO;
import com.anunciosloc.anunciosloc_server.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/me")
    public ResponseEntity<?> obterAdminInfo(
            @RequestParam String email) {
        log.info(" [ADMIN] Obtendo informações do admin: {}", email);

        try {
            AdminInfoDTO adminInfo = adminService.obterAdminInfo(email);
            return ResponseEntity.ok(adminInfo);
        } catch (Exception e) {
            log.error(" Erro ao obter informações do admin: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "sucesso", false,
                    "mensagem", e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> obterDashboard() {
        log.info(" [ADMIN] Dashboard solicitado");
        try {
            DashboardEstatisticasDTO stats = adminService.obterDashboardEstatisticas();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error(" Erro no dashboard: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/infraestruturas")
    public ResponseEntity<?> listarInfraestruturas() {
        try {
            List<InfraestruturaAdminDTO> infras = adminService.listarTodasInfraestruturas();
            return ResponseEntity.ok(infras);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/infraestruturas/nao-registadas")
    public ResponseEntity<?> listarInfraestruturasNaoRegistadas() {
        try {
            List<InfraestruturaAdminDTO> infras = adminService.listarInfraestruturasNaoRegistadas();
            return ResponseEntity.ok(infras);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/utilizadores")
    public ResponseEntity<?> listarUtilizadores() {
        try {
            List<UtilizadorAdminDTO> users = adminService.listarTodosUtilizadores();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/utilizadores/inativos")
    public ResponseEntity<?> listarUtilizadoresInativos() {
        try {
            List<UtilizadorAdminDTO> users = adminService.listarUtilizadoresInativos();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/utilizadores/ativos")
    public ResponseEntity<?> listarUtilizadoresAtivos() {
        try {
            List<UtilizadorAdminDTO> users = adminService.listarUtilizadoresAtivos();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }

    @GetMapping("/utilizadores/contagem")
    public ResponseEntity<?> contagemUtilizadores() {
        try {
            long total = adminService.contarUtilizadores();
            long ativos = adminService.contarUtilizadoresAtivos();
            long inativos = adminService.contarUtilizadoresInativos();
            return ResponseEntity.ok(Map.of(
                    "total", total,
                    "ativos", ativos,
                    "inativos", inativos));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erro", e.getMessage()));
        }
    }
}