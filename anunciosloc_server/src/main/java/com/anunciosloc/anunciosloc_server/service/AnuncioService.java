package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.PostarAnuncioRequest;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final LocalRepository localRepository;
    private final InfraestruturaRepository infraRepository;
    private final SaldoUtilizadorRepository saldoRepository;
    private final EntregaAnuncioRepository entregaRepository;
    private final ConexaoRepository conexaoRepository;
    private final RegistoEstatisticoRepository estatisticoRepository;
    private final InfraDisponibilidadeService infraDisponibilidade;

      List<String> categorias = List.of();
    

    @SuppressWarnings("null")
    @Transactional
    public Anuncio postarAnuncio(PostarAnuncioRequest request) {

        Utilizador autor = utilizadorRepository.findByEmail(request.getEmailAutor())
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (!"USER".equals(autor.getRole())) {
            throw new RuntimeException("Apenas clientes podem publicar anúncios");
        }
        Local local = localRepository.findById(request.getLocalId())
            .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        Infraestrutura infra = local.getInfraestrutura();

        if (!infra.isAtiva()) {
            throw new RuntimeException("Infraestrutura não está activa");
        }

        infraDisponibilidade.verificarDisponibilidade(infra.getNome());

        if (autor.getSaldo() < infra.getCustoPost()) {
            throw new RuntimeException(
                "Saldo insuficiente. Saldo actual: " + autor.getSaldo()
                + " | Custo: " + infra.getCustoPost());
        }
        SaldoUtilizador saldoInfra = saldoRepository
            .findByUtilizadorAndInfraestrutura(autor, infra)
            .orElseGet(() -> {
                SaldoUtilizador novo = new SaldoUtilizador();
                novo.setUtilizador(autor);
                novo.setInfraestrutura(infra);
                novo.setSaldoParcial(autor.getSaldo());
                novo.setPontosGanhos(0);
                novo.setPontosGastos(0);
                return novo;
            });
        autor.setSaldo(autor.getSaldo() - infra.getCustoPost());
        saldoInfra.setSaldoParcial(saldoInfra.getSaldoParcial() - infra.getCustoPost());
        saldoInfra.setPontosGastos(saldoInfra.getPontosGastos() + infra.getCustoPost());
        saldoInfra.setUltimaAtualizacao(LocalDateTime.now());

        
        if (autor.getDataUltimoPost() != null) {
            long diasSemPost = java.time.temporal.ChronoUnit.DAYS
                .between(autor.getDataUltimoPost(), LocalDateTime.now());
            long semanasSemPost = diasSemPost / 7;
            if (semanasSemPost >= 1) {
                
                int penalizacao = (int) semanasSemPost;
                int novoSaldo = Math.max(0, autor.getSaldo() - penalizacao);
                int novoSaldoParcial = Math.max(0, saldoInfra.getSaldoParcial() - penalizacao);
                
                autor.setSaldo(novoSaldo);
                saldoInfra.setSaldoParcial(novoSaldoParcial);
            }
        }
        autor.setDataUltimoPost(LocalDateTime.now());

        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo(request.getTitulo());
        anuncio.setConteudo(request.getConteudo());
        anuncio.setCategoria(request.getCategoria());
        anuncio.setDataPublicacao(LocalDateTime.now());
        anuncio.setEstado("ATIVO");
        anuncio.setLocal(local);
        anuncio.setAutor(autor);
        anuncio.setInfraestrutura(infra);

        infra.setTotalAnuncios(infra.getTotalAnuncios() + 1);

    
        if (infra.getRegistoEstatistico() != null) {
            RegistoEstatistico regEst = infra.getRegistoEstatistico();
            regEst.setTotalAnuncio(regEst.getTotalAnuncio() + 1);
            regEst.setActualizadoEm(LocalDateTime.now());
            estatisticoRepository.save(regEst);
        }
        utilizadorRepository.save(autor);
        saldoRepository.save(saldoInfra);
        infraRepository.save(infra);

        return anuncioRepository.save(anuncio);
    }

    
@Transactional
public List<Anuncio> receberAnuncios(String email, UUID infraId,
                                      Double latUtilizador,
                                      Double lonUtilizador) {

    Utilizador user = utilizadorRepository.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

    if (!"USER".equals(user.getRole())) {
        throw new RuntimeException("Apenas clientes podem receber anúncios");
    }

    @SuppressWarnings("null")
    Infraestrutura infra = infraRepository.findById(infraId)
        .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

    if (!infra.isAtiva()) {
        throw new RuntimeException("Infraestrutura não está activa");
    }

    InfraProxy proxy = infraDisponibilidade.verificarDisponibilidade(infra.getNome());
    proxy.obterInfoInfraestrutura();

    //Verifica se esta dentro da infra e dentro de um local da infra
    Local localDoUtilizador = null;

    for (Local local : infra.getLocais()) {
        if (local.getCoordenadaGps() == null) continue;

        CoordenadaGps gps = local.getCoordenadaGps();

        
        if (latUtilizador != null && lonUtilizador != null) {
            double distancia = HaversineUtil.calcularDistancia(
                    latUtilizador, lonUtilizador,
                    gps.getLatitude(), gps.getLongitude()
            );
            if (distancia <= gps.getRaio()) {
                localDoUtilizador = local;
                break;
            }
        }

        
        if (local.getCoordenadaWifi() != null
                && latUtilizador == null && lonUtilizador == null) {
            localDoUtilizador = local;
            break;
        }
    }

    if (localDoUtilizador == null) {
        throw new RuntimeException(
            "Não está dentro do raio de nenhum local desta infraestrutura. " +
            "Aproxime-se de um local para receber anúncios.");
    }

    
    Conexao conexao = conexaoRepository
        .findByUtilizadorAndInfraestrutura(user, infra)
        .orElseGet(() -> {
            Conexao nova = new Conexao();
            nova.setUtilizador(user);
            nova.setInfraestrutura(infra);
            nova.setDataConexao(LocalDateTime.now());
            infra.setTotalConexoes(infra.getTotalConexoes() + 1);
            return nova;
        });
    conexao.setDataConexao(LocalDateTime.now());
    conexaoRepository.save(conexao);

    
    List<Anuncio> anuncios;
    boolean temPreferencia = user.getPreferenciaAnuncio() != null
                          && !user.getPreferenciaAnuncio().isBlank();

    if (temPreferencia) {
        List<String> categorias = List.of(
                user.getPreferenciaAnuncio().split(","));
        anuncios = anuncioRepository
                .findByLocalAndEstadoAndCategoriaIn(
                        localDoUtilizador, "ATIVO", categorias);
    } else {
        anuncios = anuncioRepository
                .findByLocal(localDoUtilizador);
    }

    //entrega ebonus
    int totalEntregues = 0;

    for (Anuncio anuncio : anuncios) {
        boolean jaEntregue = entregaRepository
                .existsByAnuncioAndUtilizador(anuncio, user);
        if (jaEntregue) continue;

        //Regista entrega como PENDENTE so passa a LIDA quando o user marcar como lido
        EntregaAnuncio entrega = new EntregaAnuncio();
        entrega.setAnuncio(anuncio);
        entrega.setInfraestrutura(infra);
        entrega.setUtilizador(user);
        entrega.setNumEntrega(++totalEntregues);
        entrega.setDataEntrega(LocalDateTime.now());
        entrega.setEstadoEntrega("PENDENTE");
        entregaRepository.save(entrega);

        //Bonus ao autor por cada utilizador que recebe
        Utilizador autor = anuncio.getAutor();
        autor.setSaldo(autor.getSaldo() + infra.getBonusEntrega());
        SaldoUtilizador saldoAutor = saldoRepository
            .findByUtilizadorAndInfraestrutura(autor, infra)
            .orElseGet(() -> {
                SaldoUtilizador novo = new SaldoUtilizador();
                novo.setUtilizador(autor);
                novo.setInfraestrutura(infra);
                novo.setPontosGanhos(0);
                novo.setPontosGastos(0);
                return novo;
            });
        saldoAutor.setSaldoParcial(
                saldoAutor.getSaldoParcial() + infra.getBonusEntrega());
        saldoAutor.setPontosGanhos(
                saldoAutor.getPontosGanhos() + infra.getBonusEntrega());
        saldoAutor.setUltimaAtualizacao(LocalDateTime.now());
        utilizadorRepository.save(autor);
        saldoRepository.save(saldoAutor);
    }

    if (totalEntregues > 0) {
        infra.setTotalEntregas(infra.getTotalEntregas() + totalEntregues);
        infraRepository.save(infra);
    }

    return anuncios;
}



    @Transactional
    public void marcarComoLido(UUID anuncioId, String emailUtilizador) {
        Utilizador user = utilizadorRepository.findByEmail(emailUtilizador)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        @SuppressWarnings("null")
        Anuncio anuncio = anuncioRepository.findById(anuncioId)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

        EntregaAnuncio entrega = entregaRepository
            .findByAnuncioAndUtilizador(anuncio, user)
            .orElseThrow(() -> new RuntimeException(
                "Anúncio não foi entregue a este utilizador"));

        if ("LIDO".equals(entrega.getEstadoEntrega())) {
            throw new RuntimeException("Anúncio já foi marcado como lido");
        }

        entrega.setEstadoEntrega("LIDO");
        entrega.setDataEntrega(LocalDateTime.now());
        entregaRepository.save(entrega);

        log.info("Anúncio {} marcado como lido por {}", anuncioId, emailUtilizador);
    }

    public List<Anuncio> listarAnunciosPorLocal(@NonNull UUID localId) {
        Local local = localRepository.findById(localId)
            .orElseThrow(() -> new RuntimeException("Local não encontrado"));
        return anuncioRepository.findByLocal(local);
    }

    public List<Anuncio> listarAnunciosPorUtilizador(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        return anuncioRepository.findByAutor(user);
    }

    public List<Anuncio> listarAnunciosPorInfraestrutura(@NonNull UUID infraId) {
        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));
        return anuncioRepository.findByInfraestrutura(infra);
    }


    @SuppressWarnings("null")
    @Transactional
    public void removerAnuncio(UUID anuncioId, String emailUtilizador) {

        Utilizador utilizador = utilizadorRepository.findByEmail(emailUtilizador)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        Anuncio anuncio = anuncioRepository.findById(anuncioId)
            .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

        boolean ehAutor = anuncio.getAutor().getIdUtilizador()
                                .equals(utilizador.getIdUtilizador());
        boolean ehAdmin = "ADMIN".equals(utilizador.getRole());

        if (!ehAutor && !ehAdmin) {
            throw new RuntimeException(
                "Apenas o autor ou um administrador pode eliminar este anúncio.");
        }
        anuncio.setEstado("REMOVIDO");
        anuncioRepository.save(anuncio);

        log.info("Anúncio '{}' removido por '{}'", anuncioId, emailUtilizador);
    }
}