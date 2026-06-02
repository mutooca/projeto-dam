package ao.uan.fc.dam.mobile.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ao.uan.fc.dam.mobile.R;
import ao.uan.fc.dam.mobile.api.RetrofitClient;
import ao.uan.fc.dam.mobile.database.AppDatabase;
import ao.uan.fc.dam.mobile.model.Anuncio;
import ao.uan.fc.dam.mobile.model.Local;
import ao.uan.fc.dam.mobile.model.TicketResponse;
import ao.uan.fc.dam.mobile.model.Utilizador;
import ao.uan.fc.dam.mobile.security.KerberosAuthManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail;
    private EditText inputPalavraChave;
    private Button btnEntrar;
    private ProgressBar progressBar;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = findViewById(R.id.editTextTextEmailAddress);
        inputPalavraChave = findViewById(R.id.editTextTextPassword);
        btnEntrar = findViewById(R.id.buttonEntrar);
        
        // Adicionando um ProgressBar via código caso não esteja no XML, 
        // ou buscando se o user adicionou (idealmente estaria no XML)
        progressBar = findViewById(R.id.progressLogin); 

        findViewById(R.id.layoutRegistar).setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));

        btnEntrar.setOnClickListener(v -> autenticar());
    }

    private void autenticar() {
        String email = inputEmail.getText().toString().trim();
        String senha = inputPalavraChave.getText().toString();

        if (email.isEmpty()) {
            inputEmail.setError("Introduza o email");
            return;
        }

        if (senha.isEmpty()) {
            inputPalavraChave.setError("Introduza a palavra-chave");
            return;
        }

        btnEntrar.setEnabled(false);
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);

        Map<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("password", senha);
        request.put("palavraChave", senha);
        request.put("clientNonce", UUID.randomUUID().toString());

        RetrofitClient.getInstance().getApi().login(request).enqueue(new Callback<TicketResponse>() {
            @Override
            public void onResponse(Call<TicketResponse> call, Response<TicketResponse> response) {
                TicketResponse ticketResponse = response.body();
                if (response.isSuccessful() && ticketResponse != null && ticketResponse.isSuccess()) {
                    KerberosAuthManager.saveSession(LoginActivity.this, email, ticketResponse);
                    
                    // Iniciar Sincronização Inicial de Dados
                    sincronizarDadosIniciais(email);
                    
                    // Navegar para o Dashboard
                    startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
                    finish();
                    return;
                }

                btnEntrar.setEnabled(true);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                String erro = lerErro(response, "Erro ao efetuar login");
                Toast.makeText(LoginActivity.this, erro, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<TicketResponse> call, Throwable t) {
                btnEntrar.setEnabled(true);
                if (progressBar != null) progressBar.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Falha na rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Sincroniza dados do utilizador, locais e anúncios logo após o login
     * de forma assíncrona para povoar o cache Room.
     */
    private void sincronizarDadosIniciais(String email) {
        AppDatabase db = AppDatabase.getInstance(this);

        // 1. Obter Dados do Utilizador (Saldo/Perfil)
        RetrofitClient.getInstance().getApi().obterSaldo(email).enqueue(new Callback<Utilizador>() {
            @Override
            public void onResponse(Call<Utilizador> call, Response<Utilizador> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> db.utilizadorDao().salvar(response.body()));
                }
            }
            @Override
            public void onFailure(Call<Utilizador> call, Throwable t) {}
        });

        // 2. Obter Locais/Infraestruturas
        RetrofitClient.getInstance().getApi().listarInfraestruturas(0.0, 0.0, 50).enqueue(new Callback<List<Local>>() {
            @Override
            public void onResponse(Call<List<Local>> call, Response<List<Local>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    executor.execute(() -> {
                        db.localDao().limparTudo();
                        db.localDao().salvarTodos(response.body());
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Local>> call, Throwable t) {}
        });

        // 3. Obter Minhas Mensagens (Anúncios do user)
        RetrofitClient.getInstance().getApi().listarMinhasMensagens(email).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Anuncio> lista = response.body();
                    for (Anuncio a : lista) {
                        a.setUsuarioEmail(email);
                        a.setCategoria("MINHAS_MENSAGENS");
                    }
                    executor.execute(() -> {
                        db.anuncioDao().limparPorCategoria(email, "MINHAS_MENSAGENS");
                        db.anuncioDao().salvarTodos(lista);
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Anuncio>> call, Throwable t) {}
        });

        // 4. Obter Histórico (Inbox/Recebidos)
        RetrofitClient.getInstance().getApi().listarHistorico(email).enqueue(new Callback<List<Anuncio>>() {
            @Override
            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Anuncio> lista = response.body();
                    for (Anuncio a : lista) {
                        a.setUsuarioEmail(email);
                        a.setCategoria("HISTORICO");
                    }
                    executor.execute(() -> {
                        db.anuncioDao().limparPorCategoria(email, "HISTORICO");
                        db.anuncioDao().salvarTodos(lista);
                    });
                }
            }
            @Override
            public void onFailure(Call<List<Anuncio>> call, Throwable t) {}
        });
    }

    private String lerErro(Response<?> response, String padrao) {
        try {
            if (response.errorBody() != null) {
                return response.errorBody().string();
            }
        } catch (IOException e) {
            Log.e("Login", "Erro ao ler resposta do servidor", e);
        }
        return padrao;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
    }
}
