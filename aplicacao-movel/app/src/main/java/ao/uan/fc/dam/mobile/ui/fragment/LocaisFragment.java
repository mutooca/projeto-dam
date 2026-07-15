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

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.contentProvider.LocalizacaoProvider;
import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;
import ao.uan.fc.dam.mobile.data.entity.Local;
import ao.uan.fc.dam.mobile.data.enums.TipoCoordenada;
import ao.uan.fc.dam.mobile.data.repository.CoordenadaGpsRepository;
import ao.uan.fc.dam.mobile.data.repository.LocalRepository;
import ao.uan.fc.dam.mobile.ui.adapter.LocalAdapter;


public class LocaisFragment extends Fragment {
    private SearchView pesquisar;
    private RecyclerView recyclerView;
    private static final int REQUEST_LOCATION = 100;
    private LocalAdapter adapter;
    private LocalRepository repository;
    private LocalizacaoProvider localizacaoProvider;
    private CoordenadaGpsRepository coordenadaGpsRepository;
    private ExtendedFloatingActionButton btnNovoLocal;


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
        coordenadaGpsRepository = new CoordenadaGpsRepository(requireContext());
        btnNovoLocal = view.findViewById(R.id.fabAddLocal);

        verificarPermissaoLocalizacao();

        carregarLocais();

        adapter.setOnLocalClickListener(new LocalAdapter.OnLocalClickListener() {
            @Override
            public void onClick(Local local) {
                editarLocal(local);
            }

            @Override
            public void onLongClick(Local local) {
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
                repository.pesquisarComCoordenadas(s, lista -> {
                    requireActivity().runOnUiThread(() -> {
                        adapter.setLocais(lista);
                    });
                });
                return true;
            }
        });

        btnNovoLocal.setOnClickListener(v->{
            abrirDialogNovoLocal();
        });
    }

    private void carregarLocais(){
        repository.listarTodosComCoordenadas(lista -> {
            requireActivity().runOnUiThread(() -> {
                adapter.setLocais(lista);
            });
        });
    }

    private void editarLocal(Local local){
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_novo_local,null);
        EditText nome = view.findViewById(R.id.editNomeLocal);
        nome.setText(local.getNome());

        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Editar Local")
                .setView(view)
                .setPositiveButton("Guardar",(d,w)->{
                    local.setNome(nome.getText().toString());
                    repository.atualizar(local, resultado -> {
                        requireActivity().runOnUiThread(() -> {
                            carregarLocais();
                        });
                    });
                })
                .setNegativeButton("Cancelar",null)
                .show();
    }

    private void confirmarRemocao(Local local){
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Remover Local")
                .setMessage("Deseja remover este local?")

                .setPositiveButton("Sim",(d,w)->{
                    repository.remover(local, resultado -> {
                        requireActivity().runOnUiThread(() -> {
                            carregarLocais();
                        });
                    });
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
        if(nome.trim().isEmpty()){
            return;
        }

        if(!localizacaoProvider.possuiPermissao(requireContext())){
            Toast.makeText(requireContext(), "Permissão de localização necessária", Toast.LENGTH_LONG).show();
            return;
        }

        localizacaoProvider.obterLocalizacao(
                (latitude, longitude) -> {


                    Local local = new Local();
                    local.setNome(nome);
                    local.setTipoCoordenada(
                            TipoCoordenada.GPS
                    );
                    Log.d("LOCAL", "GPS recebido: "+latitude+" "+longitude);
                    repository.inserir(local,idLocal -> {
                        Log.d("LOCAL", "ID Local criado: "+idLocal);
                        CoordenadaGps gps =
                                new CoordenadaGps();
                        gps.setLatitude(latitude);
                        gps.setLongitude(longitude);
                        gps.setRaio(100);
                        gps.setIdLocal(
                                idLocal.intValue()
                        );
                        coordenadaGpsRepository.inserir(
                                gps,
                                idGps -> {
                                    requireActivity()
                                            .runOnUiThread(
                                                    this::carregarLocais
                                            );
                                }
                        );
                    });
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
                // Permissão concedida
            }else{
                Toast.makeText(requireContext(), "A localização é necessária para criar Locais.", Toast.LENGTH_LONG).show();
            }
        }
    }
}
