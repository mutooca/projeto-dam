package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.ui.fragment.DetailAnuncioFragment;
import ao.uan.fc.dam.mobile.ui.fragment.InicioFragment;
import ao.uan.fc.dam.mobile.ui.fragment.LocaisFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PerfilFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PostarFragment;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        SessionManager session = new SessionManager(this);

        if (!session.estaAutenticado()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        menu = findViewById(R.id.menuBar);
        
        if (savedInstanceState == null) {
            tratarIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        tratarIntent(intent);
    }

    private void tratarIntent(Intent intent) {
        if (intent != null && intent.getBooleanExtra("abrir_detalhe", false)) {
            String msgId = intent.getStringExtra("msg_id");
            if (msgId != null) {
                Bundle bundle = new Bundle();
                bundle.putString("msg_id", msgId);
                DetailAnuncioFragment fragment = new DetailAnuncioFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frameContainer, fragment)
                        .addToBackStack(null)
                        .commit();
                return;
            }
        }

        // Navegação padrão se não houver intent de detalhe
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.frameContainer);
        if (current == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameContainer, new InicioFragment())
                    .commit();
            menu.setSelectedItemId(R.id.home);
        }

        menu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) trocarFragment(new InicioFragment());
            else if (itemId == R.id.local) trocarFragment(new LocaisFragment());
            else if (itemId == R.id.post) trocarFragment(new PostarFragment());
            else if (itemId == R.id.profile) trocarFragment(new PerfilFragment());
            return true;
        });
    }

    private void trocarFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit();
    }
}
