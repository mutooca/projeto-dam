package ao.uan.fc.dam.mobile.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.PerfilAdapter;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.model.Utilizador;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.ui.activity.LoginActivity;
import ao.uan.fc.dam.mobile.ui.viewmodel.AnunciosViewModel;
import ao.uan.fc.dam.mobile.ui.viewmodel.PerfilViewModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PerfilFragment extends Fragment {
    private TextView txtNome, txtEmail, txtSaldo, txtAnunciosCount, txtEntregasCount;
    private PerfilAdapter adapter;
    private LinearLayout layoutProperties;
    private PerfilViewModel viewModel;
    private AnunciosViewModel anunciosViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        viewModel = new ViewModelProvider(this).get(PerfilViewModel.class);
        anunciosViewModel = new ViewModelProvider(this).get(AnunciosViewModel.class);

        txtNome = view.findViewById(R.id.textView23);
        txtEmail = view.findViewById(R.id.textView24);
        txtSaldo = view.findViewById(R.id.textView25);
        txtAnunciosCount = view.findViewById(R.id.textView26);
        txtEntregasCount = view.findViewById(R.id.textView28);
        layoutProperties = view.findViewById(R.id.layoutProperties);

        RecyclerView recycler = view.findViewById(R.id.recyclerViewPerfil);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new PerfilAdapter(new ArrayList<>());
        recycler.setAdapter(adapter);

        view.findViewById(R.id.btnEditName).setOnClickListener(v -> mostrarDialogEditarNome());
        view.findViewById(R.id.btnAddProperty).setOnClickListener(v -> mostrarDialogAddAtributo("", ""));
        view.findViewById(R.id.btnLogout).setOnClickListener(v -> confirmarLogout());
        
        setupObservers();
        return view;
    }

    private void setupObservers() {
        viewModel.getProfile().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                txtNome.setText(user.getNome());
                txtEmail.setText(user.getEmail());
                txtSaldo.setText("Saldo: " + (user.getSaldo() != null ? user.getSaldo() : 0));
                txtAnunciosCount.setText((user.getTotalAnuncios() != null ? user.getTotalAnuncios() : 0) + " Anúncios");
                txtEntregasCount.setText((user.getTotalEntregas() != null ? user.getTotalEntregas() : 0) + " Entregas");
                
                renderProperties(user.getPreferenciaAnuncio());
            }
        });

        anunciosViewModel.getMeusAnuncios().observe(getViewLifecycleOwner(), anuncios -> {
            if (anuncios != null) {
                adapter.atualizar(anuncios);
            }
        });
    }

    private void renderProperties(String prefs) {
        layoutProperties.removeAllViews();
        if (prefs == null || prefs.isEmpty()) {
            TextView empty = new TextView(requireContext());
            empty.setText("Nenhum atributo definido.");
            empty.setPadding(32, 16, 32, 16);
            empty.setTextColor(ContextCompat.getColor(requireContext(), R.color.cor_app));
            layoutProperties.addView(empty);
            return;
        }

        String[] pairs = prefs.split(",");
        for (String pair : pairs) {
            if (!pair.contains("=")) continue;
            
            View propView = getLayoutInflater().inflate(R.layout.item_locais_fragment, layoutProperties, false);
            TextView text = propView.findViewById(R.id.txtLocalNome);
            text.setText(pair.replace("=", " : "));
            text.setTextSize(16);
            
            propView.findViewById(R.id.iconContainer).setBackgroundTintList(null);
            ((android.widget.ImageView)propView.findViewById(R.id.imgLocalIcon)).setImageResource(R.drawable.settings_icon);
            
            propView.setOnClickListener(v -> mostrarMenuAtributo(pair));
            layoutProperties.addView(propView);
        }
    }

    private void mostrarMenuAtributo(String pair) {
        String[] parts = pair.split("=");
        String key = parts[0];
        String value = parts.length > 1 ? parts[1] : "";

        new AlertDialog.Builder(requireContext())
                .setTitle("Atributo: " + key)
                .setItems(new String[]{"Editar", "Remover"}, (dialog, which) -> {
                    if (which == 0) mostrarDialogAddAtributo(key, value);
                    else removerAtributo(pair);
                }).show();
    }

    private void removerAtributo(String targetPair) {
        Utilizador user = viewModel.getProfile().getValue();
        if (user == null) return;
        
        String current = user.getPreferenciaAnuncio();
        List<String> list = new ArrayList<>();
        for (String p : current.split(",")) {
            if (!p.equals(targetPair)) list.add(p);
        }
        String novo = String.join(",", list);
        
        viewModel.updatePrefsLocal(novo);
        salvarPreferenciasRemoto(novo);
        Toast.makeText(requireContext(), "Removido", Toast.LENGTH_SHORT).show();
    }

    private void mostrarDialogAddAtributo(String oldKey, String oldVal) {
        View view = getLayoutInflater().inflate(R.layout.dialog_add_property, null);
        TextInputEditText editKey = view.findViewById(R.id.editKey);
        TextInputEditText editValue = view.findViewById(R.id.editValue);
        
        editKey.setText(oldKey);
        editValue.setText(oldVal);

        new AlertDialog.Builder(requireContext())
                .setView(view)
                .setPositiveButton("Confirmar", (d, w) -> {
                    String k = editKey.getText().toString().trim();
                    String v = editValue.getText().toString().trim();
                    if (!k.isEmpty() && !v.isEmpty()) {
                        Utilizador user = viewModel.getProfile().getValue();
                        String current = (user != null && user.getPreferenciaAnuncio() != null) ? user.getPreferenciaAnuncio() : "";
                        
                        List<String> list = new ArrayList<>();
                        if (!current.isEmpty()) {
                            for (String p : current.split(",")) {
                                if (!oldKey.isEmpty() && p.startsWith(oldKey + "=")) continue;
                                list.add(p);
                            }
                        }
                        list.add(k + "=" + v);
                        String finalStr = String.join(",", list);
                        
                        viewModel.updatePrefsLocal(finalStr);
                        salvarPreferenciasRemoto(finalStr);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogEditarNome() {
        EditText input = new EditText(requireContext());
        input.setText(txtNome.getText().toString());
        new AlertDialog.Builder(requireContext()).setTitle("Editar Nome").setView(input)
                .setPositiveButton("Salvar", (d, w) -> {
                    String novo = input.getText().toString().trim();
                    if (!novo.isEmpty()) {
                        viewModel.updateNameLocal(novo);
                        atualizarNomeRemoto(novo);
                    }
                }).setNegativeButton("Cancelar", null).show();
    }

    private void atualizarNomeRemoto(String novoNome) {
        Map<String, Object> req = new HashMap<>();
        req.put("email", SessionManager.getEmail(requireContext()));
        req.put("nome", novoNome);
        RetrofitClient.getInstance().getApi().editarPerfil(req).enqueue(new Callback<Utilizador>() {
            @Override
            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {}
            @Override public void onFailure(Call<Utilizador> call, Throwable t) {}
        });
    }

    private void salvarPreferenciasRemoto(String prefs) {
        String email = SessionManager.getEmail(requireContext());
        RetrofitClient.getInstance().getApi().atualizarPreferencias(email, prefs).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {}
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    private void confirmarLogout() {
        new AlertDialog.Builder(requireContext()).setTitle("Sair")
                .setMessage("Deseja realmente encerrar a sessão?")
                .setPositiveButton("Sim, Sair", (d, w) -> {
                    SessionManager.clear(requireContext());
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                }).setNegativeButton("Cancelar", null).show();
    }
}
