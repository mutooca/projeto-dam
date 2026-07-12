package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.ActualizarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.dto.RegistarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.dto.SaldoPorInfra;
import com.anunciosloc.anunciosloc_server.dto.SaldoResponse;
import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.ObterSaldoResponse;
import com.anunciosloc.anunciosloc_server.util.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final InfraestruturaRepository infraRepository;  
    private final BCryptPasswordEncoder passwordEncoder;
    private final InfrastruturaSoapClient soapClient;
   private final QuorumService quorumService;

   
    @Transactional
    @CacheEvict(value = "saldo", key = "#request.email")
    public Utilizador ativarUtilizador(RegistarUtilizadorRequest request) {
        if (!EmailValidator.isValid(request.getEmail())) {
            throw new RuntimeException("Email inválido");
        }

        if (utilizadorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email já registado");
        }

        Utilizador user = new Utilizador();
        user.setNome(request.getNome());
        user.setEmail(request.getEmail());
        user.setPalavraChave(passwordEncoder.encode(request.getPalavraChave()));
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

        
        float saldoQuorum = user.getSaldo();
        try {
            saldoQuorum = quorumService.lerSaldoQuorum(user.getEmail());
            log.debug("Saldo obtido do quorum: {}", saldoQuorum);

            if (saldoQuorum != user.getSaldo()) {
                user.setSaldo(Math.round(saldoQuorum));
                utilizadorRepository.save(user);
            }
        } catch (Exception e) {
            log.warn("Quorum não disponível — usando BD: {}", e.getMessage());
        }

        
        List<SaldoPorInfra> saldosPorInfra = new ArrayList<>();

        
        List<Infraestrutura> infras = infraRepository.findAll();

        for (Infraestrutura infra : infras) {
            try {
                
                InfraProxy proxy = soapClient.obterClientePorNome(infra.getNome());

                if (proxy != null) {
                    
                    ObterSaldoResponse response = proxy.obterSaldo(email);

                    if (response != null && response.isSucesso()) {
                        saldosPorInfra.add(SaldoPorInfra.builder()
                            .idInfra(infra.getId())
                            .nomeInfra(infra.getNome())
                            .saldoParcial(Math.round(response.getSaldo()))
                            .pontosGanhos(0)  
                            .pontosGastos(0)
                            .build());
                    }
                }
            } catch (Exception e) {
                log.warn(" Não foi possível obter saldo da infra {}: {}", infra.getNome(), e.getMessage());
            }
        }

       
        return SaldoResponse.builder()
            .email(user.getEmail())
            .nome(user.getNome())
            .saldoGlobal(Math.round(saldoQuorum))
            .saldosPorInfra(saldosPorInfra)
            .build();
    }

   
    /*public List<SincronizacaoSaldoDto> obterTodosSaldos() {
        return utilizadorRepository.findAll().stream()
            .map(u -> SincronizacaoSaldoDto.builder()
                .idUtilizador(u.getIdUtilizador().toString())
                .saldo(u.getSaldo())
                .versao(1)
                .build())
            .toList();
    }*/

    
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "saldo", key = "#email"),
        @CacheEvict(value = "preferencias", key = "#email")
    })
    public void atualizarPreferencias(String email, String preferencias) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        user.setPreferenciaAnuncio(preferencias);
        utilizadorRepository.save(user);

        // Enviar perfil para todas as infras via SOAP
       /*  List<InfraProxy> proxies = soapClient.obterClientes();
        for (InfraProxy proxy : proxies) {
            try {
                proxy.enviarPerfil(
                    user.getIdUtilizador().toString(),
                    user.getEmail(),
                    preferencias
                );
                log.debug("Perfil enviado para infra: {}", proxy.getServiceUrl());
            } catch (Exception e) {
                log.warn("Não foi possível enviar perfil para {}: {}", proxy.getServiceUrl(), e.getMessage());
            }
        }*/
    }

   
    @SuppressWarnings("null")
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "saldo", key = "#request.email"),
        @CacheEvict(value = "preferencias", key = "#request.email")
    })
    public Utilizador atualizarDados(ActualizarUtilizadorRequest request) {
        Utilizador user = utilizadorRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (request.getNome() != null && !request.getNome().isBlank()) {
            user.setNome(request.getNome());
        }

        if (request.getNovaPalavraChave() != null && !request.getNovaPalavraChave().isBlank()) {
            if (request.getPalavraChaveActual() == null) {
                throw new RuntimeException("Password actual é obrigatória para alterar a password");
            }
            if (!passwordEncoder.matches(request.getPalavraChaveActual(), user.getPalavraChave())) {
                throw new RuntimeException("Password actual incorrecta");
            }
            user.setPalavraChave(passwordEncoder.encode(request.getNovaPalavraChave()));
        }

        if (request.getPreferenciaAnuncio() != null) {
            user.setPreferenciaAnuncio(request.getPreferenciaAnuncio());

            List<InfraProxy> proxies = soapClient.obterClientes();
            /*for (InfraProxy proxy : proxies) {
                try {
                    proxy.enviarPerfil(
                        user.getIdUtilizador().toString(),
                        user.getEmail(),
                        request.getPreferenciaAnuncio()
                    );
                } catch (Exception e) {
                    log.warn("Não foi possível enviar perfil para {}: {}", proxy.getServiceUrl(), e.getMessage());
                }
            }*/
        }

        return utilizadorRepository.save(user);
    }
}