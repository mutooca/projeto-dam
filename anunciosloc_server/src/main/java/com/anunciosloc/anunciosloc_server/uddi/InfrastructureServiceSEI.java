package com.anunciosloc.anunciosloc_server.uddi;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import com.anunciosloc.anunciosloc_server.uddi.dto.*;

@WebService(
    name = "InfrastructureService",
    targetNamespace = "http://infrastructura.anunciosloc.uan.com"
)
public interface InfrastructureServiceSEI {

    @WebMethod(operationName = "obterInfoInfraestrutura")
    InfraInfoResponse obterInfoInfraestrutura();

    @WebMethod(operationName = "lerSaldo")
    LerSaldoResponse lerSaldo(
        @WebParam(name = "idUtilizador") String idUtilizador
    );

    @WebMethod(operationName = "escreverSaldo")
    EscreverSaldoResponse escreverSaldo(
        @WebParam(name = "idUtilizador") String idUtilizador,
        @WebParam(name = "novoSaldo") float novoSaldo,
        @WebParam(name = "versao") int versao
    );

    @WebMethod(operationName = "obterSaldo")
    ObterSaldoResponse obterSaldo(
        @WebParam(name = "idUtilizador") String idUtilizador
    );

    @WebMethod(operationName = "ping")
    String ping();

    @WebMethod(operationName = "clear")
    void clear();
}