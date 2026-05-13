package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.*;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnunciosService {
    
    private final UtilizadorRepository utilizadorRepository;
    private final InfraestruturaRepository infraestruturaRepository;
    private final MensagemRepository mensagemRepository;
    
    
    
    @Transactional
    public Utilizador ativarUtilizador(String email, String password) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        user.setAtivo(true);
        return utilizadorRepository.save(user);
    }
    
    public SaldoResponse obterSaldo(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));
        
        return SaldoResponse.builder()
            .email(user.getEmail())
            .saldo(user.getSaldo())
            .build();
    }
    
    public List<InfraestruturaResponse> listarInfraestruturas(double lat, double lon, int k) {
        List<Infraestrutura> todas = infraestruturaRepository.findAll();
        
        return todas.stream()
            .map(infra -> {
                double distancia = HaversineUtil.calcularDistancia(lat, lon, infra.getLatitude(), infra.getLongitude());
                return InfraestruturaResponse.builder()
                    .id(infra.getId())
                    .nome(infra.getNome())
                    .latitude(infra.getLatitude())
                    .longitude(infra.getLongitude())
                    .capacidade(infra.getCapacidade())
                    .conexoesDisponiveis(infra.getCapacidade() - infra.getConexoesAtuais())
                    .distanciaKm(distancia)
                    .totalAnuncios(infra.getTotalAnuncios())
                    .totalEntregas(infra.getTotalEntregas())
                    .build();
            })
            .sorted(Comparator.comparing(InfraestruturaResponse::getDistanciaKm))
            .limit(k)
            .collect(Collectors.toList());
    }
    
    public InfraestruturaResponse obterInfoInfraestrutura(@NonNull Long id, double latUsuario, double lonUsuario) {
        Infraestrutura infra = infraestruturaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada: " + id));
        
        double distancia = HaversineUtil.calcularDistancia(latUsuario, lonUsuario, infra.getLatitude(), infra.getLongitude());
        
        return InfraestruturaResponse.builder()
            .id(infra.getId())
            .nome(infra.getNome())
            .latitude(infra.getLatitude())
            .longitude(infra.getLongitude())
            .capacidade(infra.getCapacidade())
            .conexoesDisponiveis(infra.getCapacidade() - infra.getConexoesAtuais())
            .distanciaKm(distancia)
            .totalAnuncios(infra.getTotalAnuncios())
            .totalEntregas(infra.getTotalEntregas())
            .build();
    }
    
    @Transactional
    public MensagemResponse postarMensagem(String email, @NonNull Long infraId, String titulo, String conteudo) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        Infraestrutura infra = infraestruturaRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));
        
        if (user.getSaldo() < 1) {
            throw new RuntimeException("Saldo insuficiente para publicar anúncio (mínimo 1 ponto)");
        }
        
        user.setSaldo(user.getSaldo() - 1);
        utilizadorRepository.save(user);
        
        infra.setTotalAnuncios(infra.getTotalAnuncios() + 1);
        infraestruturaRepository.save(infra);
        
        Mensagem mensagem = new Mensagem();
        mensagem.setTitulo(titulo);
        mensagem.setConteudo(conteudo);
        mensagem.setAutor(user);
        mensagem.setLocal(infra);
        mensagem.setDataPost(LocalDateTime.now());
        mensagem.setTotalVisualizacoes(0);
        
        Mensagem saved = mensagemRepository.save(mensagem);
        
        return MensagemResponse.builder()
            .id(saved.getId())
            .titulo(saved.getTitulo())
            .conteudo(saved.getConteudo())
            .autorEmail(user.getEmail())
            .localNome(infra.getNome())
            .dataPost(saved.getDataPost())
            .entregue(false)
            .build();
    }
    
    @Transactional
    public List<MensagemResponse> receberMensagens(String email, Long infraId) {
        
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado: " + email));
        
        
        Infraestrutura infra = infraestruturaRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada: " + infraId));
        
        
        List<Mensagem> mensagensNaoVisualizadas = mensagemRepository.findByLocalAndUserNotVisualized(infra, user);
        
        
        for (Mensagem msg : mensagensNaoVisualizadas) {
            msg.getVisualizadaPor().add(user);  // add utilizador a lista de quem viu
            msg.setTotalVisualizacoes(msg.getTotalVisualizacoes() + 1);
            mensagemRepository.save(msg);
        }
        
        // atualizar estatist. da infra
        infra.setTotalEntregas(infra.getTotalEntregas() + mensagensNaoVisualizadas.size());
        infraestruturaRepository.save(infra);
        
        
        return mensagensNaoVisualizadas.stream()
            .map(msg -> MensagemResponse.builder()
                .id(msg.getId())
                .titulo(msg.getTitulo())
                .conteudo(msg.getConteudo())
                .autorEmail(msg.getAutor().getEmail())
                .localNome(msg.getLocal().getNome())
                .dataPost(msg.getDataPost())
                .entregue(true)  
                .build())
            .collect(Collectors.toList());
    }

    
    public List<MensagemResponse> listarMensagensVisualizadas(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        List<Mensagem> mensagens = mensagemRepository.findAll().stream()
            .filter(msg -> msg.getVisualizadaPor().contains(user))
            .collect(Collectors.toList());
        
        return mensagens.stream()
            .map(msg -> MensagemResponse.builder()
                .id(msg.getId())
                .titulo(msg.getTitulo())
                .conteudo(msg.getConteudo())
                .autorEmail(msg.getAutor().getEmail())
                .localNome(msg.getLocal().getNome())
                .dataPost(msg.getDataPost())
                .entregue(true)
                .build())
            .collect(Collectors.toList());
    }
   
    
    // Listar mensagens de um local específico (todas, incluindo entregues)
    public List<MensagemResponse> listarMensagensPorLocal(@NonNull Long infraId) {
        Infraestrutura infra = infraestruturaRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));
        
        return mensagemRepository.findByLocal(infra).stream()
            .map(msg -> MensagemResponse.builder()
                .id(msg.getId())
                .titulo(msg.getTitulo())
                .conteudo(msg.getConteudo())
                .autorEmail(msg.getAutor().getEmail())
                .localNome(msg.getLocal().getNome())
                .dataPost(msg.getDataPost())
                .entregue(msg.isEntregue())
                .build())
            .collect(Collectors.toList());
    }
    
    
    public List<MensagemResponse> listarMensagensPorUtilizador(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        
        return mensagemRepository.findByAutor(user).stream()
            .map(msg -> MensagemResponse.builder()
                .id(msg.getId())
                .titulo(msg.getTitulo())
                .conteudo(msg.getConteudo())
                .autorEmail(msg.getAutor().getEmail())
                .localNome(msg.getLocal().getNome())
                .dataPost(msg.getDataPost())
                .entregue(msg.isEntregue())
                .build())
            .collect(Collectors.toList());
    }
}