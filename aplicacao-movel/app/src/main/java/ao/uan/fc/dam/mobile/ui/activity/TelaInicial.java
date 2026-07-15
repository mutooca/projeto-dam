package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class TelaInicial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        if (session.estaAutenticado()) {

            startActivity(new Intent(
                    this,
                    DashboardActivity.class));

            finish();

            return;

        }

        setContentView(R.layout.activity_tela_inicial);

        Button btnLogin = findViewById(R.id.btnEntrar);

        Button btnRegistar = findViewById(R.id.btnRegistar);

        btnLogin.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        LoginActivity.class)));

        btnRegistar.setOnClickListener(v ->
                startActivity(new Intent(
                        this,
                        CadastroActivity.class)));

    }

}