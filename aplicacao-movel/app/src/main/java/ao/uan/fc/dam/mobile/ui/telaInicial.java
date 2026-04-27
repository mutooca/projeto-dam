package ao.uan.fc.dam.mobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import ao.uan.fc.dam.mobile.R;

public class telaInicial extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_inicial);

        Button btnRegistar = findViewById(R.id.btnRegistar);
        Button btnEntrar = findViewById(R.id.btnEntrar);

        // 2. Ação do botão Registar
        btnRegistar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(telaInicial.this, CadastroActivity.class);
                startActivity(intent);
            }
        });

        btnEntrar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(telaInicial.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
