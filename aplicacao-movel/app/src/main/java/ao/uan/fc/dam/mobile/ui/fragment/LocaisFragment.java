package ao.uan.fc.dam.mobile.ui.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.contentProvider.LocalizacaoProvider;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;
import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.ui.adapter.LocalAdapter;


public class LocaisFragment extends Fragment {
    private SearchView pesquisar;
    private RecyclerView recyclerView;
    private static final int REQUEST_LOCATION = 100;
    private LocalAdapter adapter;
    private LocalRepository repository;
    private LocalizacaoProvider localizacaoProvider;
    private ExtendedFloatingActionButton btnNovoLocal;
    private final List<LocalCompleto> locaisCarregados = new ArrayList<>();


    public LocaisFragment(){
        super(R.layout.fragment_locais);
    }

    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        pesquisar = view.findViewById(R.id.searchView2);
        recyclerView = view.findViewById(R.id.recyclerViewLocais);
        adapter = new LocalAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        repository = new LocalRepository(requireContext());
        localizacaoProvider = new LocalizacaoProvider(requireContext());
        btnNovoLocal = view.findViewById(R.id.fabAddLocal);

        verificarPermissaoLocalizacao();
        if (localizacaoProvider.possuiPermissao(requireContext())) {
            carregarLocais();
        }

        adapter.setOnLocalClickListener(new LocalAdapter.OnLocalClickListener() {
            @Override
            public void onClick(Local local) {
                if (local.getIdServidor() == null || local.getIdServidor().isBlank()) {
                    Toast.makeText(requireContext(),
                            "Este local não tem um ID remoto válido para edição.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                editarLocal(local);
            }

            @Override
            public void onLongClick(Local local) {
                if (local.getIdServidor() == null || local.getIdServidor().isBlank()) {
                    Toast.makeText(requireContext(),
                            "Este local não tem um ID remoto válido para remoção.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                confirmarRemocao(local);
            }
        });

        pesquisar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                filtrarLocais(s);
                return true;
            }
        });

        btnNovoLocal.setOnClickListener(v->{
            abrirDialogNovoLocal();
        });
    }

    private void carregarLocais(){
        if(!localizacaoProvider.possuiPermissao(requireContext())){
            Toast.makeText(requireContext(),
                    "Permissão de localização necessária para listar locais próximos.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        localizacaoProvider.obterLocalizacao((latitude, longitude) -> {
            requireActivity().runOnUiThread(() -> {
                if (Double.isNaN(latitude) || Double.isNaN(longitude)) {
                    Toast.makeText(requireContext(),
                            "Não foi possível obter a localização atual.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Log.d("LOCAL", "A listar locais próximos em lat=" + latitude + " lon=" + longitude);
                repository.listarProximosRemoto(
                        latitude,
                        longitude,
                        lista -> requireActivity().runOnUiThread(() -> {
                            locaisCarregados.clear();
                            locaisCarregados.addAll(lista);
                            adapter.setLocais(new ArrayList<>(lista));
                            Log.d("LOCAL", "Locais próximos carregados: " + lista.size());
                        }),
                        erro -> requireActivity().runOnUiThread(() ->
                                Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
                );
            });
        });
    }

    private void filtrarLocais(String texto) {
        String filtro = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        if (filtro.isEmpty()) {
            adapter.setLocais(new ArrayList<>(locaisCarregados));
            return;
        }

        List<LocalCompleto> filtrados = new ArrayList<>();
        for (LocalCompleto local : locaisCarregados) {
            if (local.getLocal() != null
                    && local.getLocal().getNome() != null
                    && local.getLocal().getNome().toLowerCase(Locale.ROOT).contains(filtro)) {
                filtrados.add(local);
            }
        }
        adapter.setLocais(filtrados);
    }

    private void editarLocal(Local local){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_novo_local,null);
        EditText nome = view.findViewById(R.id.editNomeLocal);
        nome.setText(local.getNome());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Editar Local")
                .setView(view)
                .setPositiveButton("Guardar",(d,w)->{
                    String novoNome = nome.getText().toString().trim();
                    if (novoNome.isEmpty()) {
                        Toast.makeText(requireContext(), "Introduza o nome do local.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.d("LOCAL", "A editar local remoto idServidor=" + local.getIdServidor() + " novoNome=" + novoNome);
                    repository.editarRemoto(
                            local.getIdServidor(),
                            novoNome,
                            null,
                            null,
                            null,
                            null,
                            resultado -> requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Local editado com sucesso.", Toast.LENGTH_SHORT).show();
                                carregarLocais();
                            }),
                            erro -> requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
                    );
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    private void confirmarRemocao(Local local){
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remover Local")
                .setMessage("Deseja remover este local?")

                .setPositiveButton("Sim",(d,w)->{
                    Log.d("LOCAL", "A remover local remoto idServidor=" + local.getIdServidor());
                    repository.removerRemoto(
                            local.getIdServidor(),
                            resultado -> requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Local removido com sucesso.", Toast.LENGTH_SHORT).show();
                                carregarLocais();
                            }),
                            erro -> requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
                    );
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    private void abrirDialogNovoLocal(){
        View view = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_novo_local,null);
        EditText nome = view.findViewById(R.id.editNomeLocal);
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Novo Local")
                .setView(view)
                .setPositiveButton("Guardar",(dialog,which)->{

                    criarLocal(nome.getText().toString());
                }).setNegativeButton("Cancelar",null).show();
    }

    private void criarLocal(String nome){
        Log.d("LOCAL", "Criar local iniciado");
        String nomeNormalizado = nome.trim();
        if(nomeNormalizado.isEmpty()){
            Toast.makeText(requireContext(), "Introduza o nome do local.", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!localizacaoProvider.possuiPermissao(requireContext())){
            Toast.makeText(requireContext(), "Permissão de localização necessária", Toast.LENGTH_LONG).show();
            return;
        }

        localizacaoProvider.obterLocalizacao(
                (latitude, longitude) -> {
                    Log.d("LOCAL", "GPS recebido: "+latitude+" "+longitude);
                    Log.d("LOCAL", "A criar local remoto via /api/locais/criar para nome=" + nomeNormalizado);
                    repository.criarRemoto(
                            nomeNormalizado,
                            latitude,
                            longitude,
                            100.0,
                            localCriado -> requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "Local criado com sucesso.", Toast.LENGTH_SHORT).show();
                                carregarLocais();
                            }),
                            erro -> requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), erro, Toast.LENGTH_LONG).show())
                    );
                }
        );
    }

    private void verificarPermissaoLocalizacao(){

        if(ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    REQUEST_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults);

        if(requestCode == REQUEST_LOCATION){
            if(grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                carregarLocais();
            }else{
                Toast.makeText(requireContext(), "A localização é necessária para criar Locais.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
