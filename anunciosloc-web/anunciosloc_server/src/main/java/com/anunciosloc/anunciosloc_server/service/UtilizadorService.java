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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final SaldoUtilizadorRepository saldoRepository;

    @Transactional
    public Utilizador ativarUtilizador(RegistarUtilizadorRequest request) {
        if (!EmailValidator.isValid(request.getEmail())) {
            throw new RuntimeException(
                "Email inválido. Formato esperado: utilizador@dominio. " +
                "Domínios permitidos: gmail.com, hotmail.com, outlook.com, " +
                "yahoo.com, empresa.com, universidade.ao, anunciosloc.com"
            );
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

    public SaldoResponse obterSaldo(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        return SaldoResponse.builder()
            .email(user.getEmail())
            .nome(user.getNome())
            .saldoGlobal(user.getSaldo())
            .saldosPorInfra(saldoRepository.findByUtilizador(user).stream()
                .map(saldo -> SaldoPorInfra.builder()
                    .idInfra(saldo.getInfraestrutura().getIdInfraestrutura())
                    .nomeInfra(saldo.getNome())
                    .saldoParcial(saldo.getSaldo())
                    .pontosGanhos(saldo.getPontosGanhos())
                    .pontosGastos(saldo.getPontosPerdidos())
                    .build())
                .collect(Collectors.toList()))
            .build();
    }
    
    @Transactional
    @CachePut(value = "preferencias", key = "#email")
    public void atualizarPreferencias(String email, String preferencias) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        user.setPreferenciaAnuncio(preferencias);
        utilizadorRepository.save(user);
    }
}