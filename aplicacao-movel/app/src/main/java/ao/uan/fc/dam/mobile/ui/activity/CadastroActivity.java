package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.util.ApiErrorUtils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {
    private EditText inputNome;
    private EditText inputEmail;
    private EditText inputSenha;
    private EditText inputConfirmarSenha;
    private Button criarConta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        inputNome = findViewById(R.id.editTextText);
        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputSenha = findViewById(R.id.editTextTextPassword);
        inputConfirmarSenha = findViewById(R.id.editTextTextPassword2);
        criarConta = findViewById(R.id.button);
        TextView linkEntrar = findViewById(R.id.textView2);

        criarConta.setOnClickListener(v -> criarConta());
        linkEntrar.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
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
            inputSenha.setError("A palavra-chave deve ter no minimo 6 caracteres");
            return;
        }
        if (!senha.equals(confirmar)) {
            inputConfirmarSenha.setError("As palavras-chave sao diferentes");
            return;
        }

        criarConta.setEnabled(false);
        Map<String, String> request = new HashMap<>();
        request.put("nome", nome);
        request.put("email", email);
        request.put("palavraChave", senha);
        request.put("role", "USER");
        request.put("preferenciaAnuncio", "GERAL");

        RetrofitClient.getInstance().getApi().registarUtilizador(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                criarConta.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Conta criada. Faça login para continuar.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(CadastroActivity.this, LoginActivity.class));
                    finish();
                    return;
                }
                Toast.makeText(CadastroActivity.this,
                        ApiErrorUtils.erroHttp(response, "Erro ao criar conta"),
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                criarConta.setEnabled(true);
                Toast.makeText(CadastroActivity.this, ApiErrorUtils.mensagemFalhaRede(t), Toast.LENGTH_LONG).show();
            }
        });
    }
}
