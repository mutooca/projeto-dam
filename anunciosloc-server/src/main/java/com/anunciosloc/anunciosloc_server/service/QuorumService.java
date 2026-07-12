package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.model.Infraestrutura;
import com.anunciosloc.anunciosloc_server.repository.InfraestruturaRepository;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.uddi.InfrastruturaSoapClient;
import com.anunciosloc.anunciosloc_server.uddi.dto.LerSaldoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.EscreverSaldoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuorumService {

    private final InfrastruturaSoapClient soapClient;
    private final InfraestruturaRepository infraRepository;

    private static final int TIMEOUT_MS = 3000;

    private record SaldoComVersao(float saldo, int versao, String infraNome) {}

    @SuppressWarnings("null")
    public float lerSaldoQuorum(String email) {
        log.info(" [QUORUM] Lendo saldo de todas as infras para: {}", email);

        List<Infraestrutura> infrasBD = infraRepository.findByAtivaTrue();
        if (infrasBD.isEmpty()) {
            log.warn("Nenhuma infraestrutura registada");
            return 0;
        }

        List<InfraProxy> proxies = new ArrayList<>();
        for (Infraestrutura infra : infrasBD) {
            try {
                InfraProxy proxy = soapClient.obterClientePorNome(infra.getNome());
                if (proxy != null) {
                    proxies.add(proxy);
                }
            } catch (Exception e) {
                log.warn(" Não foi possível obter proxy para {}: {}", infra.getNome(), e.getMessage());
            }
        }

        if (proxies.isEmpty()) {
            log.warn("Nenhum proxy disponível");
            return 0;
        }

        int n = proxies.size();
        int quorum = (n / 2) + 1;
        log.info(" {} réplicas, quorum necessário: {}", n, quorum);

        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<SaldoComVersao>> futures = new ArrayList<>();

        for (InfraProxy proxy : proxies) {
            futures.add(executor.submit(() -> {
                try {
                    LerSaldoResponse response = proxy.lerSaldo(email);
                    if (response != null && response.isSucesso()) {
                        log.debug(" Saldo de {}: {} (versão {})", 
                            proxy.getServiceUrl(), response.getSaldo(), response.getVersao());
                        return new SaldoComVersao(
                            response.getSaldo(),
                            response.getVersao(),
                            proxy.getServiceUrl()
                        );
                    }
                    return null;
                } catch (Exception e) {
                    log.warn(" Falha em {}: {}", proxy.getServiceUrl(), e.getMessage());
                    return null;
                }
            }));
        }

        executor.shutdown();

        List<SaldoComVersao> respostas = new ArrayList<>();
        for (Future<SaldoComVersao> future : futures) {
            try {
                SaldoComVersao resultado = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (resultado != null) {
                    respostas.add(resultado);
                }
            } catch (Exception e) {
                log.warn("Timeout: {}", e.getMessage());
            }
        }

        if (respostas.size() < quorum) {
            log.warn(" Quorum não atingido: {}/{} (necessário: {})", 
                respostas.size(), n, quorum);
            if (respostas.isEmpty()) {
                return 0;
            }
        }

        float saldoTotal = respostas.stream()
            .map(r -> r.saldo())
            .reduce(0f, Float::sum);

        log.info(" Saldo total: {} (de {} infraestruturas)", saldoTotal, respostas.size());

        return saldoTotal;
    }

    public void escreverSaldoQuorum(String email, float novoSaldo, int novaVersao) {
        log.info(" [QUORUM] Escrevendo saldo {} (versão {}) para {}", novoSaldo, novaVersao, email);

        List<InfraProxy> proxies = soapClient.obterClientes();
        if (proxies.isEmpty()) {
            log.warn("Nenhuma réplica disponível");
            return;
        }

        int n = proxies.size();
        int quorum = (n / 2) + 1;
        log.info(" {} réplicas, quorum necessário: {}", n, quorum);

        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (InfraProxy proxy : proxies) {
            futures.add(executor.submit(() -> {
                try {
                    EscreverSaldoResponse response = proxy.escreverSaldo(email, novoSaldo, novaVersao);
                    if (response != null && response.isSucesso()) {
                        log.debug(" Escrito em {}", proxy.getServiceUrl());
                        return true;
                    } else {
                        log.warn(" Rejeitado por {}: {}", 
                            proxy.getServiceUrl(), 
                            response != null ? response.getMensagem() : "null");
                        return false;
                    }
                } catch (Exception e) {
                    log.warn("Falha em {}: {}", proxy.getServiceUrl(), e.getMessage());
                    return false;
                }
            }));
        }

        executor.shutdown();

        int confirmacoes = 0;
        for (Future<Boolean> future : futures) {
            try {
                if (Boolean.TRUE.equals(future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS))) {
                    confirmacoes++;
                }
            } catch (Exception e) {
                log.warn("Timeout: {}", e.getMessage());
            }
        }

        if (confirmacoes < quorum) {
            log.warn(" Quorum não atingido: {}/{} (necessário: {})", 
                confirmacoes, n, quorum);
        } else {
            log.info(" Saldo {} escrito em {}/{} réplicas", novoSaldo, confirmacoes, n);
        }
    }
}