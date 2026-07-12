package com.uan.anunciosloc.infrastructura_server.soap;

import java.util.List;

import com.uan.anunciosloc.infrastructura_server.soap.dto.AnuncioInfo;
import com.uan.anunciosloc.infrastructura_server.soap.dto.CriarLocalRequest;
import com.uan.anunciosloc.infrastructura_server.soap.dto.CriarLocalResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.DiasInatividadeResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.EscreverSaldoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.LerSaldoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.ObterInfraResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.ListarLocaisResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.MensagemResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.ObterSaldoResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.PingResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.PostarAnuncioRequest;
import com.uan.anunciosloc.infrastructura_server.soap.dto.PostarAnuncioResponse;
import com.uan.anunciosloc.infrastructura_server.soap.dto.ReceberAnunciosRequest;
import com.uan.anunciosloc.infrastructura_server.soap.dto.ReceberAnunciosResponse;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

@WebService(name = "InfrastructureService", targetNamespace = "http://infrastructura.anunciosloc.uan.com")
public interface InfrastructureServiceSEI {

        @WebMethod(operationName = "obterInfoInfraestrutura")
        @WebResult(name = "ObterInfraResponse")
        ObterInfraResponse obterInfoInfraestrutura();

        @WebMethod(operationName = "criarInfraestrutura")
        MensagemResponse criarInfraestrutura(
                        @WebParam(name = "capacidade") int capacidade,
                        @WebParam(name = "bonusEntrega") int bonusEntrega,
                        @WebParam(name = "custoPost") int custoPost,
                        @WebParam(name = "latitude") double latitude,
                        @WebParam(name = "longitude") double longitude,
                        @WebParam(name = "raio") double raio);

        @WebMethod(operationName = "criarLocal")
        @WebResult(name = "CriarLocalResponse")
        CriarLocalResponse criarLocal(
                        @WebParam(name = "request") CriarLocalRequest request);

        @WebMethod(operationName = "listarLocais")
        @WebResult(name = "ListarLocaisResponse")
        ListarLocaisResponse listarLocais(
                        @WebParam(name = "latUtilizador") Double latUtilizador,
                        @WebParam(name = "lonUtilizador") Double lonUtilizador);
        /*
         * *
         * 
         * @WebMethod(operationName = "definirRestricao")
         * DefinirRestricaoResponse definirRestricao(
         * 
         * @WebParam(name = "request") DefinirRestricaoRequest request
         * );
         */

        @WebMethod(operationName = "lerSaldo")
        @WebResult(name = "LerSaldoResponse")
        LerSaldoResponse lerSaldo(
                        @WebParam(name = "email") String email);

        @WebMethod(operationName = "escreverSaldo")
        @WebResult(name = "EscreverSaldoResponse")
        EscreverSaldoResponse escreverSaldo(
                        @WebParam(name = "email") String email,
                        @WebParam(name = "novoSaldo") float novoSaldo,
                        @WebParam(name = "versao") int versao);

        @WebMethod(operationName = "obterSaldo")
        @WebResult(name = "ObterSaldoResponse")
        ObterSaldoResponse obterSaldo(
                        @WebParam(name = "email") String email);

        @WebMethod(operationName = "postarAnuncio")
        @WebResult(name = "PostarAnuncioResponse")
        PostarAnuncioResponse postarAnuncio(
                        @WebParam(name = "request") PostarAnuncioRequest request);

        @WebMethod(operationName = "receberAnuncios")
        @WebResult(name = "ReceberAnunciosResponse")
        ReceberAnunciosResponse receberAnuncios(
                        @WebParam(name = "request") ReceberAnunciosRequest request);

        @WebMethod(operationName = "listarAnunciosPorEmail")
        @WebResult(name = "AnunciosResponse")
        List<AnuncioInfo> listarAnunciosPorEmail(
                        @WebParam(name = "email") String email);

        @WebMethod(operationName = "marcarComoLido")
        MensagemResponse marcarComoLido(
                        @WebParam(name = "idAnuncio") String idAnuncio,
                        @WebParam(name = "emailUtilizador") String emailUtilizador);

        @WebMethod(operationName = "verificarInatividade")
        @WebResult(name = "MensagemResponse")
        MensagemResponse verificarInatividade();

        @WebMethod(operationName = "obterDiasInatividade")
        @WebResult(name = "DiasInatividadeResponse")
        DiasInatividadeResponse obterDiasInatividade(
                        @WebParam(name = "email") String email);

        /*
         * *
         * 
         * @WebMethod(operationName = "sincronizarUtilizador")
         * MensagemResponse sincronizarUtilizador(
         * 
         * @WebParam(name = "idUtilizador") String idUtilizador,
         * 
         * @WebParam(name = "email") String email,
         * 
         * @WebParam(name = "nome") String nome,
         * 
         * @WebParam(name = "saldo") int saldo
         * );
         */
        @WebMethod(operationName = "ping")
        @WebResult(name = "pingResponse", targetNamespace = "http://infrastructura.anunciosloc.uan.com")
        PingResponse ping();

        @WebMethod(operationName = "clear")
        void clear();
}