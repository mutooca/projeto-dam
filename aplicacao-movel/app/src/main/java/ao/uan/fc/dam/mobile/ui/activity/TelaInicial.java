package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import ao.uan.fc.dam.mobile.R;


public class TelaInicial extends AppCompatActivity {
    private Button btnLogin;
    private Button btnRegistar;

    @Override // androidx.fragment.app.FragmentActivity, androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tela_inicial);
        this.btnRegistar = (Button) findViewById(R.id.btnRegistar);
        this.btnLogin = (Button) findViewById(R.id.btnEntrar);
        this.btnRegistar.setOnClickListener(new View.OnClickListener() { // from class: ao.uan.fc.dam.mobile.ui.activity.TelaInicial.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent(TelaInicial.this, (Class<?>) CadastroActivity.class);
                TelaInicial.this.startActivity(intent);
            }
        });
        this.btnLogin.setOnClickListener(new View.OnClickListener() { // from class: ao.uan.fc.dam.mobile.ui.activity.TelaInicial.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                Intent intent = new Intent(TelaInicial.this, (Class<?>) LoginActivity.class);
                TelaInicial.this.startActivity(intent);
            }
        });
    }
}