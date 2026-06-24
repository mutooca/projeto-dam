package com.anunciosloc.anunciosloc_server.service;

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

    private static final int TIMEOUT_MS = 3000;

    private record SaldoComVersao(float saldo, int versao, 
                               String infraNome, InfraProxy proxy) {}

    public float lerSaldoQuorum(String idUtilizador) {
        List<InfraProxy> proxies = soapClient.obterClientes();

        if (proxies.isEmpty()) {
            log.warn("Nenhuma réplica disponível para lerSaldo — usando BD");
            throw new RuntimeException(
                "Nenhum servidor de infraestrutura disponível");
        }

        int n = proxies.size();
        int quorum = (n / 2) + 1;

        log.debug("lerSaldoQuorum: {} réplicas, quorum={}", n, quorum);

        
        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<SaldoComVersao>> futures = new ArrayList<>();

        for (InfraProxy proxy : proxies) {
            futures.add(executor.submit(() -> {
                try {
                    LerSaldoResponse resp = proxy.lerSaldo(idUtilizador);
                    return new SaldoComVersao(
                        resp.getSaldo(),
                        resp.getVersao(),
                        proxy.getServiceUrl(),
                        proxy
                    );
                } catch (Exception e) {
                    log.warn("Réplica {} falhou no lerSaldo: {}",
                            proxy.getServiceUrl(), e.getMessage());
                    return null;
                }
            }));
        }

        executor.shutdown();

        
        List<SaldoComVersao> respostas = new ArrayList<>();
        for (Future<SaldoComVersao> future : futures) {
            try {
                SaldoComVersao resultado = future.get(TIMEOUT_MS,
                                                    TimeUnit.MILLISECONDS);
                if (resultado != null) respostas.add(resultado);
            } catch (Exception e) {
                log.warn("Timeout ou erro a aguardar réplica: {}", e.getMessage());
            }
        }

        
        if (respostas.size() < quorum) {
            throw new RuntimeException(
                "Quorum não atingido para leitura. Respostas: " +
                respostas.size() + "/" + n + " (quorum=" + quorum + ")");
        }

       
        SaldoComVersao maisRecente = respostas.stream()
                .max((a, b) -> Integer.compare(a.versao(), b.versao()))
                .orElseThrow();

        log.debug("lerSaldoQuorum: saldo={}, versao={} (de {})",
                maisRecente.saldo(), maisRecente.versao(),
                maisRecente.infraNome());

        
        List<SaldoComVersao> desactualizadas = respostas.stream()
                .filter(r -> r.versao() < maisRecente.versao())
                .toList();

        if (!desactualizadas.isEmpty()) {
            log.info("Read-repair: {} réplica(s) desactualizada(s) — a corrigir...",
                    desactualizadas.size());

            ExecutorService repairExecutor = Executors.newFixedThreadPool(
                    desactualizadas.size());

            for (SaldoComVersao desactualizada : desactualizadas) {
                repairExecutor.submit(() -> {
                    try {
                        EscreverSaldoResponse resp = desactualizada.proxy()
                                .escreverSaldo(
                                    idUtilizador,
                                    maisRecente.saldo(),
                                    maisRecente.versao()
                                );
                        if (resp.isSucesso()) {
                            log.info("Read-repair: réplica {} actualizada para versao={}",
                                    desactualizada.infraNome(), maisRecente.versao());
                        } else {
                            log.warn("Read-repair: réplica {} rejeitou: {}",
                                    desactualizada.infraNome(), resp.getMensagem());
                        }
                    } catch (Exception e) {
                        log.warn("Read-repair: falhou em {}: {}",
                                desactualizada.infraNome(), e.getMessage());
                    }
                });
            }

            repairExecutor.shutdown();
            // Não espera pelo repair — é assíncrono para não bloquear o cliente
        }
        

        return maisRecente.saldo();
    }

    public void escreverSaldoQuorum(String idUtilizador, float novoSaldo) {
        List<InfraProxy> proxies = soapClient.obterClientes();

        if (proxies.isEmpty()) {
            log.warn("Nenhuma réplica disponível para escreverSaldo");
            throw new RuntimeException(
                "Nenhum servidor de infraestrutura disponível");
        }

        int n = proxies.size();
        int quorum = (n / 2) + 1;

        log.debug("escreverSaldoQuorum: {} réplicas, quorum={}", n, quorum);

        int versaoActual = obterVersaoActual(idUtilizador, proxies, quorum);
        int novaVersao = versaoActual + 1;

        log.debug("escreverSaldoQuorum: versaoActual={}, novaVersao={}",
                  versaoActual, novaVersao);

        ExecutorService executor = Executors.newFixedThreadPool(n);
        List<Future<Boolean>> futures = new ArrayList<>();

        for (InfraProxy proxy : proxies) {
            final int versao = novaVersao;
            futures.add(executor.submit(() -> {
                try {
                    EscreverSaldoResponse resp = proxy.escreverSaldo(
                            idUtilizador, novoSaldo, versao);
                    if (resp.isSucesso()) {
                        log.debug("Réplica {} aceitou escrita versao={}",
                                  proxy.getServiceUrl(), versao);
                        return true;
                    } else {
                        log.warn("Réplica {} rejeitou escrita: {}",
                                 proxy.getServiceUrl(), resp.getMensagem());
                        return false;
                    }
                } catch (Exception e) {
                    log.warn("Réplica {} falhou na escrita: {}",
                             proxy.getServiceUrl(), e.getMessage());
                    return false;
                }
            }));
        }

        executor.shutdown();

        int confirmacoes = 0;
        for (Future<Boolean> future : futures) {
            try {
                Boolean resultado = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
                if (Boolean.TRUE.equals(resultado)) confirmacoes++;
            } catch (Exception e) {
                log.warn("Timeout a aguardar confirmação: {}", e.getMessage());
            }
        }

        if (confirmacoes < quorum) {
            throw new RuntimeException(
                "Quorum não atingido para escrita. Confirmações: " +
                confirmacoes + "/" + n + " (quorum=" + quorum + ")");
        }

        log.info("escreverSaldoQuorum: saldo={} escrito em {}/{} réplicas (versao={})",
                 novoSaldo, confirmacoes, n, novaVersao);
    }

    private int obterVersaoActual(String idUtilizador,
                                   List<InfraProxy> proxies,
                                   int quorum) {
        List<Integer> versoes = new ArrayList<>();

        for (InfraProxy proxy : proxies) {
            try {
                LerSaldoResponse resp = proxy.lerSaldo(idUtilizador);
                versoes.add(resp.getVersao());
            } catch (Exception e) {
                log.warn("Não foi possível obter versão de {}: {}",
                         proxy.getServiceUrl(), e.getMessage());
            }
        }

        if (versoes.size() < quorum) {
            throw new RuntimeException(
                "Não foi possível obter versão actual de réplicas suficientes");
        }

        // Versão mais alta entre as réplicas disponíveis
        return versoes.stream().mapToInt(Integer::intValue).max().orElse(0);
    }
}