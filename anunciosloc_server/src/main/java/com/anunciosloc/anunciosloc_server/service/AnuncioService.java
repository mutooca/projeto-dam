package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.AnuncioResponse;
import com.anunciosloc.anunciosloc_server.dto.PostarAnuncioRequest;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import com.anunciosloc.anunciosloc_server.uddi.InfraProxy;
import com.anunciosloc.anunciosloc_server.util.HaversineUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final CacheManager cacheManager;
    private final QuorumService quorumService;
    private final PerfilUtilizadorRepository perfilUtilizadorRepository;


    List<String> categorias = List.of();  
    

    @SuppressWarnings("null")
    @Transactional
    @CacheEvict(value = "saldo", key = "#request.emailAutor")
    public AnuncioResponse postarAnuncio(PostarAnuncioRequest request) {

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
        anuncio.setTipoPolitica(request.getTipoPolitica());
        anuncio.setPoliticaFiltro(request.getPoliticaFiltro());
        anuncio.setVisivelDe(request.getVisivelDe() != null
                ? request.getVisivelDe() : LocalDateTime.now());
        anuncio.setVisivelAte(request.getVisivelAte());

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

        try {
            quorumService.escreverSaldoQuorum(
                autor.getIdUtilizador().toString(),
                autor.getSaldo()
            );
            log.debug("Saldo do autor replicado no quorum: {}", autor.getSaldo());
        } catch (Exception e) {
            log.warn("Quorum não disponível em postarAnuncio: {}", e.getMessage());
            
        }

        Anuncio salvo = anuncioRepository.save(anuncio);

            return  AnuncioResponse.builder()
                    .id(salvo.getIdAnuncio())
                    .titulo(salvo.getTitulo())
                    .conteudo(salvo.getConteudo())
                    .categoria(salvo.getCategoria())
                    .estado(salvo.getEstado())
                    .dataPublicacao(salvo.getDataPublicacao())
                    .visivelDe(salvo.getVisivelDe())
                    .visivelAte(salvo.getVisivelAte())
                    .tipoPolitica(salvo.getTipoPolitica())
                    .politicaFiltro(salvo.getPoliticaFiltro())
                    .idLocal(local.getIdLocal().toString())
                    .nomeLocal(local.getNome())
                    .autorEmail(autor.getEmail())
                    .nomeAutor(autor.getNome())
                    .build();
    }

        
    @SuppressWarnings("null")
    @Transactional
    public List<Anuncio> receberAnuncios(String email, UUID infraId,
                                        Double latUtilizador,
                                        Double lonUtilizador) {

        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        if (!"USER".equals(user.getRole())) {
            throw new RuntimeException("Apenas clientes podem receber anúncios");
        }

        
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

        /* 
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
        }*/

        List<Anuncio> todosAnuncios = anuncioRepository.findByLocal(localDoUtilizador);

        List<Anuncio> anuncios = todosAnuncios.stream()
            
            .filter(a -> "ATIVO".equals(a.getEstado()))
            // 2. Dentro da janela de tempo
            .filter(a -> dentroJanelaTempo(a))
            // 3. Utilizador passa na política do anúncio
            .filter(a -> passaNaPolitica(a, user))
            
            .filter(a -> {
                if (user.getPreferenciaAnuncio() == null
                        || user.getPreferenciaAnuncio().isBlank()) {
                    return true; // sem preferência → recebe todos
                }
                List<String> categorias = List.of(
                        user.getPreferenciaAnuncio().split(","));
                return categorias.contains(a.getCategoria());
            })
            .toList();



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

            
            try {
                quorumService.escreverSaldoQuorum(
                    autor.getIdUtilizador().toString(),
                    autor.getSaldo()
                );
                log.debug("Saldo do autor replicado no quorum: {}", autor.getSaldo());
            } catch (Exception e) {
                log.warn("Quorum não disponível em receberAnuncios: {}", e.getMessage());
            }
             Cache saldoCache = cacheManager.getCache("saldo");
            if (saldoCache != null) {
                saldoCache.evict(autor.getEmail());
            }
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

    @SuppressWarnings("null")
    @Cacheable(value = "anuncios", key = "#localId")
    public List<AnuncioResponse> listarAnunciosPorLocal(UUID localId) {
    Local local = localRepository.findById(localId)
        .orElseThrow(() -> new RuntimeException("Local não encontrado"));
    return anuncioRepository.findByLocal(local).stream()
            .map(this::toResponse).toList();
}

    @Cacheable(value = "anuncios", key = "#email")
    public List<AnuncioResponse> listarAnunciosPorUtilizador(String email) {
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));
        return anuncioRepository.findByAutor(user).stream()
                .map(this::toResponse).toList();
    }


    public List<Anuncio> listarAnunciosPorInfraestrutura(@NonNull UUID infraId) {
        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));
        return anuncioRepository.findByInfraestrutura(infra);
    }


    @SuppressWarnings("null")
    @Transactional
    @CacheEvict(value = "anuncios", allEntries = true)
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

    // Verifica se o anúncio está dentro da janela de tempo
private boolean dentroJanelaTempo(Anuncio anuncio) {
    LocalDateTime agora = LocalDateTime.now();

    if (anuncio.getVisivelDe() != null && agora.isBefore(anuncio.getVisivelDe())) {
        return false; // ainda não começou
    }
    if (anuncio.getVisivelAte() != null && agora.isAfter(anuncio.getVisivelAte())) {
        return false; // já expirou
    }
    return true;
}

// Verifica se o utilizador passa na política do anúncio
private boolean passaNaPolitica(Anuncio anuncio, Utilizador user) {
    // Sem política → todos recebem
    if (anuncio.getTipoPolitica() == null || anuncio.getTipoPolitica().isBlank()) {
        return true;
    }
    if (anuncio.getPoliticaFiltro() == null || anuncio.getPoliticaFiltro().isBlank()) {
        return true;
    }

    // Obtém pares do perfil do utilizador
    List<PerfilUtilizador> perfil = perfilUtilizadorRepository.findByUtilizador(user);

    // Parse dos pares da política: "profissao=Estudante,bairro=Maianga"
    String[] pares = anuncio.getPoliticaFiltro().split(",");

    boolean corresponde = java.util.Arrays.stream(pares)
        .map(par -> par.split("="))
        .filter(kv -> kv.length == 2)
        .allMatch(kv -> perfil.stream()
            .anyMatch(p -> p.getChave().equalsIgnoreCase(kv[0].trim())
                       && p.getValor().equalsIgnoreCase(kv[1].trim())));

    if ("WHITELIST".equalsIgnoreCase(anuncio.getTipoPolitica())) {
        return corresponde;  // só recebe se corresponde
    } else if ("BLACKLIST".equalsIgnoreCase(anuncio.getTipoPolitica())) {
        return !corresponde; // recebe se NÃO corresponde
    }

    return true;
}

private AnuncioResponse toResponse(Anuncio a) {
    return AnuncioResponse.builder()
            .id(a.getIdAnuncio())
            .titulo(a.getTitulo())
            .conteudo(a.getConteudo())
            .categoria(a.getCategoria())
            .estado(a.getEstado())
            .dataPublicacao(a.getDataPublicacao())
            .visivelDe(a.getVisivelDe())
            .visivelAte(a.getVisivelAte())
            .tipoPolitica(a.getTipoPolitica())
            .politicaFiltro(a.getPoliticaFiltro())
            .idLocal(a.getLocal() != null ? a.getLocal().getIdLocal().toString() : null)
            .nomeLocal(a.getLocal() != null ? a.getLocal().getNome() : null)
            .autorEmail(a.getAutor() != null ? a.getAutor().getEmail() : null)
            .nomeAutor(a.getAutor() != null ? a.getAutor().getNome() : null)
            .build();
}
}