package ao.uan.fc.dam.mobile.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.adapter.LocalAdapter;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.ui.viewmodel.LocaisViewModel;

/**
 * Arquiteto: Gestão de Locais (F3)
 */
public class LocaisFragment extends Fragment {
    private static final String TAG = "LocaisFragment";
    private LocalAdapter adapter;
    private LocaisViewModel viewModel;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locais, container, false);
        viewModel = new ViewModelProvider(this).get(LocaisViewModel.class);

        setupRecyclerView(view);
        setupObservers();

        view.findViewById(R.id.fabAddLocal).setOnClickListener(v -> mostrarDialogCriarLocal());

        return view;
    }

    private void setupRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLocais);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new LocalAdapter(new ArrayList<>(), new LocalAdapter.OnLocalClickListener() {
            @Override
            public void onLocalClick(Local local) {}

            @Override
            public void onLocalDelete(Local local) {
                confirmarExclusao(local);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    private void setupObservers() {
        viewModel.getLocales().observe(getViewLifecycleOwner(), locales -> {
            if (locales != null) adapter.atualizar(locales);
        });
        
        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null) Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        });
    }

    private void mostrarDialogCriarLocal() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 20);

        final EditText edtNome = new EditText(requireContext());
        edtNome.setHint("Nome do Local (ex: Largo da Independência)");
        edtNome.setInputType(InputType.TYPE_CLASS_TEXT);
        layout.addView(edtNome);

        new AlertDialog.Builder(requireContext())
                .setTitle("Registar Local Atual")
                .setMessage("A sua posição GPS será capturada automaticamente.")
                .setView(layout)
                .setPositiveButton("Registar", (dialog, which) -> {
                    String nome = edtNome.getText().toString().trim();
                    if (!nome.isEmpty()) {
                        verificarPermissaoEObterPosicao(nome);
                    } else {
                        Toast.makeText(requireContext(), "Nome é obrigatório", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void verificarPermissaoEObterPosicao(String nome) {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireContext(), "Permissão de GPS necessária", Toast.LENGTH_SHORT).show();
            return;
        }
        obterPosicaoECriarLocal(nome);
    }

    @SuppressLint("MissingPermission")
    private void obterPosicaoECriarLocal(String nome) {
        Toast.makeText(requireContext(), "A obter localização...", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Solicitando localização atual para local: " + nome);
        
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        enviarCriacaoLocal(nome, location);
                    } else {
                        Log.w(TAG, "Localização atual é nula, tentando última conhecida...");
                        fusedLocationClient.getLastLocation().addOnSuccessListener(lastLocation -> {
                            if (lastLocation != null) {
                                enviarCriacaoLocal(nome, lastLocation);
                            } else {
                                Log.w(TAG, "Ambas as localizações são nulas.");
                                Toast.makeText(requireContext(), "Não foi possível obter GPS. Ative a localização no dispositivo/emulador.", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Falha ao obter localização", e);
                    Toast.makeText(requireContext(), "Erro ao aceder ao GPS: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void enviarCriacaoLocal(String nome, Location location) {
        Log.d(TAG, "Localização utilizada: " + location.getLatitude() + ", " + location.getLongitude());
        viewModel.createLocal(nome, location.getLatitude(), location.getLongitude(), 20);
        Toast.makeText(requireContext(), "Enviando local '" + nome + "'...", Toast.LENGTH_SHORT).show();
    }

    private void confirmarExclusao(Local local) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remover")
                .setMessage("Deseja remover '" + local.getNome() + "'?")
                .setPositiveButton("Remover", (d, w) -> viewModel.removeLocal(local))
                .setNegativeButton("Cancelar", null).show();
    }
}
