package com.uan.anunciosloc.infrastructura_server.service;

import com.uan.anunciosloc.infrastructura_server.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
@Service
public class InfraEstadoService {

    @Value("${infra.nome:D01_Infrastructure1}")
    private String infraNome;

    @Value("${infra.capacidade:100}")
    private int capacidade;

    @Value("${infra.bonus-entrega:2}")
    private int bonusEntrega;

    @Value("${infra.custo-post:1}")
    private int custoPost;

    @Value("${infra.public-url:http://localhost:8081}")
    private String publicUrl;

  
    private final ConcurrentHashMap<String, LocalInfo> locais
            = new ConcurrentHashMap<>();

    
    private final ConcurrentHashMap<String, RestricaoInfo> restricoes
            = new ConcurrentHashMap<>();

  
    private final ConcurrentHashMap<String, LocalDateTime> conectados
            = new ConcurrentHashMap<>();

    // Saldos replica: chave = idUtilizador
    private final ConcurrentHashMap<String, SaldoReplicaInfo> saldosReplica
            = new ConcurrentHashMap<>();

    // Estatísticas da sessão
    private final AtomicInteger totalAnuncios  = new AtomicInteger(0);
    private final AtomicInteger totalEntregas  = new AtomicInteger(0);
    private final AtomicInteger totalConexoes  = new AtomicInteger(0);

    

    public String getInfraNome()   { return infraNome; }
    public int getCapacidade()     { return capacidade; }
    public int getBonusEntrega()   { return bonusEntrega; }
    public int getCustoPost()      { return custoPost; }
    public String getPublicUrl()   { return publicUrl; }

    

    public int getTotalAnuncios()  { return totalAnuncios.get(); }
    public int getTotalEntregas()  { return totalEntregas.get(); }
    public int getTotalConexoes()  { return totalConexoes.get(); }
    public int getConectadosAgora(){ return conectados.size(); }

    public void incrementarAnuncios() { totalAnuncios.incrementAndGet(); }
    public void incrementarEntregas() { totalEntregas.incrementAndGet(); }

    

    public LocalInfo criarLocal(LocalInfo local) {
        String id = UUID.randomUUID().toString();
        local.setId(id);
        locais.put(id, local);
        log.info("Local criado: {} ({})", local.getNome(), id);
        return local;
    }

    public Optional<LocalInfo> obterLocal(String id) {
        return Optional.ofNullable(locais.get(id));
    }

    public Collection<LocalInfo> listarLocais() {
        return Collections.unmodifiableCollection(locais.values());
    }

    

    public RestricaoInfo adicionarRestricao(RestricaoInfo restricao) {
        String id = UUID.randomUUID().toString();
        restricao.setId(id);
        restricoes.put(id, restricao);
        log.info("Restrição definida: {} = {}", restricao.getTipoRestricao(),
                                                restricao.getValorRestricao());
        return restricao;
    }

    public Collection<RestricaoInfo> listarRestricoes() {
        return Collections.unmodifiableCollection(restricoes.values());
    }

  

    public void registarConexao(String emailUtilizador) {
        boolean novo = !conectados.containsKey(emailUtilizador);
        conectados.put(emailUtilizador, LocalDateTime.now());
        if (novo) {
            totalConexoes.incrementAndGet();
            log.debug("Utilizador conectado: {}", emailUtilizador);
        }
    }

    public void removerConexao(String emailUtilizador) {
        conectados.remove(emailUtilizador);
        log.debug("Utilizador desconectado: {}", emailUtilizador);
    }

    public boolean estaConectado(String emailUtilizador) {
        return conectados.containsKey(emailUtilizador);
    }

    public boolean capacidadeDisponivel() {
        return conectados.size() < capacidade;
    }

    

    public SaldoReplicaInfo lerSaldo(String idUtilizador) {
        // Se não existe, devolve saldo inicial de 10 pontos com versão 0
        return saldosReplica.getOrDefault(idUtilizador,
                SaldoReplicaInfo.builder()
                        .idUtilizador(idUtilizador)
                        .saldo(10.0f)
                        .versao(0)
                        .actualizadoEm(LocalDateTime.now())
                        .build());
    }

    public boolean escreverSaldo(String idUtilizador, float novoSaldo, int versao) {
        SaldoReplicaInfo actual = saldosReplica.get(idUtilizador);

        // Rejeita se a versão recebida for mais antiga que a réplica local
        if (actual != null && actual.getVersao() >= versao) {
            log.warn("escreverSaldo rejeitado: versão {} <= actual {}",
                     versao, actual.getVersao());
            return false;
        }

        saldosReplica.put(idUtilizador, SaldoReplicaInfo.builder()
                .idUtilizador(idUtilizador)
                .saldo(novoSaldo)
                .versao(versao)
                .actualizadoEm(LocalDateTime.now())
                .build());

        log.debug("Saldo escrito: utilizador={}, saldo={}, versao={}",
                  idUtilizador, novoSaldo, versao);
        return true;
    }

    

    public void clear() {
        locais.clear();
        restricoes.clear();
        conectados.clear();
        saldosReplica.clear();
        totalAnuncios.set(0);
        totalEntregas.set(0);
        totalConexoes.set(0);
        log.warn("Estado da infraestrutura limpo (clear)");
    }
}