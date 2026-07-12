package com.uan.anunciosloc.infrastructura_server.service;

import com.uan.anunciosloc.infrastructura_server.model.SaldoUtilizador;
import com.uan.anunciosloc.infrastructura_server.repository.AnuncioRepository;
import com.uan.anunciosloc.infrastructura_server.repository.SaldoUtilizadorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InatividadeService {

    private final AnuncioRepository anuncioRepository;
    private final SaldoUtilizadorRepository saldoRepository;
    private final InfraEstadoService infraEstadoService;

    private static final int DIAS_CARENCIA = 7;
    private static final int DESCONTO_POR_DIA = 1;
    private static final int SALDO_MINIMO = 0;

    /**
     * Verifica e aplica descontos para utilizadores inativos.
     * Executa automaticamente todo dia às 00:00.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void aplicarDescontosInatividade() {
        log.info("═══════════════════════════════════════════════════════════════");
        log.info("  INICIANDO VERIFICAÇÃO DE INATIVIDADE");
        log.info("═══════════════════════════════════════════════════════════════");

        LocalDateTime hoje = LocalDateTime.now();
        LocalDateTime dataLimite = hoje.minusDays(DIAS_CARENCIA);

        log.info(" Data limite: {}", dataLimite);
        log.info(" Dias de carência: {}", DIAS_CARENCIA);

        UUID infraId = infraEstadoService.getInfraId();
        log.info(" Infraestrutura ID: {}", infraId);

        List<String> emailsInativos = anuncioRepository.findUsuariosInativosComSaldo(dataLimite);

        if (emailsInativos.isEmpty()) {
            log.info(" Nenhum utilizador inativo com saldo encontrado.");
            return;
        }

        log.info(" Encontrados {} utilizadores inativos com saldo.", emailsInativos.size());

        int totalDescontos = 0;
        int totalSaldoRemovido = 0;

        for (String email : emailsInativos) {
            try {

                SaldoUtilizador saldo = saldoRepository
                        .findByEmailUtilizadorAndIdInfraestrutura(email, infraId)
                        .orElse(null);

                if (saldo == null || saldo.getSaldoParcial() <= 0) {
                    continue;
                }

                LocalDateTime ultimoPost = anuncioRepository.findUltimoPostByEmail(email);
                long diasInativo = calcularDiasInativo(ultimoPost, hoje);

                if (diasInativo <= DIAS_CARENCIA) {
                    log.debug("   '{}' está inativo há {} dias (abaixo da carência)",
                            email, diasInativo);
                    continue;
                }

                int desconto = DESCONTO_POR_DIA;
                int novoSaldo = saldo.getSaldoParcial() - desconto;

                if (novoSaldo < SALDO_MINIMO) {
                    desconto = saldo.getSaldoParcial();
                    novoSaldo = SALDO_MINIMO;
                }

                saldo.setSaldoParcial(novoSaldo);
                saldo.setUltimaAtualizacao(hoje);
                saldo.setVersao((saldo.getVersao() != null ? saldo.getVersao() : 0) + 1);

                saldoRepository.save(saldo);

                totalDescontos++;
                totalSaldoRemovido += desconto;

                log.info("    Desconto aplicado para '{}': -{} ponto(s) (saldo: {}, inativo há {} dias)",
                        email, desconto, novoSaldo, diasInativo);

            } catch (Exception e) {
                log.error("    Erro ao aplicar desconto para '{}': {}",
                        email, e.getMessage());
            }
        }

        log.info("═══════════════════════════════════════════════════════════════");
        log.info("  RESUMO DA VERIFICAÇÃO:");
        log.info("    Utilizadores inativos com saldo: {}", emailsInativos.size());
        log.info("    Descontos aplicados: {}", totalDescontos);
        log.info("    Total de pontos removidos: {}", totalSaldoRemovido);
        log.info("═══════════════════════════════════════════════════════════════");
    }

    private long calcularDiasInativo(LocalDateTime ultimoPost, LocalDateTime dataAtual) {
        if (ultimoPost == null) {

            return 999L;
        }
        return dataAtual.toLocalDate().toEpochDay()
                - ultimoPost.toLocalDate().toEpochDay();
    }

    public boolean isInativo(String email) {
        LocalDateTime ultimoPost = anuncioRepository.findUltimoPostByEmail(email);
        if (ultimoPost == null)
            return true;

        LocalDateTime dataLimite = LocalDateTime.now().minusDays(DIAS_CARENCIA);
        return ultimoPost.isBefore(dataLimite);
    }

    public long getDiasInativo(String email) {
        LocalDateTime ultimoPost = anuncioRepository.findUltimoPostByEmail(email);
        if (ultimoPost == null)
            return 999L;

        return LocalDateTime.now().toLocalDate().toEpochDay()
                - ultimoPost.toLocalDate().toEpochDay();
    }

    @Transactional
    public String executarVerificacaoManual() {
        log.info("  EXECUTANDO VERIFICAÇÃO MANUAL DE INATIVIDADE");
        aplicarDescontosInatividade();
        return "Verificação manual concluída!";
    }

    public LocalDateTime getUltimoPost(String email) {
        return anuncioRepository.findUltimoPostByEmail(email);
    }
}