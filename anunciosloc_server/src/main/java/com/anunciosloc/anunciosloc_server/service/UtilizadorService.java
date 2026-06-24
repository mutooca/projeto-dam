package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.RegistarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.dto.SaldoPorInfra;
import com.anunciosloc.anunciosloc_server.dto.SaldoResponse;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.SaldoUtilizadorRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.util.EmailValidator;

import lombok.RequiredArgsConstructor;

import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final SaldoUtilizadorRepository saldoRepository;

    @Transactional
    public Utilizador ativarUtilizador(RegistarUtilizadorRequest request) {
        if (!EmailValidator.isValid(request.getEmail())) {
            throw new RuntimeException("Email inválido.");
        }
        if (utilizadorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já registado");
        }
        
        Utilizador user = new Utilizador();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setPalavraChave(request.getPalavraChave());
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setPreferenciaAnuncio(request.getPreferenciaAnuncio());
        user.setSaldo(10);
        user.setDataCriacao(LocalDateTime.now());
        user.setAtivo(true);
        
        return utilizadorRepository.save(user);
    }

    @Transactional(readOnly = true)
    public SaldoResponse obterSaldo(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        return SaldoResponse.builder()
            .email(user.getEmail())
            .nome(user.getNome())
            .saldoGlobal(user.getSaldo())
            .preferencias(user.getPreferenciaAnuncio())
            .totalAnuncios((long) user.getAnuncios().size())
            .totalEntregas((long) user.getEntregas().size())
            .saldosPorInfra(saldoRepository.findByUtilizador(user).stream()
                .map(saldo -> SaldoPorInfra.builder()
                    .idInfra(saldo.getInfraestrutura().getIdInfraestrutura())
                    .nomeInfra(saldo.getInfraestrutura().getNome())
                    .saldoParcial(saldo.getSaldoParcial())
                    .pontosGanhos(saldo.getPontosGanhos())
                    .pontosGastos(saldo.getPontosGastos())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
    
    @Transactional
    public void atualizarPreferencias(String email, String preferencias) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        user.setPreferenciaAnuncio(preferencias);
        utilizadorRepository.save(user);
    }

    @Transactional
    public Utilizador editarPerfil(Map<String, Object> request) {
        String email = (String) request.get("email");
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        if (request.containsKey("nome")) {
            user.setNome((String) request.get("nome"));
        }
        return utilizadorRepository.save(user);
    }

    public List<String> listarChavesPublicas() {
        Set<String> chaves = new HashSet<>();
        utilizadorRepository.findAll().forEach(u -> {
            String prefs = u.getPreferenciaAnuncio();
            if (prefs != null && !prefs.isEmpty()) {
                for (String pair : prefs.split(",")) {
                    String[] kv = pair.split("=");
                    if (kv.length > 0) chaves.add(kv[0].trim());
                }
            }
        });
        return chaves.stream().sorted().collect(Collectors.toList());
    }
}
