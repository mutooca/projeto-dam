package ao.uan.fc.dam.mobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
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

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;
import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;
import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;
import ao.uan.fc.dam.mobile.data.repository.HistoricoRepository;
import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;
import ao.uan.fc.dam.mobile.ui.activity.LoginActivity;
import ao.uan.fc.dam.mobile.ui.adapter.HistoricoAdapter;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class PerfilFragment extends Fragment {
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
    private AtributoPerfilRepository atributoPerfilRepository;

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
            sessionManager.terminarSessao();
            Intent intent = new Intent(requireContext(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
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

    private void carregarAtributos() {
        atributoPerfilRepository.listarPorUtilizador(
                sessionManager.getIdUtilizador(),
                lista -> requireActivity().runOnUiThread(() -> {
                    layoutProperties.removeAllViews();

                    for (AtributoPerfil atributo : lista) {
                        View itemView = LayoutInflater.from(requireContext())
                                .inflate(R.layout.item_atributo_perfil, layoutProperties, false);

                        TextView txtChave = itemView.findViewById(R.id.txtChave);
                        TextView txtValor = itemView.findViewById(R.id.txtValor);
                        ImageView btnRemover = itemView.findViewById(R.id.btnRemoverAtributo);

                        txtChave.setText(atributo.getChave());
                        txtValor.setText(atributo.getValor());

                        btnRemover.setOnClickListener(v ->
                                atributoPerfilRepository.remover(atributo, resultado ->
                                        requireActivity().runOnUiThread(this::carregarAtributos))
                        );

                        layoutProperties.addView(itemView);
                    }
                })
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

                    AtributoPerfil atributo = new AtributoPerfil(chave, valor, sessionManager.getIdUtilizador());

                    atributoPerfilRepository.inserir(atributo, id ->
                            requireActivity().runOnUiThread(this::carregarAtributos)
                    );
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}