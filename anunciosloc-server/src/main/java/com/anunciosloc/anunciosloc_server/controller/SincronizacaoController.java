package com.anunciosloc.anunciosloc_server.controller;

import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.service.QuorumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/internal/sincronizacao")
@RequiredArgsConstructor
public class SincronizacaoController {

    private final UtilizadorRepository utilizadorRepository;
    private final QuorumService quorumService;

    /**
     *  Infra-Server chama este ednpoin quando inicia
     * ele devolve todos os saldos dos utilizadores registados
     */
    @GetMapping("/saldos")
    public ResponseEntity<?> obterTodosSaldos() {
        List<Utilizador> utilizadores = utilizadorRepository.findAll();

        Map<String, Integer> saldos = new HashMap<>();
        for (Utilizador user : utilizadores) {
            if ("USER".equals(user.getRole())) {
                try {
                    float saldoQuorum = quorumService.lerSaldoQuorum(user.getEmail());
                    saldos.put(user.getEmail(), Math.round(saldoQuorum));
                } catch (Exception e) {
                    // Fallback: usar o saldo da BD local
                    saldos.put(user.getEmail(), user.getSaldo());
                }
            }
        }

        return ResponseEntity.ok(saldos);
    }
}