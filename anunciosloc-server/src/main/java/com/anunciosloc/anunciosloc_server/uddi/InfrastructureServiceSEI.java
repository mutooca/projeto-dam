package com.anunciosloc.anunciosloc_server.uddi;

import java.util.List;

import com.anunciosloc.anunciosloc_server.uddi.dto.*;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(name = "InfrastructureService", targetNamespace = "http://infrastructura.anunciosloc.uan.com")
public interface InfrastructureServiceSEI {

        @WebMethod(operationName = "obterInfoInfraestrutura")
        @WebResult(name = "InfraInfoResponse")
        InfraInfoResponse obterInfoInfraestrutura();

        @WebMethod(operationName = "ping")
        @WebResult(name = "pingResponse", targetNamespace = "http://infrastructura.anunciosloc.uan.com")
        PingResponse ping();

        @WebMethod(operationName = "lerSaldo")
        @WebResult(name = "LerSaldoResponse")
        LerSaldoResponse lerSaldo(@WebParam(name = "email") String email);

        @WebMethod(operationName = "escreverSaldo")
        @WebResult(name = "EscreverSaldoResponse")
        EscreverSaldoResponse escreverSaldo(
                        @WebParam(name = "email") String email,
                        @WebParam(name = "novoSaldo") float novoSaldo,
                        @WebParam(name = "versao") int versao);

        @WebMethod(operationName = "obterSaldo")
        @WebResult(name = "ObterSaldoResponse")
        ObterSaldoResponse obterSaldo(@WebParam(name = "email") String email);

        @WebMethod(operationName = "criarLocal")
        @WebResult(name = "CriarLocalResponse")
        String criarLocal(@WebParam(name = "request") CriarLocalRequestSOAP request);

        @WebMethod(operationName = "listarLocais")
        @WebResult(name = "ListarLocaisResponse")
        ListarLocaisResponse listarLocais(
                        @WebParam(name = "latUtilizador") Double latUtilizador,
                        @WebParam(name = "lonUtilizador") Double lonUtilizador);

        @WebMethod(operationName = "postarAnuncio")
        @WebResult(name = "PostarAnuncioResponse")
        String postarAnuncio(@WebParam(name = "request") PostarAnuncioRequestSOAP request);

        @WebMethod(operationName = "receberAnuncios")
        @WebResult(name = "ReceberAnunciosResponse")
        ReceberAnunciosResponse receberAnuncios(
                        @WebParam(name = "request") ReceberAnunciosRequestSOAP request);

        @WebMethod(operationName = "listarAnunciosPorEmail")
        @WebResult(name = "AnunciosResponse") 
        List<AnuncioInfoSOAP> listarAnunciosPorEmail(
                        @WebParam(name = "email") String email); 

        @WebMethod(operationName = "marcarComoLido")
        @WebResult(name = "MensagemResponse")
        MensagemResponse marcarComoLido(
                        @WebParam(name = "idAnuncio") String idAnuncio,
                        @WebParam(name = "emailUtilizador") String emailUtilizador);
}
