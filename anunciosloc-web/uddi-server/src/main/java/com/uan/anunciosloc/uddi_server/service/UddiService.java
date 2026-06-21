package com.uan.anunciosloc.uddi_server.service;


import com.uan.anunciosloc.uddi_server.model.UddiRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UddiService {
    
    private final ConcurrentHashMap<String, UddiRecord> registry
            = new ConcurrentHashMap<>();

     public UddiRecord registar(String serviceName, String serviceUrl) {
        UddiRecord record = UddiRecord.builder()
                .serviceName(serviceName)
                .serviceUrl(serviceUrl)
                .registadoEm(LocalDateTime.now())
                .ultimoPing(LocalDateTime.now())
                .build();

        registry.put(serviceName, record);
        log.info("Registado: {} @ {}", serviceName, serviceUrl);
        return record;
    }
    
    public boolean cancelar(String serviceName) {
        UddiRecord removido = registry.remove(serviceName);
        if (removido != null) {
            log.info("Registo cancelado: {}", serviceName);
            return true;
        }
        return false;
    }

    // anunciosloc_server chama: pesquisar("D01_Infrastructure")
    // e recebe todos os servidores de infra do grupo D01
    public List<UddiRecord> pesquisar(String prefixo) {
        if (prefixo == null || prefixo.isBlank()) {
            return new ArrayList<>(registry.values());
        }
        return registry.values().stream()
                .filter(r -> r.getServiceName().startsWith(prefixo))
                .collect(Collectors.toList());
    }

    public Optional<UddiRecord> obter(String serviceName) {
        return Optional.ofNullable(registry.get(serviceName));
    }

     public Collection<UddiRecord> listarTodos() {
        return Collections.unmodifiableCollection(registry.values());
    }

    public boolean ping(String serviceName) {
        UddiRecord record = registry.get(serviceName);
        if (record != null) {
            record.setUltimoPing(LocalDateTime.now());
            log.debug("Ping recebido de: {}", serviceName);
            return true;
        }
        return false;
    }

    public void clear() {
        registry.clear();
        log.warn("Registo UDDI limpo");
    }
}
