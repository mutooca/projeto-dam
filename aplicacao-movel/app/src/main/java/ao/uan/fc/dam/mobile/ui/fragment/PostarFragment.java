package ao.uan.fc.dam.mobile.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.contentProvider.LocalizacaoProvider;
import ao.uan.fc.dam.mobile.data.entity.Anuncio;
import ao.uan.fc.dam.mobile.data.entity.Historico;
import ao.uan.fc.dam.mobile.data.enums.EstadoAnuncio;
import ao.uan.fc.dam.mobile.data.enums.ModoEntrega;
import ao.uan.fc.dam.mobile.data.enums.Visibilidade;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.data.repository.HistoricoRepository;
import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.network.MessagePublisher;
import ao.uan.fc.dam.mobile.network.UdpServidor;
import ao.uan.fc.dam.mobile.network.WifiDirectManager;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class PostarFragment extends Fragment {

    private static final int REQUEST_P2P_PERMISSOES = 200;

    private Spinner destinoAnuncio;
    private EditText titulo;
    private EditText conteudo;
    private EditText dataInicio;
    private EditText dataFim;
    private RadioGroup visibilidade;
    private EditText restricoes;
    private RadioGroup modoEntrega;

    private UdpServidor udpServidor;
    private WifiDirectManager wifiDirectManager;
    private MessagePublisher messagePublisher;

    private LocalRepository localRepository;
    private AnuncioRepository anuncioRepository;
    private HistoricoRepository historicoRepository;
    private SessionManager sessionManager;
    private LocalizacaoProvider localizacaoProvider;

    private final List<LocalCompleto> locais = new ArrayList<>();

    public PostarFragment() {
        super(R.layout.fragment_postar);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        destinoAnuncio = view.findViewById(R.id.spinnerLocais);
        titulo = view.findViewById(R.id.inputTitulo);
        conteudo = view.findViewById(R.id.inputMensagem);
        dataInicio = view.findViewById(R.id.inputDataInicio);
        dataFim = view.findViewById(R.id.inputDataFim);
        visibilidade = view.findViewById(R.id.radioGroupPolitica);
        restricoes = view.findViewById(R.id.inputRestricoes);
        modoEntrega = view.findViewById(R.id.radioGroupEntrega);
        Button btnPublicar = view.findViewById(R.id.btnPublicar);
        Button btnTestarP2p = view.findViewById(R.id.btnTestarP2p);

        localRepository = new LocalRepository(requireContext());
        anuncioRepository = new AnuncioRepository(requireContext());
        historicoRepository = new HistoricoRepository(requireContext());
        sessionManager = new SessionManager(requireContext());
        localizacaoProvider = new LocalizacaoProvider(requireContext());
        messagePublisher = new MessagePublisher(requireContext());

        carregarLocais();

        btnPublicar.setOnClickListener(v -> publicar());

        pedirPermissoesP2p();
        inicializarP2p();

        btnTestarP2p.setOnClickListener(v -> {
            wifiDirectManager.registar();
            wifiDirectManager.descobrirPeers();
            Toast.makeText(
                    requireContext(),
                    "A procurar dispositivos...",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    private void pedirPermissoesP2p() {
        List<String> pendentes = new ArrayList<>();

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            pendentes.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.NEARBY_WIFI_DEVICES
        ) != PackageManager.PERMISSION_GRANTED) {
            pendentes.add(Manifest.permission.NEARBY_WIFI_DEVICES);
        }

        if (!pendentes.isEmpty()) {
            requestPermissions(
                    pendentes.toArray(new String[0]),
                    REQUEST_P2P_PERMISSOES
            );
        }
    }

    private void inicializarP2p() {
        wifiDirectManager = new WifiDirectManager(
                requireContext(),
                new WifiDirectManager.WifiDirectListener() {
                    @Override
                    public void onPeersDisponiveis(List<WifiP2pDevice> peers) {
                        Log.d("P2P", "Peers encontrados: " + peers.size());
                        if (!peers.isEmpty()) {
                            wifiDirectManager.conectarComDesempate(peers.get(0));
                        }
                    }

                    @Override
                    public void onLigacaoEstabelecida(WifiP2pInfo info) {
                        Log.d(
                                "P2P",
                                "Grupo formado: " + info.groupOwnerAddress.getHostAddress()
                        );

                        udpServidor = new UdpServidor(requireContext());
                        udpServidor.iniciar();

                        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                            if (!info.isGroupOwner) {
                                messagePublisher.enviarHello(info.groupOwnerAddress);
                                return;
                            }

                            try {
                                messagePublisher.enviarHello(
                                        java.net.InetAddress.getByName("192.168.49.2")
                                );
                            } catch (Exception ignored) {
                            }
                        }, 2000);

                        Toast.makeText(
                                requireContext(),
                                "Ligado ao grupo P2P - Handshake iniciado",
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onWifiDirectIndisponivel() {
                        Toast.makeText(
                                requireContext(),
                                "WiFi Direct indisponível",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }

    private void publicar() {
        if (titulo.getText().toString().trim().isEmpty()) {
            titulo.setError("Informe o título");
            return;
        }

        if (conteudo.getText().toString().trim().isEmpty()) {
            conteudo.setError("Informe o conteúdo");
            return;
        }

        if (locais.isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Nenhum local disponível",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        LocalCompleto localSelecionado = locais.get(destinoAnuncio.getSelectedItemPosition());

        LocalDateTime visivelDe = lerDataOpcional(dataInicio, "data de início");
        if (visivelDe == null && !dataInicio.getText().toString().trim().isEmpty()) {
            return;
        }

        LocalDateTime visivelAte = lerDataOpcional(dataFim, "data de fim");
        if (visivelAte == null && !dataFim.getText().toString().trim().isEmpty()) {
            return;
        }

        if (visivelDe != null
                && visivelAte != null
                && visivelAte.isBefore(visivelDe)) {
            dataFim.setError("A data final deve ser posterior ao início");
            Toast.makeText(
                    requireContext(),
                    "A data final deve ser posterior à data inicial.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        Anuncio anuncio = new Anuncio();
        anuncio.setTitulo(titulo.getText().toString().trim());
        anuncio.setConteudo(conteudo.getText().toString().trim());
        anuncio.setEstado(EstadoAnuncio.ACTIVO);
        anuncio.setDataPublicacao(LocalDateTime.now());
        anuncio.setDataInicio(visivelDe);
        anuncio.setDataFim(visivelAte);
        anuncio.setRestricaoPerfil(restricoes.getText().toString().trim());
        anuncio.setIdUtilizador(sessionManager.getIdUtilizador());

        if (modoEntrega.getCheckedRadioButtonId() == R.id.radioCentralizado) {
            anuncio.setModoEntrega(ModoEntrega.CENTRALIZADO);
        } else {
            anuncio.setModoEntrega(ModoEntrega.DESCENTRALIZADO);
        }

        if (visibilidade.getCheckedRadioButtonId() == R.id.radioWhitelist) {
            anuncio.setVisibilidade(Visibilidade.WHITELIST);
        } else {
            anuncio.setVisibilidade(Visibilidade.BLACKLIST);
        }

        localRepository.garantirLocalPersistido(
                localSelecionado,
                localPersistido -> {
                    anuncio.setIdLocal(localPersistido.getIdLocal());

                    String idLocalServidor = localPersistido.getIdServidor();
                    if ((idLocalServidor == null || idLocalServidor.isBlank())
                            && localSelecionado.getLocal() != null) {
                        idLocalServidor = localSelecionado.getLocal().getIdServidor();
                    }

                    String idLocalServidorFinal = idLocalServidor;
                    anuncioRepository.publicar(
                            anuncio,
                            idLocalServidorFinal,
                            sessionManager.getEmail(),
                            id -> {
                                anuncio.setIdAnuncio(id.intValue());

                                if (anuncio.getModoEntrega() == ModoEntrega.DESCENTRALIZADO) {
                                    messagePublisher.publicar(anuncio);
                                }

                                requireActivity().runOnUiThread(() -> {
                                    Historico historico = new Historico();
                                    historico.setTipo("PUBLICACAO");
                                    historico.setNome(anuncio.getTitulo());
                                    historico.setPontos(3);
                                    historico.setRegisto(LocalDateTime.now());
                                    historico.setIdUtilizador(anuncio.getIdUtilizador());
                                    historicoRepository.inserir(historico, null);

                                    Toast.makeText(
                                            requireContext(),
                                            "Anúncio publicado",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    limparFormulario();
                                });
                            },
                            erro -> requireActivity().runOnUiThread(() ->
                                    Toast.makeText(
                                            requireContext(),
                                            erro,
                                            Toast.LENGTH_LONG
                                    ).show())
                    );
                },
                erro -> requireActivity().runOnUiThread(() ->
                        Toast.makeText(
                                requireContext(),
                                erro,
                                Toast.LENGTH_LONG
                        ).show())
        );
    }

    private void carregarLocais() {
        if (!localizacaoProvider.possuiPermissao(requireContext())) {
            Toast.makeText(
                    requireContext(),
                    "Permissão de localização necessária para carregar locais próximos.",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }

        localizacaoProvider.obterLocalizacao((latitude, longitude) ->
                requireActivity().runOnUiThread(() -> {
                    if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
                        Toast.makeText(
                                requireContext(),
                                "Não foi possível obter a localização atual.",
                                Toast.LENGTH_LONG
                        ).show();
                        return;
                    }

                    Log.d(
                            "POSTAR",
                            "A carregar locais próximos para postagem em lat="
                                    + latitude
                                    + " lon="
                                    + longitude
                    );

                    localRepository.listarProximosRemoto(
                            latitude,
                            longitude,
                            lista -> requireActivity().runOnUiThread(() -> {
                                locais.clear();
                                locais.addAll(lista);

                                List<String> nomes = new ArrayList<>();
                                for (LocalCompleto local : lista) {
                                    nomes.add(montarEtiquetaLocal(local));
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                        requireContext(),
                                        android.R.layout.simple_spinner_item,
                                        nomes
                                );
                                adapter.setDropDownViewResource(
                                        android.R.layout.simple_spinner_dropdown_item
                                );
                                destinoAnuncio.setAdapter(adapter);
                            }),
                            erro -> requireActivity().runOnUiThread(() ->
                                    Toast.makeText(
                                            requireContext(),
                                            erro,
                                            Toast.LENGTH_LONG
                                    ).show())
                    );
                }));
    }

    private LocalDateTime lerDataOpcional(EditText campo, String nomeCampo) {
        String texto = campo.getText().toString().trim();
        if (texto.isEmpty()) {
            campo.setError(null);
            return null;
        }

        try {
            campo.setError(null);
            return LocalDateTime.parse(texto);
        } catch (DateTimeParseException e) {
            campo.setError("Use o formato AAAA-MM-DDTHH:MM");
            Toast.makeText(
                    requireContext(),
                    "Formato inválido para " + nomeCampo + ". Use AAAA-MM-DDTHH:MM.",
                    Toast.LENGTH_LONG
            ).show();
            return null;
        }
    }

    private String montarEtiquetaLocal(LocalCompleto local) {
        String nome = local.getLocal() != null && local.getLocal().getNome() != null
                ? local.getLocal().getNome()
                : "Local sem nome";

        if (local.getDistanciaMetros() != null) {
            return String.format(
                    Locale.ROOT,
                    "%s (%.0f m)",
                    nome,
                    local.getDistanciaMetros()
            );
        }

        return nome;
    }

    private void limparFormulario() {
        titulo.setText("");
        conteudo.setText("");
        dataInicio.setText("");
        dataFim.setText("");
        restricoes.setText("");
        if (destinoAnuncio.getAdapter() != null && destinoAnuncio.getAdapter().getCount() > 0) {
            destinoAnuncio.setSelection(0);
        }
        visibilidade.check(R.id.radioWhitelist);
        modoEntrega.check(R.id.radioCentralizado);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (wifiDirectManager != null) {
            wifiDirectManager.registar();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (wifiDirectManager != null) {
            wifiDirectManager.cancelarRegistro();
        }

        if (udpServidor != null) {
            udpServidor.parar();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NotNull String[] permissions,
            @NotNull int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != REQUEST_P2P_PERMISSOES) {
            return;
        }

        boolean localizacaoConcedida = false;
        for (int i = 0; i < permissions.length; i++) {
            if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i])
                    && grantResults.length > i
                    && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                localizacaoConcedida = true;
                break;
            }
        }

        if (localizacaoConcedida) {
            carregarLocais();
        }
    }
}
