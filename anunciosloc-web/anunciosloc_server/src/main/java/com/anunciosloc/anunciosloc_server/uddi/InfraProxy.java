package com.anunciosloc.anunciosloc_server.uddi;

import com.anunciosloc.anunciosloc_server.uddi.dto.EscreverSaldoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.LerSaldoResponse;

public interface InfraProxy {
        String getServiceUrl();
        InfraInfoResponse obterInfoInfraestrutura();
        LerSaldoResponse lerSaldo(String idUtilizador);
        EscreverSaldoResponse escreverSaldo(String idUtilizador,
                                             float novoSaldo,
                                             int versao);
        String ping();
    }
