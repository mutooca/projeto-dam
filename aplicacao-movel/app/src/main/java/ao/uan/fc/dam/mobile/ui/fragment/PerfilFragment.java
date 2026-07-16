package ao.uan.fc.dam.mobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;
import ao.uan.fc.dam.mobile.data.repository.HistoricoRepository;
import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;
import ao.uan.fc.dam.mobile.network.dto.PerfilItemDto;
import ao.uan.fc.dam.mobile.ui.activity.LoginActivity;
import ao.uan.fc.dam.mobile.ui.adapter.HistoricoAdapter;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class PerfilFragment extends Fragment {
    private static final String TAG = "PerfilFragment";

    private TextView nomeUtilizador;
    private TextView emailUtilizador;
    private TextView saldo;
    private TextView anuncioPublicado;
    private TextView anuncioEntregue;
    private UtilizadorRepository utilizadorRepository;
    private FrameLayout btnLogout;
    private ImageView btnEditName;
    private MaterialButton btnAddProperty;
    private SessionManager sessionManager;
    private RecyclerView recyclerHistorico;
    private HistoricoAdapter adapter;
    private HistoricoRepository historicoRepository;
    private AnuncioRepository anuncioRepository;
    private LinearLayout layoutProperties;
    private LinearLayout layoutCatalogo;
    private AtributoPerfilRepository atributoPerfilRepository;
    private List<PerfilItemDto> meuPerfilAtual = new ArrayList<>();

    public PerfilFragment() {
        super(R.layout.fragment_perfil);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        nomeUtilizador = view.findViewById(R.id.textView23);
        emailUtilizador = view.findViewById(R.id.textView24);
        saldo = view.findViewById(R.id.textView25);
        anuncioPublicado = view.findViewById(R.id.textView26);
        anuncioEntregue = view.findViewById(R.id.textView28);
        btnLogout = view.findViewById(R.id.btnLogout);
        btnEditName = view.findViewById(R.id.btnEditName);
        btnAddProperty = view.findViewById(R.id.btnAddProperty);
        layoutProperties = view.findViewById(R.id.layoutProperties);   // só o findViewById aqui
        layoutCatalogo = view.findViewById(R.id.layoutCatalogo);

        sessionManager = new SessionManager(requireContext());          // <- sessionManager nasce aqui
        anuncioRepository = new AnuncioRepository(requireContext());
        utilizadorRepository = new UtilizadorRepository(requireContext());
        atributoPerfilRepository = new AtributoPerfilRepository(requireContext());

        carregarPerfil();
        carregarAtributos();   // <- agora sim, sessionManager já existe

        recyclerHistorico = view.findViewById(R.id.recyclerViewPerfil);

        adapter = new HistoricoAdapter();
        recyclerHistorico.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerHistorico.setAdapter(adapter);
        historicoRepository = new HistoricoRepository(requireContext());

        historicoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(),
                lista -> requireActivity().runOnUiThread(() -> {
                    adapter.setHistorico(lista);
                })
        );

        btnLogout.setOnClickListener(v -> {
            utilizadorRepository.terminarSessaoRemota(resultado ->
                    requireActivity().runOnUiThread(() -> {
                        sessionManager.terminarSessao();
                        Intent intent = new Intent(requireContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        requireActivity().finish();
                    })
            );
        });

        btnEditName.setOnClickListener(v -> abrirDialogEditarPerfil());
        btnAddProperty.setOnClickListener(v -> abrirDialogNovoAtributo());   // <- listener fica aqui, separado do findViewById
    }
    private void carregarPerfil(){
        nomeUtilizador.setText(sessionManager.getNome());
        emailUtilizador.setText(sessionManager.getEmail());
        utilizadorRepository.buscarPorId(
                sessionManager.getIdUtilizador(),
                utilizador -> requireActivity().runOnUiThread(() -> {

                    if (utilizador == null) return;

                    nomeUtilizador.setText(utilizador.getNome());
                    emailUtilizador.setText(utilizador.getEmail());
                    saldo.setText(String.valueOf(utilizador.getSaldo()));
                })
        );
    }

    private void abrirDialogEditarPerfil() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil, null);

        EditText nome = dialogView.findViewById(R.id.editNome);
        EditText email = dialogView.findViewById(R.id.editEmail);
        EditText senha = dialogView.findViewById(R.id.editSenha);

        nome.setText(sessionManager.getNome());
        email.setText(sessionManager.getEmail());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Editar Perfil")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    guardarPerfil(nome.getText().toString().trim(), email.getText().toString().trim(), senha.getText().toString());
                }).setNegativeButton("Cancelar", null).show();
    }

    private void guardarPerfil(String nome, String email, String senha){

        utilizadorRepository.buscarPorId(
                sessionManager.getIdUtilizador(),
                utilizador -> {

                    if(utilizador == null)
                        return;


                    utilizador.setNome(nome);
                    utilizador.setEmail(email);


                    if(!senha.isBlank()){
                        utilizador.setPalavraChave(senha);
                    }


                    utilizadorRepository.atualizarRemoto(
                            utilizador,
                            resultado -> requireActivity().runOnUiThread(() -> {

                                if(resultado != null){

                                    sessionManager.iniciarSessao(
                                            resultado.getIdUtilizador(),
                                            resultado.getNome(),
                                            resultado.getEmail()
                                    );


                                    carregarPerfil();


                                    Toast.makeText(
                                            requireContext(),
                                            "Perfil atualizado.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                } else {

                                    Toast.makeText(
                                            requireContext(),
                                            "Falha ao atualizar perfil no servidor.",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                }

                            })
                    );

                }
        );
    }

    /**
     * O perfil e o catálogo são partilhados por todos os utilizadores (residem no servidor).
     * Por isso carregamos sempre o MEU perfil primeiro e, com essa referência, o catálogo global,
     * para saber quais os pares chave=valor que já são meus.
     */
    private void carregarAtributos() {
        atributoPerfilRepository.listarMeuPerfilRemoto(
                meuPerfil -> requireActivity().runOnUiThread(() -> {
                    meuPerfilAtual = meuPerfil != null ? new ArrayList<>(meuPerfil) : new ArrayList<>();
                    renderMeusAtributos();
                    carregarCatalogo();
                }),
                erro -> requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Erro ao carregar o meu perfil: " + erro);
                    Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show();
                })
        );
    }

    private void carregarCatalogo() {
        atributoPerfilRepository.listarCatalogoRemoto(
                catalogo -> requireActivity().runOnUiThread(() -> renderCatalogo(catalogo)),
                erro -> requireActivity().runOnUiThread(() -> {
                    Log.e(TAG, "Erro ao carregar catálogo de atributos: " + erro);
                    Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show();
                })
        );
    }

    private void renderMeusAtributos() {
        layoutProperties.removeAllViews();

        for (PerfilItemDto atributo : meuPerfilAtual) {
            View itemView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_atributo_perfil, layoutProperties, false);

            TextView txtChave = itemView.findViewById(R.id.txtChave);
            TextView txtValor = itemView.findViewById(R.id.txtValor);
            ImageView btnRemover = itemView.findViewById(R.id.btnRemoverAtributo);

            txtChave.setText(atributo.getChave());
            txtValor.setText(atributo.getValor());

            btnRemover.setOnClickListener(v -> {
                Log.d(TAG, "A remover atributo do meu perfil: " + atributo.getChave() + "=" + atributo.getValor());
                atributoPerfilRepository.removerChaveRemota(
                        atributo.getChave(),
                        resultado -> requireActivity().runOnUiThread(() -> {
                            Toast.makeText(requireContext(), "Atributo removido.", Toast.LENGTH_SHORT).show();
                            carregarAtributos();
                        }),
                        erro -> requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
                );
            });

            layoutProperties.addView(itemView);
        }
    }

    private void renderCatalogo(List<PerfilItemDto> catalogo) {
        layoutCatalogo.removeAllViews();

        if (catalogo == null) {
            return;
        }

        for (PerfilItemDto item : catalogo) {
            if (jaEhMeuAtributo(item.getChave(), item.getValor())) {
                continue;
            }

            View itemView = LayoutInflater.from(requireContext())
                    .inflate(R.layout.item_atributo_catalogo, layoutCatalogo, false);

            TextView txtChave = itemView.findViewById(R.id.txtChaveCatalogo);
            TextView txtValor = itemView.findViewById(R.id.txtValorCatalogo);
            ImageView btnAdicionar = itemView.findViewById(R.id.btnAdicionarAtributo);

            txtChave.setText(item.getChave());
            txtValor.setText(item.getValor());

            btnAdicionar.setOnClickListener(v -> adicionarAoMeuPerfil(item.getChave(), item.getValor()));
            itemView.setOnClickListener(v -> adicionarAoMeuPerfil(item.getChave(), item.getValor()));

            layoutCatalogo.addView(itemView);
        }
    }

    private boolean jaEhMeuAtributo(String chave, String valor) {
        for (PerfilItemDto meu : meuPerfilAtual) {
            if (meu.getChave().equalsIgnoreCase(chave) && meu.getValor().equalsIgnoreCase(valor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * O endpoint /api/perfil substitui SEMPRE o perfil completo do utilizador, por isso
     * enviamos a lista actual + o novo par (substituindo qualquer valor anterior da mesma chave,
     * já que o servidor só permite um valor por chave por utilizador).
     */
    private void adicionarAoMeuPerfil(String chave, String valor) {
        List<PerfilItemDto> atualizado = new ArrayList<>();
        for (PerfilItemDto item : meuPerfilAtual) {
            if (!item.getChave().equalsIgnoreCase(chave)) {
                atualizado.add(item);
            }
        }
        atualizado.add(new PerfilItemDto(chave, valor));

        Log.d(TAG, "A adicionar atributo ao meu perfil: " + chave + "=" + valor
                + " (total apos adicionar=" + atualizado.size() + ")");

        atributoPerfilRepository.guardarPerfilRemoto(
                atualizado,
                resultado -> requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Atributo adicionado ao seu perfil.", Toast.LENGTH_SHORT).show();
                    carregarAtributos();
                }),
                erro -> requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
        );
    }

    private void abrirDialogNovoAtributo() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_property, null);

        TextInputEditText editKey = dialogView.findViewById(R.id.editKey);
        TextInputEditText editValue = dialogView.findViewById(R.id.editValue);

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Novo Atributo")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String chave = editKey.getText().toString().trim();
                    String valor = editValue.getText().toString().trim();

                    if (chave.isEmpty() || valor.isEmpty()) {
                        Toast.makeText(requireContext(), "Preencha ambos os campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    adicionarAoMeuPerfil(chave, valor);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
