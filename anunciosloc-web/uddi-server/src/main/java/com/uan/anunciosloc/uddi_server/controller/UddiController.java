package com.uan.anunciosloc.uddi_server.controller;

import com.uan.anunciosloc.uddi_server.model.UddiRecord;
import com.uan.anunciosloc.uddi_server.service.UddiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/uddi")
@RequiredArgsConstructor
public class UddiController {

    private final UddiService uddiService;

     
    // Infra chama este endpoint ao arrancar
    @PostMapping("/register")
    public ResponseEntity<UddiRecord> registar(
            @RequestBody Map<String, String> body) {

        String serviceName = body.get("serviceName");
        String serviceUrl  = body.get("serviceUrl");

        if (serviceName == null || serviceUrl == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(uddiService.registar(serviceName, serviceUrl));
    }

    
    @DeleteMapping("/register/{serviceName}")
    public ResponseEntity<Void> cancelar(
            @PathVariable String serviceName) {

        boolean ok = uddiService.cancelar(serviceName);
        return ok
            ? ResponseEntity.noContent().build()
            : ResponseEntity.notFound().build();
    }

    // anunciosloc_server chama para descobrir todas as infras do grupo
    @GetMapping("/lookup")
    public ResponseEntity<List<UddiRecord>> pesquisar(
            @RequestParam(required = false) String prefixo) {

        return ResponseEntity.ok(uddiService.pesquisar(prefixo));
    }

    
    @GetMapping("/lookup/{serviceName}")
    public ResponseEntity<UddiRecord> obter(
            @PathVariable String serviceName) {

        return uddiService.obter(serviceName)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

  
    @PostMapping("/ping/{serviceName}")
    public ResponseEntity<Void> ping(
            @PathVariable String serviceName) {

        boolean ok = uddiService.ping(serviceName);
        return ok
            ? ResponseEntity.ok().build()
            : ResponseEntity.notFound().build();
    }

   
    // Listar todos os serviços registados
    @GetMapping("/all")
    public ResponseEntity<Collection<UddiRecord>> listarTodos() {
        return ResponseEntity.ok(uddiService.listarTodos());
    }

    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(Map.of(
            "status",   "UP",
            "servicos", uddiService.listarTodos().size()
        ));
    }

    
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, String>> clear() {
        uddiService.clear();
        return ResponseEntity.ok(Map.of("status", "cleared"));
    }
}