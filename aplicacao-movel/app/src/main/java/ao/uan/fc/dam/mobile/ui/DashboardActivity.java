package ao.uan.fc.dam.mobile.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.ui.fragments.CaixaFragment;
import ao.uan.fc.dam.mobile.ui.fragments.InicioFragment;
import ao.uan.fc.dam.mobile.ui.fragments.LocaisFragment;
import ao.uan.fc.dam.mobile.ui.fragments.PerfilFragment;
import ao.uan.fc.dam.mobile.ui.fragments.PostarFragment;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        menu = findViewById(R.id.menuBar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameContainer, new InicioFragment());
        fragmentTransaction.commit();


        menu.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home) {
                trocarFragment(new InicioFragment());
                return true;
            }
            if(item.getItemId() == R.id.local){
                trocarFragment(new LocaisFragment());
                return true;
            }
            if(item.getItemId() == R.id.post){
                trocarFragment(new PostarFragment());
                return true;
            }
            if(item.getItemId() == R.id.inbox){
                trocarFragment(new CaixaFragment());
                return true;
            }
            if(item.getItemId() == R.id.profile){
                trocarFragment(new PerfilFragment());
                return true;
            }
            return false;
        });

    }
    public void trocarFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }
}