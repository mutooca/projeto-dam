package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.ActualizarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.dto.RegistarUtilizadorRequest;
import com.anunciosloc.anunciosloc_server.dto.SaldoPorInfra;
import com.anunciosloc.anunciosloc_server.dto.SaldoResponse;
import com.anunciosloc.anunciosloc_server.dto.SincronizacaoSaldoDto;
import com.anunciosloc.anunciosloc_server.model.Utilizador;
import com.anunciosloc.anunciosloc_server.repository.SaldoUtilizadorRepository;
import com.anunciosloc.anunciosloc_server.repository.UtilizadorRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.util.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UtilizadorService {

    private final UtilizadorRepository utilizadorRepository;
    private final SaldoUtilizadorRepository saldoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final InfrastruturaSoapClient soapClient;

    private final QuorumService quorumService;

    @Transactional
    @CacheEvict(value = "saldo", key = "#request.email")
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
        //user.setPalavraChave(request.getPalavraChave());
        user.setPalavraChave(passwordEncoder.encode(request.getPalavraChave()));
        user.setRole(request.getRole() != null ? request.getRole() : "USER");
        user.setPreferenciaAnuncio(request.getPreferenciaAnuncio());
        user.setSaldo(10);
        user.setDataCriacao(LocalDateTime.now());
        user.setAtivo(true);
        
        return utilizadorRepository.save(user);
    }


    @Cacheable(value = "saldo", key = "#email")
    public SaldoResponse obterSaldo(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        
        float saldoQuorum = user.getSaldo(); // fallback para BD
        try {
            saldoQuorum = quorumService.lerSaldoQuorum(
                    user.getIdUtilizador().toString());
            log.debug("Saldo obtido do quorum: {}", saldoQuorum);

            
            if (saldoQuorum != user.getSaldo()) {
                user.setSaldo(Math.round(saldoQuorum));
                utilizadorRepository.save(user);
            }
        } catch (Exception e) {
            log.warn("Quorum não disponível para lerSaldo — usando BD: {}",
                    e.getMessage());
        }

        return SaldoResponse.builder()
                .email(user.getEmail())
                .nome(user.getNome())
                .saldoGlobal(Math.round(saldoQuorum))
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

    public List<SincronizacaoSaldoDto> obterTodosSaldos() {

        return utilizadorRepository.findAll().stream()
                .map(u -> SincronizacaoSaldoDto.builder()
                        .idUtilizador(u.getIdUtilizador().toString())
                        .saldo(u.getSaldo())
                        .versao(1)
                        .build())
                .toList();
    }
    
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

        // Envia perfil para todas as infras activas via SOAP
        List<InfraProxy> proxies = soapClient.obterClientes();
        for (InfraProxy proxy : proxies) {
            try {
                proxy.enviarPerfil(
                    user.getIdUtilizador().toString(),
                    user.getEmail(),
                    preferencias
                );
                log.debug("Perfil enviado para infra: {}", proxy.getServiceUrl());
            } catch (Exception e) {
                log.warn("Não foi possível enviar perfil para {}: {}",
                        proxy.getServiceUrl(), e.getMessage());
            }
        }
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

        
        if (request.getNovaPalavraChave() != null 
                && !request.getNovaPalavraChave().isBlank()) {
            if (request.getPalavraChaveActual() == null) {
                throw new RuntimeException(
                    "Password actual é obrigatória para alterar a password");
            }
            if (!passwordEncoder.matches(request.getPalavraChaveActual(),
                                        user.getPalavraChave())) {
                throw new RuntimeException("Password actual incorrecta");
            }
            user.setPalavraChave(passwordEncoder.encode(
                    request.getNovaPalavraChave()));
        }

        // Actualiza preferências se fornecidas
        if (request.getPreferenciaAnuncio() != null) {
            user.setPreferenciaAnuncio(request.getPreferenciaAnuncio());

            // Envia novo perfil para todas as infras via SOAP
            List<InfraProxy> proxies = soapClient.obterClientes();
            for (InfraProxy proxy : proxies) {
                try {
                    proxy.enviarPerfil(
                        user.getIdUtilizador().toString(),
                        user.getEmail(),
                        request.getPreferenciaAnuncio()
                    );
                } catch (Exception e) {
                    log.warn("Não foi possível enviar perfil para {}: {}",
                            proxy.getServiceUrl(), e.getMessage());
                }
            }
        }

        return utilizadorRepository.save(user);
    }
}