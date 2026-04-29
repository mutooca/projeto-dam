package ao.uan.fc.dam.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ao.uan.fc.dam.mobile.R;

public class TelaInicial extends AppCompatActivity {


    private Button btnLogin;
    private  Button btnRegistar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
