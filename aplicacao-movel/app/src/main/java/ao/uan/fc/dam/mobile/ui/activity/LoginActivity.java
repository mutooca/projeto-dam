package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.data.local.CacheManager;
import ao.uan.fc.dam.mobile.model.TicketResponse;
import ao.uan.fc.dam.mobile.security.SessionManager;
import ao.uan.fc.dam.mobile.service.LocationService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail, inputSenha;
    private Button btnEntrar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputSenha = findViewById(R.id.editTextTextPassword);
        btnEntrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressBar);
        TextView linkRegistar = findViewById(R.id.textView2);

        btnEntrar.setOnClickListener(v -> autenticar());
        linkRegistar.setOnClickListener(v -> startActivity(new Intent(this, CadastroActivity.class)));
    }

    private void autenticar() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        btnEntrar.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", senha);
        request.put("palavraChave", senha);

        RetrofitClient.getInstance().getApi().login(request).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                TicketResponse body = response.body();
                if (response.isSuccessful() && body != null && body.isSuccess()) {
                    SessionManager.save(LoginActivity.this, email, body);
                    
                    // Dispara a sincronização de Cache Local
                    CacheManager.getInstance(LoginActivity.this).syncAll(email);
                    
                    iniciarServicosEIrParaMain();
                } else {
                    resetUI("Falha na autenticação");
                }
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                resetUI("Erro de conexão");
            }
        });
    }

    private void iniciarServicosEIrParaMain() {
        Intent serviceIntent = new Intent(this, LocationService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(serviceIntent);
        else startService(serviceIntent);
        
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }

    private void resetUI(String msg) {
        btnEntrar.setEnabled(true);
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
