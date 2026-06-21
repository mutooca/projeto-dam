package com.uan.anunciosloc.infrastructura_server.soap;

import com.uan.anunciosloc.infrastructura_server.soap.dto.CriarLocalRequest;
import com.uan.anunciosloc.infrastructura_server.soap.dto.CriarLocalResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.DefinirRestricaoRequest;
import com.uan.anunciosloc.infrastructura_server.soap.dto.DefinirRestricaoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.EscreverSaldoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.InfraInfoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.LerSaldoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.ObterSaldoResponse;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;


@WebService(
    name = "InfrastructureService",
    targetNamespace = "http://infrastructura.anunciosloc.uan.com"
)
public interface InfraestruturaServiceSEI {

   

    @WebMethod(operationName = "obterInfoInfraestrutura")
    InfraInfoResponse obterInfoInfraestrutura();

    @WebMethod(operationName = "criarLocal")
    CriarLocalResponse criarLocal(
        @WebParam(name = "request") CriarLocalRequest request
    );

    @WebMethod(operationName = "definirRestricao")
    DefinirRestricaoResponse definirRestricao(
        @WebParam(name = "request") DefinirRestricaoRequest request
    );

    @WebMethod(operationName = "obterSaldo")
    ObterSaldoResponse obterSaldo(
        @WebParam(name = "idUtilizador") String idUtilizador
    );


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


    @WebMethod(operationName = "ping")
    String ping();

    @WebMethod(operationName = "clear")
    void clear();

    @WebMethod(operationName = "initInfraestrutura")
    void initInfraestrutura(
        @WebParam(name = "capacidade") int capacidade,
        @WebParam(name = "bonusEntrega") int bonusEntrega,
        @WebParam(name = "custoPost") int custoPost
    );
}