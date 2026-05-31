package com.anunciosloc.anunciosloc_server.service;

import com.anunciosloc.anunciosloc_server.dto.PostarAnuncioRequest;
import com.anunciosloc.anunciosloc_server.model.*;
import com.anunciosloc.anunciosloc_server.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final LocalRepository localRepository;
    private final InfraestruturaRepository infraRepository;
    private final SaldoUtilizadorRepository saldoRepository;
    private final EntregaAnuncioRepository entregaRepository;

      List<String> categorias = List.of();
    

    @SuppressWarnings("null")
    @Transactional
    public Anuncio postarAnuncio(PostarAnuncioRequest request) {
        
        Utilizador autor = utilizadorRepository.findByEmail(request.getEmailAutor())
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        
        if (!"USER".equals(autor.getRole())) {
            throw new RuntimeException("Apenas clientes podem publicar anúncios");
        }

       
        UUID localId = request.getLocalId();
        Local local = localRepository.findById(localId)
            .orElseThrow(() -> new RuntimeException("Local não encontrado"));

        
        Infraestrutura infra = local.getInfraestrutura();

      
        if (autor.getSaldo() < infra.getCustoPost()) {
            throw new RuntimeException("Saldo global insuficiente para publicar anúncio");
        }

       
        SaldoUtilizador saldoInfra = saldoRepository.findByUtilizadorAndInfraestrutura(autor, infra)
            .orElseGet(() -> {
                SaldoUtilizador novo = new SaldoUtilizador();
                novo.setUtilizador(autor);
                novo.setInfraestrutura(infra);
                novo.setSaldoParcial(0);
                novo.setPontosGanhos(0);
                novo.setPontosGastos(0);
                return novo;
            });

        if (saldoInfra.getSaldoParcial() < infra.getCustoPost()) {
            throw new RuntimeException("Saldo insuficiente nesta infraestrutura");
        }

        
        autor.setSaldo(autor.getSaldo() - infra.getCustoPost());

       
        saldoInfra.setSaldoParcial(saldoInfra.getSaldoParcial() - infra.getCustoPost());
        saldoInfra.setPontosGastos(saldoInfra.getPontosGastos() + infra.getCustoPost());
        saldoInfra.setUltimaAtualizacao(LocalDateTime.now());

       
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

       
        utilizadorRepository.save(autor);
        saldoRepository.save(saldoInfra);
        infraRepository.save(infra);

        return anuncioRepository.save(anuncio);
    }

    
    @Transactional
    public List<Anuncio> receberAnuncios(String email, @NonNull UUID infraId) {
       
        Utilizador user = utilizadorRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Utilizador não encontrado"));

        
        Infraestrutura infra = infraRepository.findById(infraId)
            .orElseThrow(() -> new RuntimeException("Infraestrutura não encontrada"));

       
      
        if (user.getPreferenciaAnuncio() != null && !user.getPreferenciaAnuncio().isEmpty()) {
            categorias = List.of(user.getPreferenciaAnuncio().split(","));
        }

       
        List<Anuncio> anuncios = anuncioRepository.findByInfraestruturaAndEstado(infra, "ATIVO");

        if (!categorias.isEmpty()) {
            anuncios = anuncios.stream()
                .filter(a -> categorias.contains(a.getCategoria()))
                .collect(java.util.stream.Collectors.toList());
        }

        
        int contador = 0;
        for (Anuncio anuncio : anuncios) {
            
            boolean jaEntregue = entregaRepository.existsByAnuncioAndUtilizador(anuncio, user);
            if (jaEntregue) {
                continue;
            }

            
            EntregaAnuncio entrega = new EntregaAnuncio();
            entrega.setAnuncio(anuncio);
            entrega.setInfraestrutura(infra);
            entrega.setUtilizador(user);
            entrega.setNumEntrega(++contador);
            entrega.setDataEntrega(LocalDateTime.now());
            entrega.setEstadoEntrega("ENTREGUE");
            entregaRepository.save(entrega);

            
            user.setSaldo(user.getSaldo() + infra.getBonusEntrega());

            // Adiciona bónus ao saldo específico da infra
            SaldoUtilizador saldoInfra = saldoRepository.findByUtilizadorAndInfraestrutura(user, infra)
                .orElseGet(() -> {
                    SaldoUtilizador novo = new SaldoUtilizador();
                    novo.setUtilizador(user);
                    novo.setInfraestrutura(infra);
                    novo.setSaldoParcial(0);
                    novo.setPontosGanhos(0);
                    novo.setPontosGastos(0);
                    return novo;
                });

            saldoInfra.setSaldoParcial(saldoInfra.getSaldoParcial() + infra.getBonusEntrega());
            saldoInfra.setPontosGanhos(saldoInfra.getPontosGanhos() + infra.getBonusEntrega());
            saldoInfra.setUltimaAtualizacao(LocalDateTime.now());

            saldoRepository.save(saldoInfra);
        }

        
        infra.setTotalEntregas(infra.getTotalEntregas() + contador);

        utilizadorRepository.save(user);
        infraRepository.save(infra);

        return anuncios;
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

   
    @Transactional
    public void removerAnuncio(@NonNull UUID anuncioId, String emailGestor) {
        Utilizador gestor = utilizadorRepository.findByEmail(emailGestor)
            .orElseThrow(() -> new RuntimeException("Gestor não encontrado"));

        if (!"ADMIN".equals(gestor.getRole())) {
            throw new RuntimeException("Apenas gestores podem remover anúncios");
        }

        anuncioRepository.deleteById(anuncioId);
    }
}