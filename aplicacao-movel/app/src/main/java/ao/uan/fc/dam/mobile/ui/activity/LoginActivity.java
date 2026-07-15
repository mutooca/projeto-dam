package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;
import ao.uan.fc.dam.mobile.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail;
    private EditText inputSenha;
    private Button btnEntrar;
    private ProgressBar progressBar;
    private UtilizadorRepository repository;
    private SessionManager sessionManager;
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        repository = new UtilizadorRepository(this);
        sessionManager = new SessionManager(this);

        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputSenha = findViewById(R.id.editTextTextPassword);
        btnEntrar = findViewById(R.id.buttonEntrar);
        progressBar = findViewById(R.id.progressBar);
        TextView linkRegistar = findViewById(R.id.textView2);

        btnEntrar.setOnClickListener(v -> autenticar());

        linkRegistar.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));
    }

    private void autenticar() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString();

        if (email.isEmpty()) {
            inputEmail.setError("Introduza o email");
            return;
        }
        if (senha.isEmpty()) {
            inputSenha.setError("Introduza a palavra-chave");
            return;
        }

        btnEntrar.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        // Uso de autenticação remota (Centralizada) com fallback local
        repository.autenticarRemoto(email, senha, utilizador -> runOnUiThread(() -> {
            progressBar.setVisibility(View.GONE);
            btnEntrar.setEnabled(true);

            if (utilizador == null) {
                Toast.makeText(
                        LoginActivity.this,
                        "Falha no login remoto. Verifique a ligação ao servidor e tente novamente.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            if (!sessionManager.hasKerberosSession()) {
                android.util.Log.e(TAG, "Login concluido sem sessao Kerberos."
                        + " email=" + utilizador.getEmail()
                        + " ticket=" + sessionManager.getTicket()
                        + " sessionId=" + sessionManager.getSessionId());
                sessionManager.terminarSessao();
                Toast.makeText(
                        LoginActivity.this,
                        "Sessão remota não foi criada. Faça login novamente com o servidor ligado.",
                        Toast.LENGTH_LONG
                ).show();
                return;
            }

            sessionManager.iniciarSessao(
                    utilizador.getIdUtilizador(),
                    utilizador.getNome(),
                    utilizador.getEmail()
            );

            Toast.makeText(LoginActivity.this, "Bem-vindo " + utilizador.getNome(), Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
            finish();
        }));
    }
}
