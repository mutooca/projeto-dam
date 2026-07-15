package com.uan.anunciosloc.infrastructura_server.soap;

import com.uan.anunciosloc.infrastructura_server.model.*;
import com.uan.anunciosloc.infrastructura_server.repository.*;
import com.uan.anunciosloc.infrastructura_server.service.InatividadeService;
import com.uan.anunciosloc.infrastructura_server.service.InfraEstadoService;
import com.uan.anunciosloc.infrastructura_server.soap.dto.*;
import com.uan.anunciosloc.infrastructura_server.soap.dto.LocalInfo;
import com.uan.anunciosloc.infrastructura_server.util.HaversineUtil;
import jakarta.jws.WebService;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@WebService(serviceName = "InfrastructureService", portName = "InfrastructurePort", targetNamespace = "http://infrastructura.anunciosloc.uan.com", endpointInterface = "com.uan.anunciosloc.infrastructura_server.soap.InfrastructureServiceSEI")
public class InfraestruturaServiceImpl implements InfrastructureServiceSEI {

        // rivate final InfraEstadoService estadoService;
        private final InfraEstadoService infraEstadoService;
        private final InatividadeService inatividadeService;
        private final InfraestruturaRepository infraRepository;
        private final LocalRepository localRepository;
        private final AnuncioRepository anuncioRepository;
        private final EntregaAnuncioRepository entregaRepository;
        private final SaldoUtilizadorRepository saldoRepository;
        private final RestricaoRepository restricaoRepository;
        private final PerfilUtilizadorRepository perfilRepository;

        private final ConexaoRepository conexaoRepository;

        public InfraestruturaServiceImpl(
                        InfraEstadoService infraEstadoService,
                        LocalRepository localRepository,
                        AnuncioRepository anuncioRepository,
                        SaldoUtilizadorRepository saldoRepository,
                        PerfilUtilizadorRepository perfilRepository,
                        EntregaAnuncioRepository entregaRepository,
                        CoordenadaGpsRepository gpsRepository,
                        CoordenadaWifiRepository wifiRepository,
                        InatividadeService inatividadeService) {
                this.infraEstadoService = infraEstadoService;
                this.inatividadeService = inatividadeService;
                this.infraRepository = null;
                this.localRepository = localRepository;
                this.anuncioRepository = anuncioRepository;
                this.saldoRepository = saldoRepository;
                this.restricaoRepository = null;
                this.perfilRepository = perfilRepository;
                this.entregaRepository = entregaRepository;
                this.conexaoRepository = null;
        }

        @Value("${infra.nome:D01_Infrastructure1}")
        private String infraNome;

        @Value("${infra.public-url:http://localhost:8091}")
        private String publicUrl;

        @Value("${infra.capacidade:100}")
        private int capacidade;

        @Value("${infra.bonus-entrega:2}")
        private int bonusEntrega;

        @Value("${infra.custo-post:1}")
        private int custoPost;

        @Override
        public ObterInfraResponse obterInfoInfraestrutura() {
                log.info(" [INFRA] Obtendo informações da infraestrutura");

                try {

                        Infraestrutura infra = infraEstadoService.getInfra();

                        if (infra == null) {
                                return ObterInfraResponse.builder()
                                                .sucesso(false)
                                                .mensagem("Infraestrutura não encontrada")
                                                .build();
                        }

                        log.info("   ID: {}", infra.getIdInfraestrutura());
                        log.info("   Nome: {}", infra.getNome());
                        log.info("   Ativa: {}", infra.isAtiva());
                        log.info("   Locais: {}", infra.getTotalLocais());
                        log.info("   Anúncios: {}", infra.getTotalAnuncios());

                        return ObterInfraResponse.builder()
                                        .sucesso(true)
                                        .id(infra.getIdInfraestrutura().toString())
                                        .nome(infra.getNome())
                                        .url(infra.getUrlEndpoint())
                                        .capacidade(infra.getCapacidade())
                                        .bonusEntrega(infra.getBonusEntrega() != null ? infra.getBonusEntrega() : 0)
                                        .custoPost(infra.getCustoPost() != null ? infra.getCustoPost() : 1)
                                        .ativa(infra.isAtiva())
                                        .totalLocais(infra.getTotalLocais() != null ? infra.getTotalLocais() : 0)
                                        .totalAnuncios(infra.getTotalAnuncios() != null ? infra.getTotalAnuncios() : 0)
                                        .totalEntregas(infra.getTotalEntregas() != null ? infra.getTotalEntregas() : 0)
                                        .totalConexoes(infra.getTotalConexoes() != null ? infra.getTotalConexoes() : 0)
                                        .mensagem("Informações da infraestrutura obtidas com sucesso")
                                        .build();

                } catch (Exception e) {
                        log.error("Erro ao obter informações da infraestrutura: {}", e.getMessage(), e);
                        return ObterInfraResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro ao obter informações: " + e.getMessage())
                                        .build();
                }
        }

        @Override
        @Transactional
        public MensagemResponse criarInfraestrutura(int capacidade,
                        int bonusEntrega,
                        int custoPost,
                        double latitude,
                        double longitude,
                        double raio) {

                return MensagemResponse.builder()
                                .sucesso(true)
                                .mensagem("Infraestrutura '" + infraNome + "' criada com sucesso")
                                .build();
        }

        // criarLocal
        @Override
        @Transactional
        public CriarLocalResponse criarLocal(CriarLocalRequest request) {
                log.info("═══════════════════════════════════════════════════════════════");
                log.info(" [INFRA] Criando local: {}", request.getNome());
                log.info("   Utilizador: {}", request.getEmailUtilizador());
                log.info("   Coordenadas: lat={}, lon={}", request.getLatitude(), request.getLongitude());
                log.info("   Localização do utilizador: lat={}, lon={}",
                                request.getLatUtilizador(), request.getLonUtilizador());
                log.info("   WiFi: {}", request.getSsidWifi());
                log.info("═══════════════════════════════════════════════════════════════");

                try {

                        boolean nomeExiste = localRepository.existsByNomeIgnoreCase(request.getNome());
                        if (nomeExiste) {
                                return CriarLocalResponse.builder()
                                                .sucesso(false)
                                                .mensagem("Já existe um local com o nome '" + request.getNome() + "'")
                                                .build();
                        }

                        CoordenadaGps gps = null;
                        if (request.getLatitude() != null && request.getLongitude() != null) {
                                gps = new CoordenadaGps();
                                gps.setLatitude(request.getLatitude());
                                gps.setLongitude(request.getLongitude());
                                gps.setRaio(request.getRaio() != null ? request.getRaio() : 50.0);
                                log.info("   GPS preparado: lat={}, lon={}, raio={}",
                                                gps.getLatitude(), gps.getLongitude(), gps.getRaio());
                        } else {
                                log.info("   GPS não fornecido");
                        }

                        CoordenadaWifi wifi = null;
                        if (request.getSsidWifi() != null && !request.getSsidWifi().isEmpty()) {
                                wifi = new CoordenadaWifi();
                                wifi.setSsid(request.getSsidWifi());
                                log.info("   WiFi preparado: {}", wifi.getSsid());
                        } else {
                                log.info("    WiFi não fornecido");
                        }

                        if (gps == null && wifi == null) {
                                return CriarLocalResponse.builder()
                                                .sucesso(false)
                                                .mensagem("É necessário fornecer pelo menos GPS ou WiFi para criar um local")
                                                .build();
                        }

                        UUID infraId = infraEstadoService.getInfraId();
                        log.info("   Infra ID: {}", infraId);

                        Local local = new Local();
                        local.setNome(request.getNome());
                        local.setCoordenadaGps(gps);
                        local.setCriadoPor(request.getEmailUtilizador());
                        local.setCoordenadaWifi(wifi);
                        local.setIdInfraestrutura(infraId);
                        local.setDataCriacao(LocalDateTime.now());

                        log.info("  Local: nome={}, infraId={}, temGPS={}, temWiFi={}",
                                        local.getNome(),
                                        local.getIdInfraestrutura(),
                                        local.getCoordenadaGps() != null,
                                        local.getCoordenadaWifi() != null);

                        Local savedLocal = localRepository.save(local);
                        log.info(" Local salvo com ID: {}", savedLocal.getIdLocal());

                        infraEstadoService.incrementarTotalLocais();
                        log.info("   Total locais: {}", infraEstadoService.getTotalLocais());
                        log.info(" [INFRA] Resposta criarLocal: sucesso=true, idLocal={}, mensagem=Local criado com sucesso",
                                        savedLocal.getIdLocal());

                        return CriarLocalResponse.builder()
                                        .sucesso(true)
                                        .idLocal(savedLocal.getIdLocal().toString())
                                        .mensagem("Local criado com sucesso")
                                        .build();

                } catch (Exception e) {
                        log.error(" [INFRA] Erro ao criar local: {}", e.getMessage(), e);
                        return CriarLocalResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro ao criar local: " + e.getMessage())
                                        .build();
                }
        }

        // listarLocais
        @Override
        public ListarLocaisResponse listarLocais(Double latUtilizador, Double lonUtilizador) {
                log.info(" [INFRA-SERVER] Listando locais para lat={}, lon={}",
                                latUtilizador, lonUtilizador);

                try {
                        List<Local> todosLocais = localRepository.findAll();
                        log.info("   Total de locais na BD: {}", todosLocais.size());

                        if (todosLocais.isEmpty()) {
                                log.info("   Nenhum local encontrado na BD, retornando resposta vazia");
                                return ListarLocaisResponse.builder()
                                                .sucesso(true)
                                                .locais(List.of())
                                                .mensagem("Nenhum local cadastrado nesta infraestrutura")
                                                .build();
                        }

                        List<LocalInfo> locaisComDistancia = new ArrayList<>();

                        for (Local local : todosLocais) {
                                log.info("   Processando local: ID={}, Nome={}",
                                                local.getIdLocal(), local.getNome());

                                if (local.getCoordenadaGps() == null) {
                                        log.info("   Local '{}' não tem coordenadas GPS, adicionando sem distância",
                                                        local.getNome());
                                        locaisComDistancia.add(toLocalInfo(local, null));
                                        continue;
                                }

                                double latLocal = local.getCoordenadaGps().getLatitude();
                                double lonLocal = local.getCoordenadaGps().getLongitude();
                                double raioLocal = local.getCoordenadaGps().getRaio();

                                log.info("   Local '{}' - Coordenadas: lat={}, lon={}, raio={}m",
                                                local.getNome(), latLocal, lonLocal, raioLocal);

                                double distancia = HaversineUtil.calcularDistancia(
                                                latUtilizador, lonUtilizador,
                                                latLocal, lonLocal);

                                log.info("   Distância do utilizador até '{}': {}m",
                                                local.getNome(), Math.round(distancia));

                                if (distancia <= raioLocal) {
                                        LocalInfo localInfo = toLocalInfo(local, distancia);
                                        locaisComDistancia.add(localInfo);
                                        log.info("    Local '{}' está DENTRO do raio ({}m <= {}m)",
                                                        local.getNome(), Math.round(distancia), raioLocal);
                                } else {
                                        log.info("    Local '{}' está FORA do raio ({}m > {}m)",
                                                        local.getNome(), Math.round(distancia), raioLocal);
                                }
                        }

                        // Ordenar por distância
                        locaisComDistancia.sort(Comparator.comparingDouble(
                                        l -> l.getDistancia() != null ? l.getDistancia() : Double.MAX_VALUE));

                        log.info(" [INFRA-SERVER] Encontrados {} locais próximos", locaisComDistancia.size());

                        ListarLocaisResponse response = ListarLocaisResponse.builder()
                                        .sucesso(true)
                                        .locais(locaisComDistancia)
                                        .mensagem(locaisComDistancia.size() + " local(is) encontrado(s)")
                                        .build();

                        log.info(" [INFRA-SERVER] Resposta criada: sucesso={}, mensagem={}, locais.size={}",
                                        response.isSucesso(),
                                        response.getMensagem(),
                                        response.getLocais() != null ? response.getLocais().size() : 0);

                        if (response.getLocais() != null) {
                                for (LocalInfo info : response.getLocais()) {
                                        log.info("   Local na resposta: id={}, nome={}, distância={}m",
                                                        info.getIdLocal(),
                                                        info.getNome(),
                                                        info.getDistancia() != null ? Math.round(info.getDistancia())
                                                                        : "?");
                                }
                        }

                        return response;

                } catch (Exception e) {
                        log.error("Erro ao listar locais: {}", e.getMessage(), e);
                        return ListarLocaisResponse.builder()
                                        .sucesso(false)
                                        .locais(List.of())
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public MensagemResponse eliminarLocal(String idLocal, String emailUtilizador) {
                log.info(" [INFRA] Eliminando local: {} por {}", idLocal, emailUtilizador);

                try {
                        UUID localUUID = UUID.fromString(idLocal);

                        Local local = localRepository.findById(localUUID)
                                        .orElseThrow(() -> new RuntimeException("Local não encontrado"));

                        log.info("   Local encontrado: {}", local.getNome());

                        List<Anuncio> anuncios = anuncioRepository.findByIdLocal(localUUID);

                        int totalAnunciosEliminados = 0;
                        int totalEntregasEliminadas = 0;

                        if (!anuncios.isEmpty()) {
                                log.info("   Eliminando {} anúncios associados", anuncios.size());

                                for (Anuncio anuncio : anuncios) {

                                        List<EntregaAnuncio> entregas = entregaRepository
                                                        .findByIdAnuncio(anuncio.getIdAnuncio());
                                        if (!entregas.isEmpty()) {
                                                totalEntregasEliminadas += entregas.size();
                                                entregaRepository.deleteAll(entregas);
                                                log.info("     Entregas do anúncio {} eliminadas: {}",
                                                                anuncio.getIdAnuncio(), entregas.size());
                                        }

                                        totalAnunciosEliminados++;
                                }

                                anuncioRepository.deleteAll(anuncios);

                                infraEstadoService.decrementarTotalAnuncios(totalAnunciosEliminados);
                        }

                        localRepository.delete(local);
                        infraEstadoService.decrementarTotalLocais();

                        log.info("  Local eliminado com sucesso");
                        log.info("   Anúncios eliminados: {}", totalAnunciosEliminados);
                        log.info("   Entregas eliminadas: {}", totalEntregasEliminadas);
                        log.info("   Saldos dos utilizadores NÃO foram alterados");

                        return MensagemResponse.builder()
                                        .sucesso(true)
                                        .mensagem(String.format(
                                                        "Local eliminado com sucesso! %d anúncios e %d entregas removidos.",
                                                        totalAnunciosEliminados, totalEntregasEliminadas))
                                        .build();

                } catch (Exception e) {
                        log.error("  Erro ao eliminar local: {}", e.getMessage(), e);
                        return MensagemResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro ao eliminar local: " + e.getMessage())
                                        .build();
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public PostarAnuncioResponse postarAnuncio(PostarAnuncioRequest request) {
                log.info("[INFRA] Postando anúncio");
                log.info("   Autor: {}", request.getEmailAutor());
                log.info("   Título: {}", request.getTitulo());
                log.info("   ID Local: {}", request.getIdLocal());

                try {

                        if (request.getIdLocal() == null || request.getIdLocal().isEmpty()) {
                                return PostarAnuncioResponse.builder()
                                                .sucesso(false)
                                                .mensagem("ID do local é obrigatório")
                                                .build();
                        }

                        UUID localId = UUID.fromString(request.getIdLocal());

                        Local local = localRepository.findById(localId)
                                        .orElseThrow(() -> new RuntimeException("Local não encontrado"));

                        log.info("   Local: {}", local.getNome());

                        UUID infraId = infraEstadoService.getInfraId();
                        log.info("   Infra ID: {}", infraId);

                        SaldoUtilizador saldo = saldoRepository
                                        .findByEmailUtilizadorAndIdInfraestrutura(request.getEmailAutor(), infraId)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Utilizador não tem saldo nesta infraestrutura"));

                        int custoPost = infraEstadoService.getCustoPost();
                        if (saldo.getSaldoParcial() < custoPost) {
                                return PostarAnuncioResponse.builder()
                                                .sucesso(false)
                                                .mensagem(String.format(
                                                                "Saldo insuficiente. Disponível: %d, Necessário: %d",
                                                                saldo.getSaldoParcial(), custoPost))
                                                .build();
                        }

                        log.info("   Saldo antes: {}", saldo.getSaldoParcial());

                        saldo.setSaldoParcial(saldo.getSaldoParcial() - custoPost);
                        saldo.setPontosGastos(saldo.getPontosGastos() + custoPost);
                        saldo.setUltimaAtualizacao(LocalDateTime.now());
                        saldoRepository.save(saldo);
                        log.info("   Saldo depois: {}", saldo.getSaldoParcial());
                        log.info("   Pontos gastos: {}", saldo.getPontosGastos());

                        Anuncio anuncio = new Anuncio();
                        anuncio.setTitulo(request.getTitulo());
                        anuncio.setConteudo(request.getConteudo());
                        anuncio.setEstado("ATIVO");
                        anuncio.setDataPublicacao(LocalDateTime.now());
                        anuncio.setIdLocal(localId);
                        anuncio.setIdInfraestrutura(infraId);
                        anuncio.setAutorEmail(request.getEmailAutor());
                        anuncio.setTipoPolitica(request.getTipoPolitica());
                        anuncio.setPoliticaFiltro(request.getPoliticaFiltro());

                        if (request.getVisivelDe() != null && !request.getVisivelDe().isEmpty()) {
                                anuncio.setVisivelDe(LocalDateTime.parse(request.getVisivelDe()));
                        } else {
                                anuncio.setVisivelDe(LocalDateTime.now());
                        }

                        if (request.getVisivelAte() != null && !request.getVisivelAte().isEmpty()) {
                                anuncio.setVisivelAte(LocalDateTime.parse(request.getVisivelAte()));
                        }

                        Anuncio saved = anuncioRepository.save(anuncio);
                        log.info("   Anúncio criado com ID: {}", saved.getIdAnuncio());

                        infraEstadoService.incrementarTotalAnuncios();

                        return PostarAnuncioResponse.builder()
                                        .sucesso(true)
                                        .idAnuncio(saved.getIdAnuncio().toString())
                                        .mensagem("Anúncio publicado com sucesso")
                                        .build();

                } catch (Exception e) {
                        log.error(" Erro ao postar anúncio: {}", e.getMessage(), e);
                        return PostarAnuncioResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro ao postar anúncio: " + e.getMessage())
                                        .build();
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public ReceberAnunciosResponse receberAnuncios(ReceberAnunciosRequest request) {
                log.info(" [INFRA] Recebendo anúncios para: {}", request.getEmail());
                log.info("   Local ID: {}", request.getIdLocal());

                try {
                        String email = request.getEmail();
                        UUID localUUID = UUID.fromString(request.getIdLocal());

                        Local local = localRepository.findById(localUUID)
                                        .orElseThrow(() -> new RuntimeException("Local não encontrado"));

                        log.info("   Local: {}", local.getNome());

                        List<Anuncio> anuncios = anuncioRepository.findAtivosByLocal(localUUID);

                        if (anuncios.isEmpty()) {
                                log.info("   Nenhum anúncio ativo encontrado");
                                return ReceberAnunciosResponse.builder()
                                                .sucesso(true)
                                                .anuncios(List.of())
                                                .mensagem("Nenhum anúncio disponível")
                                                .build();
                        }

                        log.info("   Anúncios encontrados: {}", anuncios.size());

                        List<PerfilUtilizador> perfil = perfilRepository.findByEmail(email);
                        log.info("   Perfil: {} atributos", perfil.size());

                        List<AnuncioInfo> anunciosFiltrados = anuncios.stream()
                                        .filter(a -> passaNaPolitica(a, perfil))
                                        .map(a -> toAnuncioInfo(a, email))
                                        .collect(Collectors.toList());

                        log.info("   Anúncios após filtros: {}", anunciosFiltrados.size());

                        UUID infraId = infraEstadoService.getInfraId();
                        int entregasRegistadas = 0;

                        for (Anuncio anuncio : anuncios) {

                                boolean jaEntregue = entregaRepository
                                                .existsByIdAnuncioAndEmailUtilizador(anuncio.getIdAnuncio(), email);

                                if (!jaEntregue) {
                                        EntregaAnuncio entrega = EntregaAnuncio.builder()
                                                        .idAnuncio(anuncio.getIdAnuncio())
                                                        .emailUtilizador(email)
                                                        .idInfraestrutura(infraId)
                                                        .dataEntrega(LocalDateTime.now())
                                                        .estadoEntrega("ENTREGUE")
                                                        .build();

                                        entregaRepository.save(entrega);
                                        entregasRegistadas++;

                                        log.info(" Entrega registrada para '{}' - anúncio: {}",
                                                        email, anuncio.getTitulo());
                                } else {
                                        // Verifica se já foi lido
                                        EntregaAnuncio entregaExistente = entregaRepository
                                                        .findByIdAnuncioAndEmailUtilizador(anuncio.getIdAnuncio(),
                                                                        email)
                                                        .orElse(null);

                                        if (entregaExistente != null
                                                        && "LIDO".equals(entregaExistente.getEstadoEntrega())) {
                                                log.info("  Anúncio '{}' já foi lido por '{}'",
                                                                anuncio.getTitulo(), email);
                                        }
                                }
                        }

                        if (entregasRegistadas > 0) {
                                infraEstadoService.incrementarTotalEntregas();
                                log.info("   {} novas entregas registadas", entregasRegistadas);
                        }

                        ReceberAnunciosResponse response = ReceberAnunciosResponse.builder()
                                        .sucesso(true) //
                                        .anuncios(anunciosFiltrados)
                                        .mensagem(anunciosFiltrados.size() + " anúncio(s) encontrado(s)")
                                        .build();

                        log.info("   Resposta criada: sucesso={}, mensagem={}, tamanho={}",
                                        response.isSucesso(),
                                        response.getMensagem(),
                                        response.getAnuncios() != null ? response.getAnuncios().size() : 0);

                        return response;

                } catch (Exception e) {
                        log.error(" Erro ao receber anúncios: {}", e.getMessage(), e);
                        return ReceberAnunciosResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro: " + e.getMessage())
                                        .anuncios(List.of())
                                        .build();
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public MensagemResponse eliminarAnuncio(String idAnuncio, String emailUtilizador, String role) {
                log.info(" [INFRA] Eliminando anúncio: {} por {} (role: {})",
                                idAnuncio, emailUtilizador, role);

                try {
                        UUID anuncioUUID = UUID.fromString(idAnuncio);

                        Anuncio anuncio = anuncioRepository.findById(anuncioUUID)
                                        .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

                        log.info("   Anúncio encontrado: {}", anuncio.getTitulo());
                        log.info("   Autor: {}", anuncio.getAutorEmail());

                        boolean isDono = anuncio.getAutorEmail().equalsIgnoreCase(emailUtilizador);
                        boolean isAdmin = "ADMIN".equalsIgnoreCase(role);

                        if (!isDono && !isAdmin) {
                                log.warn("  Utilizador {} não tem permissão para eliminar", emailUtilizador);
                                return MensagemResponse.builder()
                                                .sucesso(false)
                                                .mensagem("Apenas o dono do anúncio ou administrador podem eliminar")
                                                .build();
                        }

                        log.info("    Permissão concedida: {} {}",
                                        isDono ? "(DONO)" : "",
                                        isAdmin ? "(ADMIN)" : "");

                        List<EntregaAnuncio> entregas = entregaRepository.findByIdAnuncio(anuncioUUID);
                        int totalEntregasEliminadas = 0;

                        if (!entregas.isEmpty()) {
                                totalEntregasEliminadas = entregas.size();
                                entregaRepository.deleteAll(entregas);
                                log.info("   {} entregas eliminadas", totalEntregasEliminadas);
                        }

                        anuncioRepository.delete(anuncio);

                        infraEstadoService.decrementarTotalAnuncios(1);

                        log.info("  Anúncio eliminado com sucesso");
                        log.info("   Entregas eliminadas: {}", totalEntregasEliminadas);
                        log.info("   Saldos dos utilizadores NÃO foram alterados");

                        return MensagemResponse.builder()
                                        .sucesso(true)
                                        .mensagem(String.format(
                                                        "Anúncio eliminado com sucesso! %d entregas removidas.",
                                                        totalEntregasEliminadas))
                                        .build();

                } catch (Exception e) {
                        log.error("  Erro ao eliminar anúncio: {}", e.getMessage(), e);
                        return MensagemResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro ao eliminar anúncio: " + e.getMessage())
                                        .build();
                }
        }

        public List<AnuncioInfo> listarAnunciosPorEmail(String email) {
                log.info(" [INFRA] Listando anúncios do autor: {}", email);

                try {

                        List<Anuncio> anuncios = anuncioRepository.findByAutorEmail(email);

                        if (anuncios.isEmpty()) {
                                log.info(" Nenhum anúncio encontrado para: {}", email);
                                return List.of();
                        }

                        log.info(" Encontrados {} anúncios para: {}", anuncios.size(), email);

                        List<AnuncioInfo> result = anuncios.stream()
                                        .map(a -> toAnuncioInfo(a, email))
                                        .collect(Collectors.toList());

                        for (AnuncioInfo info : result) {
                                log.info("   Anúncio: id={}, título={}, estado={}, local={}",
                                                info.getId(),
                                                info.getTitulo(),
                                                info.getEstado(),
                                                info.getNomeLocal());
                        }

                        return result;

                } catch (Exception e) {
                        log.error(" Erro ao listar anúncios do autor {}: {}", email, e.getMessage(), e);
                        return List.of();
                }
        }

        @Override
        public String obterUltimoPost(String email) {
                log.info(" [INFRA] Buscando último post de: {}", email);
                try {
                        LocalDateTime ultimoPost = anuncioRepository.findUltimoPostByEmail(email);
                        return ultimoPost != null ? ultimoPost.toString() : null;
                } catch (Exception e) {
                        log.error(" Erro: {}", e.getMessage());
                        return null;
                }
        }

        @Override
        public long contarAnunciosPorEmail(String email) {
                log.info(" [INFRA] Contando anúncios de: {}", email);
                try {
                        return anuncioRepository.countAnunciosByAutorEmail(email);
                } catch (Exception e) {
                        log.error(" Erro: {}", e.getMessage());
                        return 0;
                }
        }

        @Override
        public long contarEntregasPorEmail(String email) {
                log.info(" [INFRA] Contando entregas (leituras) dos anúncios de: {}", email);
                try {
                        
                        return entregaRepository.countEntregasDosAnunciosDoAutor(email);
                } catch (Exception e) {
                        log.error(" Erro: {}", e.getMessage());
                        return 0;
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public MensagemResponse marcarComoLido(String idAnuncio, String emailUtilizador) {
                log.info(" [INFRA] Marcando anúncio como lido");
                log.info("   ID Anúncio: {}", idAnuncio);
                log.info("   Utilizador: {}", emailUtilizador);

                try {
                        UUID anuncioUUID = UUID.fromString(idAnuncio);

                        Anuncio anuncio = anuncioRepository.findById(anuncioUUID)
                                        .orElseThrow(() -> new RuntimeException("Anúncio não encontrado"));

                        log.info("   Anúncio: {}", anuncio.getTitulo());
                        log.info("   Autor: {}", anuncio.getAutorEmail());

                        EntregaAnuncio entrega = entregaRepository
                                        .findByIdAnuncioAndEmailUtilizador(anuncioUUID, emailUtilizador)
                                        .orElseThrow(() -> new RuntimeException(
                                                        "Entrega não encontrada para este anúncio e utilizador"));

                        if (entrega.isLido()) {
                                log.warn("   Anúncio já foi marcado como lido anteriormente");
                                return MensagemResponse.builder()
                                                .sucesso(true)
                                                .mensagem("Anúncio já foi lido anteriormente")
                                                .estado("LIDO")
                                                .build();
                        }

                        entrega.marcarComoLido();
                        entrega.setLidoEm("APP");

                        log.info("   Leitura registrada em: {}", entrega.getDataLeitura());

                        UUID infraId = infraEstadoService.getInfraId();
                        int bonusEntrega = infraEstadoService.getBonusEntrega();

                        String emailDono = anuncio.getAutorEmail();

                        SaldoUtilizador saldoDono = saldoRepository
                                        .findByEmailUtilizadorAndIdInfraestrutura(emailDono, infraId)
                                        .orElseGet(() -> {

                                                SaldoUtilizador novoSaldo = SaldoUtilizador.builder()
                                                                .emailUtilizador(emailDono)
                                                                .idInfraestrutura(infraId)
                                                                .saldoParcial(0)
                                                                .pontosGanhos(0)
                                                                .pontosGastos(0)
                                                                .versao(0)
                                                                .ultimaAtualizacao(LocalDateTime.now())
                                                                .build();
                                                return saldoRepository.save(novoSaldo);
                                        });

                        int saldoDonoAntigo = saldoDono.getSaldoParcial();
                        saldoDono.setSaldoParcial(saldoDono.getSaldoParcial() + bonusEntrega);
                        saldoDono.setPontosGanhos(saldoDono.getPontosGanhos() + bonusEntrega);
                        saldoDono.setUltimaAtualizacao(LocalDateTime.now());
                        saldoDono.setVersao(saldoDono.getVersao() + 1);
                        saldoRepository.save(saldoDono);

                        entrega.setPontosGanhosDono(bonusEntrega);

                        log.info("  Dono '{}' ganhou {} pontos (saldo: {} → {})",
                                        emailDono,
                                        bonusEntrega,
                                        saldoDonoAntigo,
                                        saldoDono.getSaldoParcial());

                        int bonusLeitor = 1;

                        SaldoUtilizador saldoLeitor = saldoRepository
                                        .findByEmailUtilizadorAndIdInfraestrutura(emailUtilizador, infraId)
                                        .orElseGet(() -> {
                                                SaldoUtilizador novoSaldo = SaldoUtilizador.builder()
                                                                .emailUtilizador(emailUtilizador)
                                                                .idInfraestrutura(infraId)
                                                                .saldoParcial(0)
                                                                .pontosGanhos(0)
                                                                .pontosGastos(0)
                                                                .versao(0)
                                                                .ultimaAtualizacao(LocalDateTime.now())
                                                                .build();
                                                return saldoRepository.save(novoSaldo);
                                        });

                        int saldoLeitorAntigo = saldoLeitor.getSaldoParcial();
                        saldoLeitor.setSaldoParcial(saldoLeitor.getSaldoParcial() + bonusLeitor);
                        saldoLeitor.setPontosGanhos(saldoLeitor.getPontosGanhos() + bonusLeitor);
                        saldoLeitor.setUltimaAtualizacao(LocalDateTime.now());
                        saldoLeitor.setVersao(saldoLeitor.getVersao() + 1);
                        saldoRepository.save(saldoLeitor);

                        entrega.setPontosGanhosLeitor(bonusLeitor);

                        log.info("   Leitor '{}' ganhou {} pontos (saldo: {} → {})",
                                        emailUtilizador,
                                        bonusLeitor,
                                        saldoLeitorAntigo,
                                        saldoLeitor.getSaldoParcial());

                        entregaRepository.save(entrega);

                        log.info(" Anúncio marcado como lido com sucesso!");

                        return MensagemResponse.builder()
                                        .sucesso(true)
                                        .mensagem(String.format(
                                                        " Anúncio lido! Dono ganhou %d pontos, Leitor ganhou %d pontos",
                                                        bonusEntrega, bonusLeitor))
                                        .estado("LIDO")
                                        .idAnuncio(idAnuncio)
                                        .build();

                } catch (Exception e) {
                        log.error(" Erro ao marcar anúncio como lido: {}", e.getMessage(), e);
                        return MensagemResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro ao marcar anúncio como lido: " + e.getMessage())
                                        .build();
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public MensagemResponse adicionarPerfil(String email, List<PerfilItem> perfil) {
                log.info(" [INFRA] Adicionando perfil para: {}", email);
                log.info("   Itens: {}", perfil.size());

                try {

                        UUID idUtilizador = obterIdUtilizador(email);

                        perfilRepository.deleteByEmail(email);

                        for (PerfilItem item : perfil) {
                                PerfilUtilizador perfilEntity = PerfilUtilizador.builder()
                                                .idUtilizador(idUtilizador)
                                                .email(email)
                                                .chave(item.getChave())
                                                .valor(item.getValor())
                                                .build();
                                perfilRepository.save(perfilEntity);
                                log.info("  {} = {}", item.getChave(), item.getValor());
                        }

                        log.info(" Perfil atualizado com sucesso para: {}", email);
                        return MensagemResponse.builder()
                                        .sucesso(true)
                                        .mensagem("Perfil atualizado com sucesso!")
                                        .build();

                } catch (Exception e) {
                        log.error("  Erro ao adicionar perfil: {}", e.getMessage(), e);
                        return MensagemResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        @Override
        public List<PerfilItem> consultarPerfil(String email) {
                log.info(" [INFRA] Consultando perfil para: {}", email);

                try {
                        List<PerfilUtilizador> perfis = perfilRepository.findByEmail(email);

                        List<PerfilItem> result = perfis.stream()
                                        .map(p -> PerfilItem.builder()
                                                        .chave(p.getChave())
                                                        .valor(p.getValor())
                                                        .build())
                                        .collect(Collectors.toList());

                        log.info("   Encontrados {} itens", result.size());
                        return result;

                } catch (Exception e) {
                        log.error("  Erro ao consultar perfil: {}", e.getMessage(), e);
                        return List.of();
                }
        }

        @Override
        @Transactional
        public MensagemResponse removerChavePerfil(String email, String chave) {
                log.info(" [INFRA] Removendo chave '{}' do perfil de: {}", chave, email);

                try {
                        int removidos = perfilRepository.deleteByEmailAndChave(email, chave);

                        if (removidos > 0) {
                                log.info("   Chave '{}' removida com sucesso", chave);
                                return MensagemResponse.builder()
                                                .sucesso(true)
                                                .mensagem("Chave '" + chave + "' removida com sucesso!")
                                                .build();
                        } else {
                                log.warn("   Chave '{}' não encontrada para: {}", chave, email);
                                return MensagemResponse.builder()
                                                .sucesso(false)
                                                .mensagem("Chave '" + chave + "' não encontrada")
                                                .build();
                        }
                } catch (Exception e) {
                        log.error("  Erro ao remover chave: {}", e.getMessage(), e);
                        return MensagemResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        // Helper para obter ID do utilizador
        private UUID obterIdUtilizador(String email) {
                // Buscar na tabela de utilizadores ou criar um mapping
                // Por simplicidade, usar um UUID baseado no email (ou criar um novo)
                return UUID.nameUUIDFromBytes(email.getBytes());
        }

        @Override
        public LerSaldoResponse lerSaldo(String email) {
                log.info("[INFRA] Lendo saldo para: {}", email);

                try {
                        UUID infraId = infraEstadoService.getInfraId();

                        SaldoUtilizador saldo = saldoRepository
                                        .findByEmailUtilizadorAndIdInfraestrutura(email, infraId)
                                        .orElse(null);

                        if (saldo == null) {
                                return LerSaldoResponse.builder()
                                                .sucesso(true)
                                                .email(email)
                                                .saldo(0)
                                                .versao(0)
                                                .mensagem("Saldo inicial (utilizador não tem saldo nesta infra)")
                                                .build();
                        }

                        return LerSaldoResponse.builder()
                                        .sucesso(true)
                                        .email(email)
                                        .saldo(saldo.getSaldoParcial())
                                        .versao(saldo.getVersao())
                                        .mensagem("Saldo obtido com sucesso")
                                        .build();

                } catch (Exception e) {
                        log.error(" Erro ao ler saldo: {}", e.getMessage());
                        return LerSaldoResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        @SuppressWarnings("null")
        @Override
        @Transactional
        public EscreverSaldoResponse escreverSaldo(String email, float novoSaldo, int versao) {
                log.info(" [INFRA] Escrevendo saldo para {}: {} (versão {})", email, novoSaldo, versao);

                try {
                        UUID infraId = infraEstadoService.getInfraId();

                        SaldoUtilizador saldo = saldoRepository
                                        .findByEmailUtilizadorAndIdInfraestrutura(email, infraId)
                                        .orElse(null);

                        if (saldo != null && saldo.getVersao() >= versao) {
                                log.warn(" Versão rejeitada: actual={}, recebida={}", saldo.getVersao(), versao);
                                return EscreverSaldoResponse.builder()
                                                .sucesso(false)
                                                .mensagem("Versão desatualizada. Actual: " + saldo.getVersao())
                                                .versaoActual(saldo.getVersao())
                                                .build();
                        }

                        if (saldo == null) {

                                saldo = SaldoUtilizador.builder()
                                                .emailUtilizador(email)
                                                .idInfraestrutura(infraId)
                                                .saldoParcial((int) novoSaldo)
                                                .versao(versao)
                                                .pontosGanhos(0)
                                                .pontosGastos(0)
                                                .ultimaAtualizacao(LocalDateTime.now())
                                                .build();
                        } else {
                                saldo.setSaldoParcial((int) novoSaldo);
                                saldo.setVersao(versao);
                                saldo.setUltimaAtualizacao(LocalDateTime.now());
                        }

                        saldoRepository.save(saldo);
                        log.info(" Saldo escrito: {} = {} (versão {})", email, novoSaldo, versao);

                        return EscreverSaldoResponse.builder()
                                        .sucesso(true)
                                        .mensagem("Saldo escrito com sucesso")
                                        .versaoActual(versao)
                                        .build();

                } catch (Exception e) {
                        log.error(" Erro ao escrever saldo: {}", e.getMessage(), e);
                        return EscreverSaldoResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro: " + e.getMessage())
                                        .versaoActual(0)
                                        .build();
                }
        }

        @Override
        public ObterSaldoResponse obterSaldo(String email) {
                log.info(" [INFRA] Obtendo saldo para: {}", email);

                try {
                        UUID infraId = infraEstadoService.getInfraId();

                        SaldoUtilizador saldo = saldoRepository
                                        .findByEmailUtilizadorAndIdInfraestrutura(email, infraId)
                                        .orElse(null);

                        if (saldo == null) {
                                return ObterSaldoResponse.builder()
                                                .sucesso(true)
                                                .email(email)
                                                .saldo(0)
                                                .mensagem("Saldo não encontrado (0)")
                                                .build();
                        }

                        return ObterSaldoResponse.builder()
                                        .sucesso(true)
                                        .email(email)
                                        .saldo(saldo.getSaldoParcial())
                                        .mensagem("Saldo obtido com sucesso")
                                        .build();

                } catch (Exception e) {
                        log.error(" Erro ao obter saldo: {}", e.getMessage());
                        return ObterSaldoResponse.builder()
                                        .sucesso(false)
                                        .email(email)
                                        .saldo(0)
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        @Override
        public MensagemResponse verificarInatividade() {
                log.info(" [SOAP] Verificação de inatividade solicitada");
                try {
                        String resultado = inatividadeService.executarVerificacaoManual();
                        return MensagemResponse.builder()
                                        .sucesso(true)
                                        .mensagem(resultado)
                                        .build();
                } catch (Exception e) {
                        log.error(" Erro ao verificar inatividade: {}", e.getMessage());
                        return MensagemResponse.builder()
                                        .sucesso(false)
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        @Override
        public DiasInatividadeResponse obterDiasInatividade(String email) {
                log.info(" [SOAP] Obtendo dias de inatividade para: {}", email);
                try {
                        long dias = inatividadeService.getDiasInativo(email);
                        boolean inativo = inatividadeService.isInativo(email);
                        LocalDateTime ultimoPost = inatividadeService.getUltimoPost(email);

                        return DiasInatividadeResponse.builder()
                                        .sucesso(true)
                                        .email(email)
                                        .diasInativo((int) dias)
                                        .inativo(inativo)
                                        .ultimoPost(ultimoPost != null ? ultimoPost.toString() : null)
                                        .mensagem(String.format(
                                                        "Utilizador '%s' está inativo há %d dias",
                                                        email, dias))
                                        .build();
                } catch (Exception e) {
                        log.error(" Erro ao obter dias de inatividade: {}", e.getMessage());
                        return DiasInatividadeResponse.builder()
                                        .sucesso(false)
                                        .email(email)
                                        .mensagem("Erro: " + e.getMessage())
                                        .build();
                }
        }

        @Override
        public PingResponse ping() {
                log.info(" [INFRA] Ping recebido");

                try {
                        String nome = infraEstadoService.getInfraNome();
                        int totalAnuncios = infraEstadoService.getTotalAnuncios();
                        int totalEntregas = infraEstadoService.getTotalEntregas();

                        String mensagem = String.format(
                                        "PONG | %s | Anúncios: %d | Entregas: %d | OK",
                                        nome, totalAnuncios, totalEntregas);

                        log.info(" [INFRA] Resposta ping: {}", mensagem);

                        return PingResponse.builder()
                                        .mensagem(mensagem)
                                        .build();

                } catch (Exception e) {
                        log.error(" [INFRA] Erro no ping: {}", e.getMessage());
                        return PingResponse.builder()
                                        .mensagem("PONG | ERROR | " + e.getMessage())
                                        .build();
                }
        }

        @Override
        @Transactional
        public void clear() {
                log.warn("clear() chamado em {}", infraNome);
                entregaRepository.deleteAll();
                anuncioRepository.deleteAll();
                perfilRepository.deleteAll();
                saldoRepository.deleteAll();
                conexaoRepository.deleteAll();
                localRepository.deleteAll();
                restricaoRepository.deleteAll();
                infraRepository.deleteAll();

        }

        @SuppressWarnings({ "null" })
        private AnuncioInfo toAnuncioInfo(Anuncio anuncio, String emailUtilizador) {
                String nomeLocal = null;
                if (anuncio.getIdLocal() != null) {
                        Local local = localRepository.findById(anuncio.getIdLocal()).orElse(null);
                        if (local != null) {
                                nomeLocal = local.getNome();
                        }
                }

                String estado = "NAO_ENTREGUE";
                if (emailUtilizador != null && !emailUtilizador.isEmpty()) {
                        Optional<EntregaAnuncio> entrega = entregaRepository
                                        .findByIdAnuncioAndEmailUtilizador(anuncio.getIdAnuncio(), emailUtilizador);
                        if (entrega.isPresent()) {
                                estado = entrega.get().getEstadoEntrega();
                        }
                }

                UUID idAnuncio = anuncio.getIdAnuncio();
                long totalEntregas = entregaRepository.countEntregasByAnuncio(idAnuncio);
                long totalLeituras = entregaRepository.countLeiturasByAnuncio(idAnuncio);

                return AnuncioInfo.builder()
                                .id(anuncio.getIdAnuncio().toString())
                                .titulo(anuncio.getTitulo())
                                .conteudo(anuncio.getConteudo())
                                .categoria(anuncio.getCategoria())
                                .autorEmail(anuncio.getAutorEmail())
                                .dataPublicacao(anuncio.getDataPublicacao() != null
                                                ? anuncio.getDataPublicacao().toString()
                                                : null)
                                .visivelDe(anuncio.getVisivelDe() != null
                                                ? anuncio.getVisivelDe().toString()
                                                : null)
                                .visivelAte(anuncio.getVisivelAte() != null
                                                ? anuncio.getVisivelAte().toString()
                                                : null)
                                .tipoPolitica(anuncio.getTipoPolitica())
                                .politicaFiltro(anuncio.getPoliticaFiltro())
                                .nomeLocal(nomeLocal)
                                .estado(estado)
                                .totalEntregas((int) totalEntregas)
                                .totalLeituras((int) totalLeituras)
                                .build();
        }

        // Helper para converter Local ---> LocalInfo com distância
        private LocalInfo toLocalInfo(Local local, Double distancia) {
                log.info("   Convertendo Local para LocalInfo: id={}, nome={}, distancia={}",
                                local.getIdLocal(), local.getNome(), distancia != null ? Math.round(distancia) : "?");

                LocalInfo info = LocalInfo.builder()
                                .idLocal(local.getIdLocal() != null ? local.getIdLocal().toString() : null)
                                .nome(local.getNome() != null ? local.getNome() : null)
                                .latitude(local.getCoordenadaGps() != null
                                                ? local.getCoordenadaGps().getLatitude()
                                                : null)
                                .longitude(local.getCoordenadaGps() != null
                                                ? local.getCoordenadaGps().getLongitude()
                                                : null)
                                .raio(local.getCoordenadaGps() != null
                                                ? local.getCoordenadaGps().getRaio()
                                                : null)
                                .ssid(local.getCoordenadaWifi() != null
                                                ? local.getCoordenadaWifi().getSsid()
                                                : null)
                                .distancia(distancia)
                                .build();

                log.info("   LocalInfo criado: id={}, nome={}", info.getIdLocal(), info.getNome());
                return info;
        }

        // Helper politica whitelist/blacklist
        @SuppressWarnings("null")
        private boolean passaNaPolitica(Anuncio anuncio, List<PerfilUtilizador> perfil) {
                if (anuncio.getTipoPolitica() == null || anuncio.getTipoPolitica().isBlank()) {
                        return true;
                }
                if (anuncio.getPoliticaFiltro() == null || anuncio.getPoliticaFiltro().isBlank()) {
                        return true;
                }

                var perfilMap = perfil.stream()
                                .collect(Collectors.toMap(
                                                PerfilUtilizador::getChave,
                                                PerfilUtilizador::getValor,
                                                (a, b) -> a));

                String[] pares = anuncio.getPoliticaFiltro().split(",");

                boolean corresponde = java.util.Arrays.stream(pares)
                                .map(par -> par.split("="))
                                .filter(kv -> kv.length == 2)
                                .allMatch(kv -> {
                                        String chave = kv[0].trim();
                                        String valor = kv[1].trim();
                                        String valorPerfil = perfilMap.get(chave);
                                        return valorPerfil != null && valorPerfil.equalsIgnoreCase(valor);
                                });

                if ("WHITELIST".equalsIgnoreCase(anuncio.getTipoPolitica())) {
                        return corresponde;
                } else if ("BLACKLIST".equalsIgnoreCase(anuncio.getTipoPolitica())) {
                        return !corresponde;
                }
                return true;
        }

}
