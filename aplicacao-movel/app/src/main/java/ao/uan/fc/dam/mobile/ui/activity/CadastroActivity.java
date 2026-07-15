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

import java.time.LocalDateTime;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.data.entity.Utilizador;
import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;

public class CadastroActivity extends AppCompatActivity {

    private EditText inputNome;
    private EditText inputEmail;
    private EditText inputSenha;
    private EditText inputConfirmarSenha;
    private Button criarConta;
    private ProgressBar progressBar;
    private UtilizadorRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        repository = new UtilizadorRepository(this);

        inputNome = findViewById(R.id.editTextText);
        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputSenha = findViewById(R.id.editTextTextPassword);
        inputConfirmarSenha = findViewById(R.id.editTextTextPassword2);
        criarConta = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar); // Verifique se existe no XML, senão adicione ou remova esta linha

        TextView linkEntrar = findViewById(R.id.textView2);

        criarConta.setOnClickListener(v -> criarConta());

        linkEntrar.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));
    }

    private void criarConta() {
        String nome = inputNome.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String senha = inputSenha.getText().toString();
        String confirmar = inputConfirmarSenha.getText().toString();

        if (nome.isEmpty()) {
            inputNome.setError("Introduza o nome");
            return;
        }
        if (email.isEmpty()) {
            inputEmail.setError("Introduza o email");
            return;
        }
        if (senha.length() < 6) {
            inputSenha.setError("A palavra-chave deve ter no mínimo 6 caracteres");
            return;
        }
        if (!senha.equals(confirmar)) {
            inputConfirmarSenha.setError("As palavras-chave são diferentes");
            return;
        }

        criarConta.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        Utilizador novo = new Utilizador();
        novo.setNome(nome);
        novo.setEmail(email);
        novo.setPalavraChave(senha);
        //novo.setSaldo(0);
        //novo.setDataCriacao(LocalDateTime.now());

        // Registo Remoto (Centralizado)
        repository.registarRemoto(novo, utilizador -> runOnUiThread(() -> {
            if (progressBar != null) progressBar.setVisibility(View.GONE);
            criarConta.setEnabled(true);

            if (utilizador != null) {
                Toast.makeText(CadastroActivity.this, "Conta criada com sucesso no servidor.", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                finish();
            } else {
                Toast.makeText(CadastroActivity.this, "Erro ao criar conta. Verifique a ligação ou se o email já existe.", Toast.LENGTH_LONG).show();
            }
        }));
    }
}
