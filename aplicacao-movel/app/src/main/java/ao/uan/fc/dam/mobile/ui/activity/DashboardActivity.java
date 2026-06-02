package ao.uan.fc.dam.mobile.ui.activity;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.ui.fragment.CaixaFragment;
import ao.uan.fc.dam.mobile.ui.fragment.InicioFragment;
import ao.uan.fc.dam.mobile.ui.fragment.LocaisFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PerfilFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PostarFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {
    private Fragment atual_fragment;
    private BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        this.menu = (BottomNavigationView) findViewById(R.id.menuBar);
        
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .replace(R.id.frameContainer, new InicioFragment())
                .commit();
        }

        this.menu.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                trocarFragment(new InicioFragment());
                return true;
            }
            if (itemId == R.id.local) {
                trocarFragment(new LocaisFragment());
                return true;
            }
            if (itemId == R.id.post) {
                trocarFragment(new PostarFragment());
                return true;
            }
            if (itemId == R.id.inbox) {
                trocarFragment(new CaixaFragment());
                return true;
            }
            if (itemId == R.id.profile) {
                trocarFragment(new PerfilFragment());
                return true;
            }
            return false;
        });
    }

    public void trocarFragment(Fragment fragment) {
        if (this.atual_fragment != null && this.atual_fragment.getClass().equals(fragment.getClass())) {
            return;
        }
        this.atual_fragment = fragment;
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frameContainer, fragment)
            .commit();
    }
}
