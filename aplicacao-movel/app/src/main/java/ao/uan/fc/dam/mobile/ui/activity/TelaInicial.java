package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.security.SessionManager;

public class TelaInicial extends AppCompatActivity {


    private Button btnLogin;
    private  Button btnRegistar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SessionManager.hasSession(this)) {
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_tela_inicial);

        btnRegistar = findViewById(R.id.btnRegistar);
        btnLogin = findViewById(R.id.btnEntrar);

        // 2. Ação do botão Registar
        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TelaInicial.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(TelaInicial.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
