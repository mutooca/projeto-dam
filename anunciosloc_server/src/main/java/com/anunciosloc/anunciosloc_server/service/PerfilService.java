package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerfilService {

    private final PerfilUtilizadorRepository perfilRepository;
    private final UtilizadorRepository utilizadorRepository;

    // Obter perfil do utilizador
    public PerfilResponse obterPerfil(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        List<PerfilParDto> pares = perfilRepository.findByUtilizador(user)
            .stream()
            .map(p -> PerfilParDto.builder()
                    .chave(p.getChave())
                    .valor(p.getValor())
                    .build())
            .toList();

        
        List<String> chavesPublicas = perfilRepository.findTodasAsChaves();

        return PerfilResponse.builder()
                .email(user.getEmail())
                .nome(user.getNome())
                .pares(pares)
                .chavesPublicas(chavesPublicas)
                .build();
    }

   
    @SuppressWarnings("null")
    @Transactional
    public PerfilParDto adicionarPar(String email, PerfilParDto request) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        // Se já existe a chave, actualiza o valor
        perfilRepository.deleteByUtilizadorAndChave(user, request.getChave());

        PerfilUtilizador par = PerfilUtilizador.builder()
                .utilizador(user)
                .chave(request.getChave().toLowerCase().trim())
                .valor(request.getValor().trim())
                .build();

        perfilRepository.save(par);

        log.info("Par adicionado ao perfil de {}: {}={}",
                 email, request.getChave(), request.getValor());

        return PerfilParDto.builder()
                .chave(par.getChave())
                .valor(par.getValor())
                .build();
    }

    // Remover par por chave
    @Transactional
    public void removerPar(String email, String chave) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        perfilRepository.deleteByUtilizadorAndChave(user, chave);
        log.info("Par '{}' removido do perfil de {}", chave, email);
    }

    // Listar todas as chaves públicas do sistema
    public List<String> listarChavesPublicas() {
        return perfilRepository.findTodasAsChaves();
    }
}