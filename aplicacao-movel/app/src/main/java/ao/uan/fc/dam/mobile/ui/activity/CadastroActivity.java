package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {
    private Button criarConta;
    private EditText inputConfirmarPalavraChave;
    private EditText inputEmail;
    private EditText inputNome;
    private EditText inputPalavraChave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);
        this.inputNome = (EditText) findViewById(R.id.editTextText);
        this.inputEmail = (EditText) findViewById(R.id.editTextTextEmailAddress);
        this.inputPalavraChave = (EditText) findViewById(R.id.editTextTextPassword);
        this.inputConfirmarPalavraChave = (EditText) findViewById(R.id.editTextTextPassword2);
        this.criarConta = (Button) findViewById(R.id.button);

        findViewById(R.id.layoutLogin).setOnClickListener(view -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        this.criarConta.setOnClickListener(view -> {
            criarConta();
        });
    }

    private void criarConta() {
        String nome = this.inputNome.getText().toString().trim();
        final String email = this.inputEmail.getText().toString().trim();
        String palavraChave = this.inputPalavraChave.getText().toString();
        String confirmarPalavraChave = this.inputConfirmarPalavraChave.getText().toString();
        if (nome.isEmpty()) {
            this.inputNome.setError("Nome obrigatorio");
            this.inputNome.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            this.inputEmail.setError("Email obrigatorio");
            this.inputEmail.requestFocus();
            return;
        }
        if (palavraChave.length() < 6) {
            this.inputPalavraChave.setError("A palavra-chave deve ter no minimo 6 caracteres");
            this.inputPalavraChave.requestFocus();
            return;
        }
        if (!palavraChave.equals(confirmarPalavraChave)) {
            this.inputConfirmarPalavraChave.setError("As palavras-chave sao diferentes");
            this.inputConfirmarPalavraChave.requestFocus();
            return;
        }
        this.criarConta.setEnabled(false);
        Map<String, String> request = new HashMap<>();
        request.put("nome", nome);
        request.put("email", email);
        request.put("palavraChave", palavraChave);
        request.put("password", palavraChave);
        request.put("role", "USER");
        RetrofitClient.getInstance().getApi().registarUtilizador(request).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                CadastroActivity.this.criarConta.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(CadastroActivity.this, "Conta criada com sucesso. Entre para continuar.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
                    intent.putExtra("email", email);
                    CadastroActivity.this.startActivity(intent);
                    CadastroActivity.this.finish();
                    return;
                }
                String erro = CadastroActivity.this.lerErro(response, "Erro ao criar conta");
                Log.e("Cadastro", "HTTP " + response.code() + ": " + erro);
                Toast.makeText(CadastroActivity.this, erro, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                CadastroActivity.this.criarConta.setEnabled(true);
                Toast.makeText(CadastroActivity.this, "Falha na rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String lerErro(Response<?> response, String padrao) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (IOException e) {
            Log.e("Cadastro", "Erro ao ler resposta do servidor", e);
        }
        return padrao;
    }
}
