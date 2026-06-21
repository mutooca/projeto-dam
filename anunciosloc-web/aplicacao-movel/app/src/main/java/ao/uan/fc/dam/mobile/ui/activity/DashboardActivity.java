package ao.uan.fc.dam.mobile.ui.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.ui.fragment.CaixaFragment;
import ao.uan.fc.dam.mobile.ui.fragment.InicioFragment;
import ao.uan.fc.dam.mobile.ui.fragment.LocaisFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PerfilFragment;
import ao.uan.fc.dam.mobile.ui.fragment.PostarFragment;

public class DashboardActivity extends AppCompatActivity {
    private BottomNavigationView menu;
    private final Fragment inicioFragment = new InicioFragment();
    private final Fragment locaisFragment = new LocaisFragment();
    private final Fragment postarFragment = new PostarFragment();
    private final Fragment caixaFragment = new CaixaFragment();
    private final Fragment perfilFragment = new PerfilFragment();

    private Fragment atual_fragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);




        menu = findViewById(R.id.menuBar);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frameContainer, inicioFragment);
        fragmentTransaction.commit();
        menu.setSelectedItemId(R.id.home);


        menu.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.home) {
                trocarFragment(inicioFragment);
                return true;
            }
            if(item.getItemId() == R.id.local){
                trocarFragment(locaisFragment);
                return true;
            }
            if(item.getItemId() == R.id.post){
                trocarFragment(postarFragment);
                return true;
            }
            if(item.getItemId() == R.id.inbox){
                trocarFragment(caixaFragment);
                return true;
            }
            if(item.getItemId() == R.id.profile){
                trocarFragment(perfilFragment);
                return true;
            }
            return false;
        });

    }
    public void trocarFragment(Fragment fragment){
        if(atual_fragment == fragment) return;

        atual_fragment = fragment;

        getSupportFragmentManager().beginTransaction().replace(R.id.frameContainer, fragment).commit();
    }
}