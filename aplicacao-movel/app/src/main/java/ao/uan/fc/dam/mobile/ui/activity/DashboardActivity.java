package ao.uan.fc.dam.mobile.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.service.AnunciosSyncService;
import ao.uan.fc.dam.mobile.ui.fragment.CaixaFragment;
import ao.uan.fc.dam.mobile.ui.fragment.InicioFragment;
import ao.uan.fc.dam.mobile.ui.fragment.LocaisFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PerfilFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PostarFragment;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView menu;

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = true;
                for (Boolean b : result.values()) if (!b) granted = false;
                if (granted) iniciarServicoSync();
                else Toast.makeText(this, "Permissões necessárias para notificações", Toast.LENGTH_LONG).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        if (!SessionManager.hasSession(this)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        menu = findViewById(R.id.menuBar);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, new InicioFragment())
                .commit();
            menu.setSelectedItemId(R.id.home);
            
            verificarIntent(getIntent());
        }

        menu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) trocarFragment(new InicioFragment());
            else if (itemId == R.id.local) trocarFragment(new LocaisFragment());
            else if (itemId == R.id.post) trocarFragment(new PostarFragment());
            else if (itemId == R.id.profile) trocarFragment(new PerfilFragment());
            return true;
        });

        verificarPermissoesEServico();
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        verificarIntent(intent);
    }

    private void verificarIntent(Intent intent) {
        if (intent != null && intent.hasExtra("OPEN_ANUNCIO")) {
            Anuncio anuncio = (Anuncio) intent.getSerializableExtra("OPEN_ANUNCIO");
            if (anuncio != null) {
                abrirDetalhesAnuncio(anuncio);
            }
        }
    }

    private void abrirDetalhesAnuncio(Anuncio anuncio) {
        // Agora usamos o CaixaFragment como tela de detalhes
        CaixaFragment fragment = CaixaFragment.novaInstancia(anuncio);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void verificarPermissoesEServico() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS);
        }

        boolean allGranted = true;
        for (String p : permissions) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                break;
            }
        }

        if (allGranted) iniciarServicoSync();
        else requestPermissionLauncher.launch(permissions.toArray(new String[0]));
    }

    private void iniciarServicoSync() {
        Intent serviceIntent = new Intent(this, AnunciosSyncService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void trocarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit();
    }
}
