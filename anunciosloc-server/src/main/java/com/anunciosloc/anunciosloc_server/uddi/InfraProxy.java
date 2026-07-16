package com.anunciosloc.anunciosloc_server.uddi;

import java.util.List;

import com.anunciosloc.anunciosloc_server.uddi.dto.AnuncioInfoSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.CriarLocalRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.CriarLocalResponseSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.EditarLocalRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.EditarLocalResponseSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.EscreverSaldoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.InfraInfoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.LerSaldoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.ListarLocaisResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.ResultadoLeituraSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.MensagemResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.ObterSaldoResponse;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.PostarAnuncioResponseSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.ReceberAnunciosRequestSOAP;
import com.anunciosloc.anunciosloc_server.uddi.dto.ReceberAnunciosResponse;

public interface InfraProxy {
    String getServiceUrl();

    InfraInfoResponse obterInfoInfraestrutura();

    // LerSaldoResponse lerSaldo(String idUtilizador);

    String ping();

    // void enviarPerfil(String idUtilizador, String email, String preferencias);

    LerSaldoResponse lerSaldo(String email);

    EscreverSaldoResponse escreverSaldo(String email, float novoSaldo, int versao);

    ObterSaldoResponse obterSaldo(String email);

    CriarLocalResponseSOAP criarLocal(CriarLocalRequestSOAP request);

    MensagemResponse eliminarLocal(String idLocal, String emailUtilizador);

    EditarLocalResponseSOAP editarLocal(EditarLocalRequestSOAP request);

    ListarLocaisResponse listarLocais(Double lat, Double lon);

    PostarAnuncioResponseSOAP postarAnuncio(PostarAnuncioRequestSOAP request);

    ReceberAnunciosResponse receberAnuncios(ReceberAnunciosRequestSOAP request);

    MensagemResponse eliminarAnuncio(String idAnuncio, String emailUtilizador, String role);

    List<AnuncioInfoSOAP> listarAnunciosPorEmail(String email);

    ResultadoLeituraSOAP marcarComoLido(String idAnuncio, String emailUtilizador);


    String obterUltimoPost(String email);

    long contarAnunciosPorEmail(String email);

    long contarEntregasPorEmail(String email);

}
