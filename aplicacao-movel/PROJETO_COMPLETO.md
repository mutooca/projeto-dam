# ANUNCIOSLOC_APP - DOCUMENTAÇÃO TÉCNICA E CÓDIGO FONTE

Este documento contém a estrutura organizada e o código integral do projeto para fins de backup e consulta rápida.

## 🌳 ESTRUTURA DE DIRETÓRIOS

```
AnunciosLoc_App/
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/ao/uan/fc/dam/mobile/
│   │   ├── AnunciosLoc_App.java
│   │   ├── contentProvider/
│   │   │   └── LocalizacaoProvider.java
│   │   ├── data/
│   │   │   ├── converter/ (EnumConverter, LocalDateTimeConverter)
│   │   │   ├── dao/ (LocalDao, AnuncioDao, HistoricoDao, UtilizadorDao, etc.)
│   │   │   ├── database/ (AppDatabase, DatabaseProvider)
│   │   │   ├── entity/ (Local, Anuncio, Historico, Utilizador, etc.)
│   │   │   ├── enums/ (ModoEntrega, Visibilidade, EstadoAnuncio, etc.)
│   │   │   ├── relation/ (LocalCompleto, AnuncioCompleto)
│   │   │   └── repository/ (LocalRepository, AnuncioRepository, etc.)
│   │   ├── network/ (P2P Handshake, UDP Server/Client, WiFi Direct)
│   │   ├── ui/
│   │   │   ├── activity/ (TelaInicial, Login, Cadastro, Dashboard)
│   │   │   ├── adapter/ (LocalAdapter, AnuncioAdapter, HistoricoAdapter)
│   │   │   ├── fragment/ (Inicio, Locais, Perfil, Postar, Detail)
│   │   │   └── viewholder/ (LocalViewHolder, AnuncioViewHolder)
│   │   └── util/ (SessionManager, DatabaseExecutor, NotificationHelper)
│   └── res/layout/ (Todos os ficheiros XML de interface)
```

---

## 📄 1. NÚCLEO DE REDE (DESCENTRALIZADO P2P)

### **Protocolo e Handshake (Protocol.java)**
```java
package ao.uan.fc.dam.mobile.network;
public class Protocol {
    public static final String HELLO = "HELLO";
    public static final String HELLO_ACK = "HELLO_ACK";
    public static final String PROFILE_REQUEST = "PROFILE_REQUEST";
    public static final String PROFILE_RESPONSE = "PROFILE_RESPONSE";
    public static final String ADVERTISEMENT = "ADVERTISEMENT";
    public static final String ACK = "ACK";
}
```

### **Publicador de Mensagens (MessagePublisher.java)**
```java
package ao.uan.fc.dam.mobile.network;
// Gerencia o envio de HELLO e Anúncios para Peers conhecidos
public class MessagePublisher {
    // ... lógica de envio UDP para a porta 8888
}
```

---

## 📄 2. PERSISTÊNCIA DE DADOS (ROOM)

### **Entidade Local (Local.java)**
```java
@Entity(tableName = "locais")
public class Local {
    @PrimaryKey(autoGenerate = true)
    private int idLocal;
    private String nome;
    private TipoCoordenada tipoCoordenada;
    // Getters e Setters
}
```

### **Base de Dados (AppDatabase.java)**
```java
@Database(entities = {...}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    // Acesso aos DAOs
}
```

---

## 📄 3. INTERFACE (XML LAYOUTS)

### **Dashboard (activity_dashboard.xml)**
```xml
<!-- Layout principal com BottomNavigationView e FragmentContainerView -->
```

### **Postar Anúncio (fragment_postar.xml)**
```xml
<!-- Formulário com Título, Mensagem, Spinner de Locais e Switch de Política -->
```

### **Detalhe do Anúncio (fragment_detail_anuncio.xml)**
```xml
<!-- Vista corrigida com NestedScrollView e Constraints ajustadas (0dp) -->
```

---

## 📄 4. LÓGICA DE NEGÓCIO (REPOSITORIES)

### **LocalRepository.java**
```java
// Executa operações de banco em threads separadas via DatabaseExecutor
```

---

## 📄 5. UTILITÁRIOS E SEGURANÇA

### **SessionManager.java**
```java
// Gestão de SharedPreferences para ID e Nome do Utilizador logado
```

### **NotificationHelper.java**
```java
// Dispara notificações de sistema ao receber anúncios via P2P
```



=======================================================================================

Volta ate a parte que estava funcional com o botao, antes de mandar o gemini fazer o codigo: "package ao.uan.fc.dam.mobile.ui.fragment;



import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;

import android.widget.EditText;

import android.widget.FrameLayout;

import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.TextView;

import android.widget.Toast;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import com.google.android.material.button.MaterialButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.android.material.textfield.TextInputEditText;



import ao.uan.fc.dam.mobile.R;

import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;

import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;

import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;

import ao.uan.fc.dam.mobile.data.repository.HistoricoRepository;

import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;

import ao.uan.fc.dam.mobile.ui.activity.LoginActivity;

import ao.uan.fc.dam.mobile.ui.adapter.HistoricoAdapter;

import ao.uan.fc.dam.mobile.util.SessionManager;



public class PerfilFragment extends Fragment {

    private TextView nomeUtilizador;

    private TextView emailUtilizador;

    private TextView saldo;

    private TextView anuncioPublicado;

    private TextView anuncioEntregue;

    private UtilizadorRepository utilizadorRepository;

    private FrameLayout btnLogout;

    private ImageView btnEditName;

    private MaterialButton btnAddProperty;

    private SessionManager sessionManager;

    private RecyclerView recyclerHistorico;

    private HistoricoAdapter adapter;

    private HistoricoRepository historicoRepository;

    private AnuncioRepository anuncioRepository;

    private LinearLayout layoutProperties;

    private AtributoPerfilRepository atributoPerfilRepository;



    public PerfilFragment() {

        super(R.layout.fragment_perfil);

    }



    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



        super.onViewCreated(view, savedInstanceState);



        nomeUtilizador = view.findViewById(R.id.textView23);

        emailUtilizador = view.findViewById(R.id.textView24);

        saldo = view.findViewById(R.id.textView25);

        anuncioPublicado = view.findViewById(R.id.textView26);

        anuncioEntregue = view.findViewById(R.id.textView28);

        btnLogout = view.findViewById(R.id.btnLogout);

        btnEditName = view.findViewById(R.id.btnEditName);

        btnAddProperty = view.findViewById(R.id.btnAddProperty);

        layoutProperties = view.findViewById(R.id.layoutProperties);   // só o findViewById aqui



        sessionManager = new SessionManager(requireContext());          // <- sessionManager nasce aqui

        anuncioRepository = new AnuncioRepository(requireContext());

        utilizadorRepository = new UtilizadorRepository(requireContext());

        atributoPerfilRepository = new AtributoPerfilRepository(requireContext());



        carregarPerfil();

        carregarAtributos();   // <- agora sim, sessionManager já existe



        recyclerHistorico = view.findViewById(R.id.recyclerViewPerfil);



        adapter = new HistoricoAdapter();

        recyclerHistorico.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerHistorico.setAdapter(adapter);

        historicoRepository = new HistoricoRepository(requireContext());



        historicoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(),

                lista -> requireActivity().runOnUiThread(() -> {

                    adapter.setHistorico(lista);

                })

        );



        btnLogout.setOnClickListener(v -> {

            sessionManager.terminarSessao();

            Intent intent = new Intent(requireContext(), LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            requireActivity().finish();

        });



        btnEditName.setOnClickListener(v -> abrirDialogEditarPerfil());

        btnAddProperty.setOnClickListener(v -> abrirDialogNovoAtributo());   // <- listener fica aqui, separado do findViewById

    }

    private void carregarPerfil(){

        nomeUtilizador.setText(sessionManager.getNome());

        emailUtilizador.setText(sessionManager.getEmail());

        utilizadorRepository.buscarPorId(

                sessionManager.getIdUtilizador(),

                utilizador -> requireActivity().runOnUiThread(() -> {



                    if (utilizador == null) return;



                    nomeUtilizador.setText(utilizador.getNome());

                    emailUtilizador.setText(utilizador.getEmail());

                    saldo.setText(String.valueOf(utilizador.getSaldo()));

                })

        );

    }



    private void abrirDialogEditarPerfil() {

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil, null);



        EditText nome = dialogView.findViewById(R.id.editNome);

        EditText email = dialogView.findViewById(R.id.editEmail);

        EditText senha = dialogView.findViewById(R.id.editSenha);



        nome.setText(sessionManager.getNome());

        email.setText(sessionManager.getEmail());



        new MaterialAlertDialogBuilder(requireContext())

                .setTitle("Editar Perfil")

                .setView(dialogView)

                .setPositiveButton("Guardar", (dialog, which) -> {

                    guardarPerfil(nome.getText().toString().trim(), email.getText().toString().trim(), senha.getText().toString());

                }).setNegativeButton("Cancelar", null).show();

    }



    private void guardarPerfil(String nome, String email, String senha){

        utilizadorRepository.buscarPorId(sessionManager.getIdUtilizador(), utilizador -> {

                    if(utilizador == null)

                        return;

                    utilizador.setNome(nome);

                    utilizador.setEmail(email);



                    if(!senha.isBlank()) utilizador.setPalavraChave(senha);

                    utilizadorRepository.atualizar(utilizador, resultado -> requireActivity().runOnUiThread(() ->{

                                sessionManager.iniciarSessao(utilizador.getIdUtilizador(), utilizador.getNome(),

                                        utilizador.getEmail());

                                carregarPerfil();



                                Toast.makeText(requireContext(), "Perfil atualizado.", Toast.LENGTH_SHORT).show();

                            })

                    );

                }

        );

    }



    private void carregarAtributos() {

        atributoPerfilRepository.listarPorUtilizador(

                sessionManager.getIdUtilizador(),

                lista -> requireActivity().runOnUiThread(() -> {

                    layoutProperties.removeAllViews();



                    for (AtributoPerfil atributo : lista) {

                        View itemView = LayoutInflater.from(requireContext())

                                .inflate(R.layout.item_atributo_perfil, layoutProperties, false);



                        TextView txtChave = itemView.findViewById(R.id.txtChave);

                        TextView txtValor = itemView.findViewById(R.id.txtValor);

                        ImageView btnRemover = itemView.findViewById(R.id.btnRemoverAtributo);



                        txtChave.setText(atributo.getChave());

                        txtValor.setText(atributo.getValor());



                        btnRemover.setOnClickListener(v ->

                                atributoPerfilRepository.remover(atributo, resultado ->

                                        requireActivity().runOnUiThread(this::carregarAtributos))

                        );



                        layoutProperties.addView(itemView);

                    }

                })

        );

    }



    private void abrirDialogNovoAtributo() {

        View dialogView = LayoutInflater.from(requireContext())

                .inflate(R.layout.dialog_add_property, null);



        TextInputEditText editKey = dialogView.findViewById(R.id.editKey);

        TextInputEditText editValue = dialogView.findViewById(R.id.editValue);



        new MaterialAlertDialogBuilder(requireContext())

                .setTitle("Novo Atributo")

                .setView(dialogView)

                .setPositiveButton("Guardar", (dialog, which) -> {

                    String chave = editKey.getText().toString().trim();

                    String valor = editValue.getText().toString().trim();



                    if (chave.isEmpty() || valor.isEmpty()) {

                        Toast.makeText(requireContext(), "Preencha ambos os campos", Toast.LENGTH_SHORT).show();

                        return;

                    }



                    AtributoPerfil atributo = new AtributoPerfil(chave, valor, sessionManager.getIdUtilizador());



                    atributoPerfilRepository.inserir(atributo, id ->

                            requireActivity().runOnUiThread(this::carregarAtributos)

                    );

                })

                .setNegativeButton("Cancelar", null)

                .show();

    }

}", "package ao.uan.fc.dam.mobile.ui.fragment;



import android.content.Intent;

import android.os.Bundle;

import android.view.LayoutInflater;

import android.view.View;

import android.widget.EditText;

import android.widget.FrameLayout;

import android.widget.ImageView;

import android.widget.LinearLayout;

import android.widget.TextView;

import android.widget.Toast;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import com.google.android.material.button.MaterialButton;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.google.android.material.textfield.TextInputEditText;



import ao.uan.fc.dam.mobile.R;

import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;

import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;

import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;

import ao.uan.fc.dam.mobile.data.repository.HistoricoRepository;

import ao.uan.fc.dam.mobile.data.repository.UtilizadorRepository;

import ao.uan.fc.dam.mobile.ui.activity.LoginActivity;

import ao.uan.fc.dam.mobile.ui.adapter.HistoricoAdapter;

import ao.uan.fc.dam.mobile.util.SessionManager;



public class PerfilFragment extends Fragment {

    private TextView nomeUtilizador;

    private TextView emailUtilizador;

    private TextView saldo;

    private TextView anuncioPublicado;

    private TextView anuncioEntregue;

    private UtilizadorRepository utilizadorRepository;

    private FrameLayout btnLogout;

    private ImageView btnEditName;

    private MaterialButton btnAddProperty;

    private SessionManager sessionManager;

    private RecyclerView recyclerHistorico;

    private HistoricoAdapter adapter;

    private HistoricoRepository historicoRepository;

    private AnuncioRepository anuncioRepository;

    private LinearLayout layoutProperties;

    private AtributoPerfilRepository atributoPerfilRepository;



    public PerfilFragment() {

        super(R.layout.fragment_perfil);

    }



    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {



        super.onViewCreated(view, savedInstanceState);



        nomeUtilizador = view.findViewById(R.id.textView23);

        emailUtilizador = view.findViewById(R.id.textView24);

        saldo = view.findViewById(R.id.textView25);

        anuncioPublicado = view.findViewById(R.id.textView26);

        anuncioEntregue = view.findViewById(R.id.textView28);

        btnLogout = view.findViewById(R.id.btnLogout);

        btnEditName = view.findViewById(R.id.btnEditName);

        btnAddProperty = view.findViewById(R.id.btnAddProperty);

        layoutProperties = view.findViewById(R.id.layoutProperties);   // só o findViewById aqui



        sessionManager = new SessionManager(requireContext());          // <- sessionManager nasce aqui

        anuncioRepository = new AnuncioRepository(requireContext());

        utilizadorRepository = new UtilizadorRepository(requireContext());

        atributoPerfilRepository = new AtributoPerfilRepository(requireContext());



        carregarPerfil();

        carregarAtributos();   // <- agora sim, sessionManager já existe



        recyclerHistorico = view.findViewById(R.id.recyclerViewPerfil);



        adapter = new HistoricoAdapter();

        recyclerHistorico.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerHistorico.setAdapter(adapter);

        historicoRepository = new HistoricoRepository(requireContext());



        historicoRepository.listarPorUtilizador(sessionManager.getIdUtilizador(),

                lista -> requireActivity().runOnUiThread(() -> {

                    adapter.setHistorico(lista);

                })

        );



        btnLogout.setOnClickListener(v -> {

            sessionManager.terminarSessao();

            Intent intent = new Intent(requireContext(), LoginActivity.class);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);

            requireActivity().finish();

        });



        btnEditName.setOnClickListener(v -> abrirDialogEditarPerfil());

        btnAddProperty.setOnClickListener(v -> abrirDialogNovoAtributo());   // <- listener fica aqui, separado do findViewById

    }

    private void carregarPerfil(){

        nomeUtilizador.setText(sessionManager.getNome());

        emailUtilizador.setText(sessionManager.getEmail());

        utilizadorRepository.buscarPorId(

                sessionManager.getIdUtilizador(),

                utilizador -> requireActivity().runOnUiThread(() -> {



                    if (utilizador == null) return;



                    nomeUtilizador.setText(utilizador.getNome());

                    emailUtilizador.setText(utilizador.getEmail());

                    saldo.setText(String.valueOf(utilizador.getSaldo()));

                })

        );

    }



    private void abrirDialogEditarPerfil() {

        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_editar_perfil, null);



        EditText nome = dialogView.findViewById(R.id.editNome);

        EditText email = dialogView.findViewById(R.id.editEmail);

        EditText senha = dialogView.findViewById(R.id.editSenha);



        nome.setText(sessionManager.getNome());

        email.setText(sessionManager.getEmail());



        new MaterialAlertDialogBuilder(requireContext())

                .setTitle("Editar Perfil")

                .setView(dialogView)

                .setPositiveButton("Guardar", (dialog, which) -> {

                    guardarPerfil(nome.getText().toString().trim(), email.getText().toString().trim(), senha.getText().toString());

                }).setNegativeButton("Cancelar", null).show();

    }



    private void guardarPerfil(String nome, String email, String senha){

        utilizadorRepository.buscarPorId(sessionManager.getIdUtilizador(), utilizador -> {

                    if(utilizador == null)

                        return;

                    utilizador.setNome(nome);

                    utilizador.setEmail(email);



                    if(!senha.isBlank()) utilizador.setPalavraChave(senha);

                    utilizadorRepository.atualizar(utilizador, resultado -> requireActivity().runOnUiThread(() ->{

                                sessionManager.iniciarSessao(utilizador.getIdUtilizador(), utilizador.getNome(),

                                        utilizador.getEmail());

                                carregarPerfil();



                                Toast.makeText(requireContext(), "Perfil atualizado.", Toast.LENGTH_SHORT).show();

                            })

                    );

                }

        );

    }



    private void carregarAtributos() {

        atributoPerfilRepository.listarPorUtilizador(

                sessionManager.getIdUtilizador(),

                lista -> requireActivity().runOnUiThread(() -> {

                    layoutProperties.removeAllViews();



                    for (AtributoPerfil atributo : lista) {

                        View itemView = LayoutInflater.from(requireContext())

                                .inflate(R.layout.item_atributo_perfil, layoutProperties, false);



                        TextView txtChave = itemView.findViewById(R.id.txtChave);

                        TextView txtValor = itemView.findViewById(R.id.txtValor);

                        ImageView btnRemover = itemView.findViewById(R.id.btnRemoverAtributo);



                        txtChave.setText(atributo.getChave());

                        txtValor.setText(atributo.getValor());



                        btnRemover.setOnClickListener(v ->

                                atributoPerfilRepository.remover(atributo, resultado ->

                                        requireActivity().runOnUiThread(this::carregarAtributos))

                        );



                        layoutProperties.addView(itemView);

                    }

                })

        );

    }



    private void abrirDialogNovoAtributo() {

        View dialogView = LayoutInflater.from(requireContext())

                .inflate(R.layout.dialog_add_property, null);



        TextInputEditText editKey = dialogView.findViewById(R.id.editKey);

        TextInputEditText editValue = dialogView.findViewById(R.id.editValue);



        new MaterialAlertDialogBuilder(requireContext())

                .setTitle("Novo Atributo")

                .setView(dialogView)

                .setPositiveButton("Guardar", (dialog, which) -> {

                    String chave = editKey.getText().toString().trim();

                    String valor = editValue.getText().toString().trim();



                    if (chave.isEmpty() || valor.isEmpty()) {

                        Toast.makeText(requireContext(), "Preencha ambos os campos", Toast.LENGTH_SHORT).show();

                        return;

                    }



                    AtributoPerfil atributo = new AtributoPerfil(chave, valor, sessionManager.getIdUtilizador());



                    atributoPerfilRepository.inserir(atributo, id ->

                            requireActivity().runOnUiThread(this::carregarAtributos)

                    );

                })

                .setNegativeButton("Cancelar", null)

                .show();

    }

}", "package ao.uan.fc.dam.mobile.ui.viewholder;



import android.view.View;

import android.widget.ImageView;

import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import ao.uan.fc.dam.mobile.R;

import ao.uan.fc.dam.mobile.data.enums.ModoEntrega;

import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;

import ao.uan.fc.dam.mobile.ui.model.AnuncioModel;



public class AnuncioViewHolder extends RecyclerView.ViewHolder {

    private final TextView titulo;

    private final TextView autorLocal;

    private final TextView tempo;

    private final ImageView tipoEntrega;



    public AnuncioViewHolder(View itemView){

        super(itemView);



        titulo = itemView.findViewById(R.id.textView42);

        autorLocal = itemView.findViewById(R.id.txtAutorELocal);

        tempo = itemView.findViewById(R.id.textView44);

        tipoEntrega = itemView.findViewById(R.id.imgTipoEntrega);



    }



    public void bind(AnuncioModel anuncio)

    {

        titulo.setText(anuncio.getTitulo());

        autorLocal.setText(

                anuncio.getAutor()

                        + " • "

                        + anuncio.getLocal()

        );

        tempo.setText("Agora");

        if (anuncio.getModoEntrega()

                .equals("DESCENTRALIZADO")) {

            tipoEntrega.setImageResource(

                    R.drawable.wifi_icon

            );

        } else {

            tipoEntrega.setImageResource(

                    R.drawable.location_on_icon

            );

        }

    }

}", "package ao.uan.fc.dam.mobile.ui.fragment;



import android.os.Bundle;

import android.view.View;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;



import com.google.android.material.floatingactionbutton.FloatingActionButton;



import ao.uan.fc.dam.mobile.R;

import ao.uan.fc.dam.mobile.data.entity.Anuncio;

import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;





public class VerAnuncioFragment extends Fragment {

    private TextView txtTituloDetalhe;

    private TextView txtMensagemDetalhe;

    private TextView txtLocalDetalhe;

    private TextView txtAutorNome;

    private FloatingActionButton btnDelete;

    private FloatingActionButton btnClose;

    private AnuncioRepository repository;

    private int idAnuncio;

    private Anuncio anuncioAtual;





    public VerAnuncioFragment(){

        super(R.layout.fragment_ver_anuncio);

    }



    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);



        txtTituloDetalhe = view.findViewById(R.id.txtTituloDetalhe);

        txtMensagemDetalhe = view.findViewById(R.id.txtMensagemDetalhe);

        txtLocalDetalhe = view.findViewById(R.id.txtLocalDetalhe);

        txtAutorNome = view.findViewById(R.id.txtAutorNome);

        btnDelete = view.findViewById(R.id.btnDelete);

        btnClose = view.findViewById(R.id.btnClose);

        repository = new AnuncioRepository(requireContext());



        Bundle bundle = getArguments();



        if(bundle != null){

            idAnuncio = bundle.getInt("id_anuncio");

            carregarAnuncio();

        }



        btnClose.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnDelete.setOnClickListener(v -> removerAnuncio());

    }



    private void carregarAnuncio(){

        repository.buscarPorId(idAnuncio, anuncioCompleto -> {



            if(anuncioCompleto == null)

                return;

            anuncioAtual = anuncioCompleto.getAnuncio();



            requireActivity().runOnUiThread(() -> {

                txtTituloDetalhe.setText(anuncioCompleto.getAnuncio().getTitulo());

                txtMensagemDetalhe.setText(anuncioCompleto.getAnuncio().getConteudo());



                if(anuncioCompleto.getLocal() != null){

                    txtLocalDetalhe.setText(anuncioCompleto.getLocal().getNome());

                }



                if(anuncioCompleto.getAutor() != null){

                    txtAutorNome.setText(anuncioCompleto.getAutor().getNome());

                }

            });

        });

    }



    private void removerAnuncio(){

        if(anuncioAtual == null)

            return;



        repository.remover(anuncioAtual, resultado -> {

                    requireActivity().runOnUiThread(() -> {

                        requireActivity().getSupportFragmentManager().popBackStack();

                    });

        });

    }

}", "package ao.uan.fc.dam.mobile.data.repository;



import android.content.Context;

import android.util.Log;

import androidx.lifecycle.LiveData;

import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.AnuncioDao;

import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;

import ao.uan.fc.dam.mobile.data.entity.Anuncio;

import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;

import ao.uan.fc.dam.mobile.network.api.RetrofitClient;

import ao.uan.fc.dam.mobile.util.DatabaseExecutor;

import ao.uan.fc.dam.mobile.util.ResultadoCallback;

import retrofit2.Call;

import retrofit2.Callback;

import retrofit2.Response;



public class AnuncioRepository {

    private final AnuncioDao dao;

    private static final String TAG = "AnuncioRepository";



    public AnuncioRepository(Context context){

        dao = DatabaseProvider.getInstance(context).anuncioDao();

    }



    public void inserir(Anuncio anuncio, ResultadoCallback<Long> callback){

        // Primeiro insere localmente

        DatabaseExecutor.executor.execute(() ->{

            long id = dao.inserir(anuncio);

            anuncio.setIdAnuncio((int) id);

            

            // Se for modo CENTRALIZADO, envia para o servidor

            if (anuncio.getModoEntrega() != null && anuncio.getModoEntrega().name().equals("CENTRALIZADO")) {

                publicarNoServidor(anuncio);

            }



            if(callback != null){

                callback.onResultado(id);

            }

        });

    }



    private void publicarNoServidor(Anuncio anuncio) {

        RetrofitClient.getApiService().publicarAnuncio(anuncio).enqueue(new Callback<Anuncio>() {

            @Override

            public void onResponse(Call<Anuncio> call, Response<Anuncio> response) {

                if (response.isSuccessful()) {

                    Log.d(TAG, "Anúncio publicado no servidor com sucesso");

                } else {

                    Log.e(TAG, "Erro ao publicar no servidor: " + response.code());

                }

            }



            @Override

            public void onFailure(Call<Anuncio> call, Throwable t) {

                Log.e(TAG, "Falha na rede ao publicar anúncio", t);

            }

        });

    }



    public void sincronizarAnunciosRemotos(ResultadoCallback<Void> callback) {

        RetrofitClient.getApiService().listarAnuncios().enqueue(new Callback<List<Anuncio>>() {

            @Override

            public void onResponse(Call<List<Anuncio>> call, Response<List<Anuncio>> response) {

                if (response.isSuccessful() && response.body() != null) {

                    DatabaseExecutor.executor.execute(() -> {

                        for (Anuncio anuncio : response.body()) {

                            dao.inserir(anuncio); // Room ignora se conflito se configurado ou atualiza

                        }

                        if (callback != null) callback.onResultado(null);

                    });

                }

            }



            @Override

            public void onFailure(Call<List<Anuncio>> call, Throwable t) {

                Log.e(TAG, "Erro ao sincronizar anúncios", t);

                if (callback != null) callback.onResultado(null);

            }

        });

    }



    public void remover(Anuncio anuncio, ResultadoCallback<Void> callback){

        DatabaseExecutor.executor.execute(() -> {

            dao.remover(anuncio);

            if(callback != null){

                callback.onResultado(null);

            }

        });

    }



    public LiveData<List<AnuncioCompleto>> listarTodosLiveData() {

        return dao.listarTodosComRelacionamentosLiveData();

    }



    public void listarTodos(ResultadoCallback<List<AnuncioCompleto>> callback){

        DatabaseExecutor.executor.execute(() -> {

            List<AnuncioCompleto> lista = dao.listarTodosComRelacionamentos();

            if (callback != null) {

                callback.onResultado(lista);

            }

        });

    }



    public void buscarPorId(int id, ResultadoCallback<AnuncioCompleto> callback){

        DatabaseExecutor.executor.execute(() -> {

            AnuncioCompleto anuncio = dao.buscarCompleto(id);

            if (callback != null) {

                callback.onResultado(anuncio);

            }

        });

    }



    public void listarPorLocal(int idLocal, ResultadoCallback<List<AnuncioCompleto>> callback){

        DatabaseExecutor.executor.execute(() -> {

            List<AnuncioCompleto> lista = dao.listarPorLocal(idLocal);

            if (callback != null) {

                callback.onResultado(lista);

            }

        });

    }



    public void atualizar(Anuncio anuncio, ResultadoCallback<Void> callback){

        DatabaseExecutor.executor.execute(() -> {

            dao.atualizar(anuncio);

            if(callback != null){

                callback.onResultado(null);

            }

        });

    }



    public void contarPorUtilizador(int idUtilizador, ResultadoCallback<Integer> callback){

        DatabaseExecutor.executor.execute(() -> {

            int total = dao.contarPorUtilizador(idUtilizador);

            if (callback != null) {

                callback.onResultado(total);

            }

        });

    }



    public void listarPorUtilizador(int idUtilizador, ResultadoCallback<List<Anuncio>> callback){

        DatabaseExecutor.executor.execute(() -> {

            List<Anuncio> lista = dao.listarPorUtilizador(idUtilizador);

            if (callback != null) {

                callback.onResultado(lista);

            }

        });

    }

}

", "package ao.uan.fc.dam.mobile.data.repository;



import android.content.Context;

import androidx.lifecycle.LiveData;

import java.util.List;

import ao.uan.fc.dam.mobile.data.dao.AnuncioRecebidoDao;

import ao.uan.fc.dam.mobile.data.database.DatabaseProvider;

import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;

import ao.uan.fc.dam.mobile.util.DatabaseExecutor;

import ao.uan.fc.dam.mobile.util.ResultadoCallback;



public class AnuncioRecebidoRepository {



    private final AnuncioRecebidoDao dao;



    public AnuncioRecebidoRepository(Context context) {

        dao = DatabaseProvider

                .getInstance(context)

                .anuncioRecebidoDao();

    }



    public void inserir(AnuncioRecebido anuncio,

                        ResultadoCallback<Long> callback) {

        DatabaseExecutor.executor.execute(() -> {

            long id = dao.inserir(anuncio);

            if (callback != null)

                callback.onResultado(id);

        });

    }


    public void receberAnuncioDescentralizado(AnuncioRecebido anuncio,

                                            ResultadoCallback<Long> callback) {

        DatabaseExecutor.executor.execute(() -> {

            AnuncioRecebido existente = dao.buscarPorMsgId(anuncio.getMsgId());

            if (existente == null) {

                long id = dao.inserir(anuncio);

                if (callback != null) callback.onResultado(id);

            } else {

                if (callback != null) callback.onResultado(-1L);

            }

        });

    }



    public LiveData<List<AnuncioRecebido>> listarTodosLiveData() {

        return dao.listarTodosLiveData();

    }



    public void listarTodos(ResultadoCallback<List<AnuncioRecebido>> callback) {

        DatabaseExecutor.executor.execute(() -> {

            List<AnuncioRecebido> lista = dao.listarTodos();

            if (callback != null) {

                callback.onResultado(lista);

            }

        });

    }



    public void buscarPorMsgId(

            String msgId,

            ResultadoCallback<AnuncioRecebido> callback) {

        DatabaseExecutor.executor.execute(() -> {

            AnuncioRecebido anuncio = dao.buscarPorMsgId(msgId);

            if (callback != null) {

                callback.onResultado(anuncio);

            }

        });

    }

}

", "package ao.uan.fc.dam.mobile.ui.fragment;





import android.os.Bundle;

import android.view.View;

import android.widget.EditText;

import android.widget.TextView;



import androidx.annotation.NonNull;

import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import androidx.lifecycle.MediatorLiveData;

import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import java.util.List;



import ao.uan.fc.dam.mobile.R;

import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;

import ao.uan.fc.dam.mobile.data.relation.AnuncioCompleto;

import ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository;

import ao.uan.fc.dam.mobile.data.repository.AnuncioRepository;

import ao.uan.fc.dam.mobile.data.repository.LocalRepository;

import ao.uan.fc.dam.mobile.ui.adapter.AnuncioAdapter;

import ao.uan.fc.dam.mobile.ui.model.AnuncioModel;





public class InicioFragment extends Fragment {

    private TextView bemVindoUser;

    private TextView numeroNotificacao;

    private EditText pesquisar;

    private TextView numeroAnuncio;

    private TextView numeroLocais;

    private AnuncioAdapter adapter;

    private AnuncioRepository anuncioRepository;

    private LocalRepository localRepository;

    private RecyclerView recyclerViewInicio;

    private AnuncioRecebidoRepository anuncioRecebidoRepository;



    private final MediatorLiveData<List<AnuncioModel>> anunciosCombined = new MediatorLiveData<>();





    public InicioFragment(){

        super(R.layout.fragment_inicio);

    }



    @Override

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){

        super.onViewCreated(view, savedInstanceState);

        bemVindoUser = view.findViewById(R.id.textView7);

        numeroNotificacao = view.findViewById(R.id.txtBadgeNotif);

        pesquisar = view.findViewById(R.id.searchView);

        numeroAnuncio = view.findViewById(R.id.textView29);

        numeroLocais = view.findViewById(R.id.textView31);

        recyclerViewInicio = view.findViewById(R.id.recyclerViewInicio);



        adapter = new AnuncioAdapter();



        recyclerViewInicio.setLayoutManager(new LinearLayoutManager(requireContext()));

        recyclerViewInicio.setAdapter(adapter);

        anuncioRepository = new AnuncioRepository(requireContext());

        localRepository = new LocalRepository(requireContext());

        anuncioRecebidoRepository = new AnuncioRecebidoRepository(requireContext());

        

        setupObservers();

        carregarNumeroLocais();



        adapter.setOnAnuncioClickListener(new AnuncioAdapter.OnAnuncioClickListener() {

            @Override

            public void onClick(AnuncioModel anuncio) {

                Fragment fragment;

                Bundle bundle = new Bundle();



                if ("DESCENTRALIZADO".equals(anuncio.getModoEntrega())) {

                    bundle.putString("msg_id", anuncio.getMsgId());

                    fragment = new DetailAnuncioFragment();

                } else {

                    bundle.putInt("id_anuncio", anuncio.getId());

                    fragment = new VerAnuncioFragment();

                }



                fragment.setArguments(bundle);

                requireActivity()

                        .getSupportFragmentManager()

                        .beginTransaction()

                        .replace(R.id.frameContainer, fragment)

                        .addToBackStack(null)

                        .commit();

            }



            @Override

            public void onLongClick(AnuncioModel anuncio) {

            }

        });

    }



    private void setupObservers() {

        var liveDataAnuncios = anuncioRepository.listarTodosLiveData();

        var liveDataRecebidos = anuncioRecebidoRepository.listarTodosLiveData();



        anunciosCombined.addSource(liveDataAnuncios, lista -> updateCombinedList(lista, liveDataRecebidos.getValue()));

        anunciosCombined.addSource(liveDataRecebidos, recebidos -> updateCombinedList(liveDataAnuncios.getValue(), recebidos));



        anunciosCombined.observe(getViewLifecycleOwner(), modelos -> {

            adapter.setAnuncios(modelos);

            numeroAnuncio.setText(String.valueOf(modelos.size()));

        });

    }



    private void updateCombinedList(List<AnuncioCompleto> lista, List<AnuncioRecebido> recebidos) {

        List<AnuncioModel> modelos = new ArrayList<>();



        if (lista != null) {

            for (var item : lista) {

                modelos.add(new AnuncioModel(

                        item.anuncio.getIdAnuncio(),

                        item.anuncio.getTitulo(),

                        item.autor != null ? item.autor.getNome() : "",

                        item.local != null ? item.local.getNome() : "",

                        item.anuncio.getConteudo(),

                        item.anuncio.getModoEntrega().name()

                ));

            }

        }



        if (recebidos != null) {

            for (AnuncioRecebido item : recebidos) {

                modelos.add(new AnuncioModel(

                        0,

                        item.getMsgId(),

                        item.getTitulo(),

                        item.getAutor(),

                        item.getLocal(),

                        item.getConteudo(),

                        "DESCENTRALIZADO"

                ));

            }

        }



        anunciosCombined.setValue(modelos);

    }



    @Override

    public void onResume() {

        super.onResume();

    }



    private void carregarNumeroLocais(){

        localRepository.listarTodosComCoordenadas(lista ->{

            requireActivity().runOnUiThread(() ->{

                numeroLocais.setText(

                        String.valueOf(lista.size())

                );

            });

        });

    }

}

",
 "package ao.uan.fc.dam.mobile.service;



import android.content.Context;



import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;

import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;





public class perfilService {

    private final AtributoPerfilRepository repository;



    public perfilService(Context context){

        repository = new AtributoPerfilRepository(context);



    }



    public void obterPerfil(int idUtilizador, Callback callback){

        repository.listarPorUtilizador(idUtilizador, atributos -> {

                    StringBuilder perfil = new StringBuilder();



                    for(AtributoPerfil atributo : atributos){

                        perfil.append(atributo.getChave()).append("=").append(atributo.getValor()).append(";");

                    }

                    callback.resposta(perfil.toString());

                }

        );

    }



    public interface Callback{

        void resposta(String perfil);

    }

}

", "package ao.uan.fc.dam.mobile.network;



import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

import java.util.Map;



public class JsonConverter {



    private static final Gson gson = new Gson();



    public static String toJson(Object object){

        return gson.toJson(object);

    }



    public static <T> T fromJson(String json, Class<T> classe){

        return gson.fromJson(json, classe);

    }



    public static Map<String, String> toMap(String json) {

        Type tipo = new TypeToken<Map<String, String>>() {}.getType();

        return gson.fromJson(json, tipo);

    }



}

", "package ao.uan.fc.dam.mobile.network;



import android.location.Location;

import android.util.Log;



import ao.uan.fc.dam.mobile.data.entity.CoordenadaGps;

import ao.uan.fc.dam.mobile.data.entity.CoordenadaWifi;

import ao.uan.fc.dam.mobile.data.relation.LocalCompleto;



public class LocationMatcher {



    private static final String TAG = "LocationMatcher";



    public static boolean estaNoLocal(LocalCompleto localTarget, double currentLat, double currentLon, String currentSsid) {

        if (localTarget == null) return false;



        // 1. Verificar WiFi se disponível no local

        CoordenadaWifi wifi = localTarget.getCoordenadaWifi();

        if (wifi != null && wifi.getSsid() != null && !wifi.getSsid().isEmpty()) {

            if (currentSsid != null && currentSsid.equalsIgnoreCase(wifi.getSsid())) {

                Log.d(TAG, "Localização validada via WiFi: " + currentSsid);

                return true;

            }

        }



        // 2. Verificar GPS se disponível no local

        CoordenadaGps gps = localTarget.getCoordenadaGps();

        if (gps != null && !Double.isNaN(currentLat) && !Double.isNaN(currentLon)) {

            float[] results = new float[1];

            Location.distanceBetween(currentLat, currentLon, gps.getLatitude(), gps.getLongitude(), results);

            float distancia = results[0];



            if (distancia <= gps.getRaio()) {

                Log.d(TAG, "Localização validada via GPS. Distância: " + distancia + "m, Raio: " + gps.getRaio() + "m");

                return true;

            } else {

                Log.d(TAG, "Fora do raio GPS. Distância: " + distancia + "m");

            }

        }



        return false;

    }

}

", "package ao.uan.fc.dam.mobile.network;



public class Message {



    private String type;

    private String sender;

    private String payload;



    // Identificação da mensagem

    private String msgId;

    private long timestamp;



    // Dados do anúncio

    private String titulo;

    private String conteudo;

    private String autor;

    private String local;

    private String politicaTipo;

    private String politicaChaves;



    // Janela temporal

    private String dataInicio;

    private String dataFim;



    public Message() {

    }



    public Message(String type, String sender, String payload) {

        this.type = type;

        this.sender = sender;

        this.payload = payload;

    }



    public String getType() {

        return type;

    }



    public void setType(String type) {

        this.type = type;

    }



    public String getSender() {

        return sender;

    }



    public void setSender(String sender) {

        this.sender = sender;

    }



    public String getPayload() {

        return payload;

    }



    public void setPayload(String payload) {

        this.payload = payload;

    }



    public String getMsgId() {

        return msgId;

    }



    public void setMsgId(String msgId) {

        this.msgId = msgId;

    }



    public long getTimestamp() {

        return timestamp;

    }



    public void setTimestamp(long timestamp) {

        this.timestamp = timestamp;

    }



    public String getTitulo() {

        return titulo;

    }



    public void setTitulo(String titulo) {

        this.titulo = titulo;

    }



    public String getConteudo() {

        return conteudo;

    }



    public void setConteudo(String conteudo) {

        this.conteudo = conteudo;

    }



    public String getAutor() {

        return autor;

    }



    public void setAutor(String autor) {

        this.autor = autor;

    }



    public String getLocal() {

        return local;

    }



    public void setLocal(String local) {

        this.local = local;

    }



    public String getPoliticaTipo() {

        return politicaTipo;

    }



    public void setPoliticaTipo(String politicaTipo) {

        this.politicaTipo = politicaTipo;

    }



    public String getPoliticaChaves() {

        return politicaChaves;

    }



    public void setPoliticaChaves(String politicaChaves) {

        this.politicaChaves = politicaChaves;

    }



    public String getDataInicio() {

        return dataInicio;

    }



    public void setDataInicio(String dataInicio) {

        this.dataInicio = dataInicio;

    }



    public String getDataFim() {

        return dataFim;

    }



    public void setDataFim(String dataFim) {

        this.dataFim = dataFim;

    }

}", "package ao.uan.fc.dam.mobile.network;



import android.content.Context;

import android.util.Log;



import java.net.InetAddress;

import java.time.LocalDateTime;

import java.util.HashMap;

import java.util.Map;



import ao.uan.fc.dam.mobile.contentProvider.LocalizacaoProvider;

import ao.uan.fc.dam.mobile.data.entity.AnuncioRecebido;

import ao.uan.fc.dam.mobile.data.entity.AtributoPerfil;

import ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository;

import ao.uan.fc.dam.mobile.data.repository.AtributoPerfilRepository;

import ao.uan.fc.dam.mobile.data.repository.LocalRepository;

import ao.uan.fc.dam.mobile.util.NotificationHelper;

import ao.uan.fc.dam.mobile.util.SessionManager;



public class MessageProcessor {



    private static final String TAG = "PROTOCOLO";



    private final Context context;

    private final SessionManager sessionManager;

    private final UdpCliente udpCliente;

    private final AtributoPerfilRepository atributoRepository;

    private final AnuncioRecebidoRepository anuncioRepository;

    private final LocalRepository localRepository;

    private final LocalizacaoProvider localizacaoProvider;



    public MessageProcessor(Context context) {

        this.context = context.getApplicationContext();

        this.sessionManager = new SessionManager(this.context);

        this.udpCliente = new UdpCliente();

        this.atributoRepository = new AtributoPerfilRepository(this.context);

        this.anuncioRepository = new AnuncioRecebidoRepository(this.context);

        this.localRepository = new LocalRepository(this.context);

        this.localizacaoProvider = new LocalizacaoProvider(this.context);

    }



    public void processar(String json, InetAddress origem) {

        Message mensagem = JsonConverter.fromJson(json, Message.class);



        if (mensagem == null || mensagem.getType() == null) {

            Log.w(TAG, "Mensagem inválida");

            return;

        }



        switch (mensagem.getType()) {

            case Protocol.HELLO:

                processarHello(mensagem, origem);

                break;

            case Protocol.HELLO_ACK:

                processarHelloAck(mensagem, origem);

                break;

            case Protocol.PROFILE_REQUEST:

                processarProfileRequest(mensagem, origem);

                break;

            case Protocol.PROFILE_RESPONSE:

                processarProfileResponse(mensagem);

                break;

            case Protocol.ADVERTISEMENT:

                processarAdvertisement(mensagem, origem);

                break;

            case Protocol.ACK:

                processarAck(mensagem);

                break;

            default:

                Log.w(TAG, "Tipo desconhecido: " + mensagem.getType());

        }

    }



    private void processarHello(Message mensagem, InetAddress origem) {

        Log.d(TAG, "HELLO recebido de " + mensagem.getSender());

        // Adiciona o peer que enviou o HELLO

        PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));

        

        Message resposta = new Message();

        resposta.setType(Protocol.HELLO_ACK);

        resposta.setSender(sessionManager.getNome());

        enviar(resposta, origem);

    }



    private void processarHelloAck(Message mensagem, InetAddress origem) {

        PeerManager.adicionar(new Peer(mensagem.getSender(), origem, UdpCliente.PORT));

        Log.d(TAG, "HELLO_ACK recebido. Peer confirmado: " + mensagem.getSender());

        

        // Opcional: Solicitar perfil após handshake

        Message pedido = new Message();

        pedido.setType(Protocol.PROFILE_REQUEST);

        pedido.setSender(sessionManager.getNome());

        enviar(pedido, origem);

    }



    private void processarProfileRequest(Message mensagem, InetAddress origem) {

        int idUtilizador = sessionManager.getIdUtilizador();

        atributoRepository.listarPorUtilizador(idUtilizador, atributos -> {

            Map<String, String> perfil = new HashMap<>();

            for (AtributoPerfil atributo : atributos) {

                perfil.put(atributo.getChave(), atributo.getValor());

            }

            Message resposta = new Message();

            resposta.setType(Protocol.PROFILE_RESPONSE);

            resposta.setSender(sessionManager.getNome());

            resposta.setPayload(JsonConverter.toJson(perfil));

            enviar(resposta, origem);

        });

    }



    private void processarProfileResponse(Message mensagem) {

        Map<String, String> perfil = JsonConverter.toMap(mensagem.getPayload());

        Log.d(TAG, "Perfil recebido: " + perfil);

    }



    private void processarAdvertisement(Message mensagem, InetAddress origem) {

        LocalDateTime agora = LocalDateTime.now();

        if (mensagem.getDataInicio() != null && !mensagem.getDataInicio().isEmpty()) {

            LocalDateTime inicio = LocalDateTime.parse(mensagem.getDataInicio());

            if (agora.isBefore(inicio)) return;

        }

        if (mensagem.getDataFim() != null && !mensagem.getDataFim().isEmpty()) {

            LocalDateTime fim = LocalDateTime.parse(mensagem.getDataFim());

            if (agora.isAfter(fim)) return;

        }



        int idUtilizador = sessionManager.getIdUtilizador();

        atributoRepository.listarPorUtilizador(idUtilizador, atributos -> {

            Map<String, String> perfil = new HashMap<>();

            for (AtributoPerfil atributo : atributos) {

                perfil.put(atributo.getChave(), atributo.getValor());

            }



            if (!ProfileMatcher.aceitar(mensagem.getPoliticaTipo(), mensagem.getPoliticaChaves(), perfil)) {

                return;

            }



            try {

                int idLocal = Integer.parseInt(mensagem.getLocal());

                localRepository.buscarCompleto(idLocal, localTarget -> {

                    if (localTarget == null) return;



                    localizacaoProvider.obterLocalizacao((lat, lon) -> {

                        String ssidActual = localizacaoProvider.obterSsid();

                        if (!LocationMatcher.estaNoLocal(localTarget, lat, lon, ssidActual)) {

                            return;

                        }



                        AnuncioRecebido anuncio = new AnuncioRecebido();

                        anuncio.setMsgId(mensagem.getMsgId());

                        anuncio.setAutor(mensagem.getAutor());

                        anuncio.setTitulo(mensagem.getTitulo());

                        anuncio.setConteudo(mensagem.getConteudo());

                        anuncio.setLocal(localTarget.getLocal().getNome());

                        anuncio.setPoliticaTipo(mensagem.getPoliticaTipo());

                        anuncio.setPoliticaChaves(mensagem.getPoliticaChaves());

                        anuncio.setDataRececao(LocalDateTime.now());

                        

                        if (mensagem.getDataInicio() != null) anuncio.setDataInicio(LocalDateTime.parse(mensagem.getDataInicio()));

                        if (mensagem.getDataFim() != null) anuncio.setDataFim(LocalDateTime.parse(mensagem.getDataFim()));



                        anuncioRepository.receberAnuncioDescentralizado(anuncio, id -> {

                            if (id > 0) {

                                NotificationHelper.mostrarNotificacaoAnuncio(context, anuncio.getMsgId(), anuncio.getTitulo(), anuncio.getConteudo());

                                Message ack = new Message();

                                ack.setType(Protocol.ACK);

                                ack.setSender(sessionManager.getNome());

                                ack.setMsgId(mensagem.getMsgId());

                                enviar(ack, origem);

                            }

                        });

                    });

                });

            } catch (Exception e) {

                Log.e(TAG, "Erro ao processar anúncio", e);

            }

        });

    }



    private void processarAck(Message mensagem) {

        Log.d(TAG, "Entrega confirmada: " + mensagem.getMsgId());

    }



    private void enviar(Message mensagem, InetAddress destino) {

        String json = JsonConverter.toJson(mensagem);

        udpCliente.enviar(json, destino);

    }

}

", "package ao.uan.fc.dam.mobile.network;



import android.content.Context;

import android.util.Log;



import java.net.InetAddress;



import ao.uan.fc.dam.mobile.data.entity.Anuncio;

import ao.uan.fc.dam.mobile.util.SessionManager;



public class MessagePublisher {



    private static final String TAG = "MessagePublisher";

    private final SessionManager sessionManager;

    private final UdpCliente udpCliente;



    public MessagePublisher(Context context){

        sessionManager = new SessionManager(context.getApplicationContext());

        udpCliente = new UdpCliente();

    }



    public void enviarHello(InetAddress destino) {

        Message mensagem = new Message();

        mensagem.setType(Protocol.HELLO);

        mensagem.setSender(sessionManager.getNome());

        

        String json = JsonConverter.toJson(mensagem);

        udpCliente.enviar(json, destino);

        Log.d(TAG, "HELLO enviado para " + destino.getHostAddress());

    }



    public void publicar(Anuncio anuncio){

        Message mensagem = new Message();

        mensagem.setType(Protocol.ADVERTISEMENT);

        mensagem.setSender(sessionManager.getNome());

        mensagem.setMsgId("anuncio-" + anuncio.getIdAnuncio());

        mensagem.setTitulo(anuncio.getTitulo());

        mensagem.setConteudo(anuncio.getConteudo());

        mensagem.setAutor(sessionManager.getNome());

        mensagem.setLocal(String.valueOf(anuncio.getIdLocal()));

        mensagem.setPoliticaTipo(anuncio.getVisibilidade().name());

        mensagem.setPoliticaChaves(anuncio.getRestricaoPerfil());



        // Janela temporal

        if (anuncio.getDataInicio() != null) {

            mensagem.setDataInicio(anuncio.getDataInicio().toString());

        }

        if (anuncio.getDataFim() != null) {

            mensagem.setDataFim(anuncio.getDataFim().toString());

        }



        enviarParaPeers(mensagem);

    }



    private void enviarParaPeers(Message mensagem){

        String json = JsonConverter.toJson(mensagem);



        if(PeerManager.listar().isEmpty()){

            Log.w(TAG, "Nenhum peer conectado via handshake");

            return;

        }



        for(Peer peer : PeerManager.listar()){

            udpCliente.enviar(json, peer.getEndereco());

            Log.d(TAG, "ADVERTISEMENT enviado para " + peer.getNome() + " (" + peer.getEndereco().getHostAddress() + ")");

        }

    }

}

", "package ao.uan.fc.dam.mobile.network;



import android.content.Context;



import java.net.InetAddress;



public class MessageRouter {



    private final MessageProcessor messageProcessor;



    public MessageRouter(Context context) {



        messageProcessor = new MessageProcessor(

                context.getApplicationContext()

        );



    }



    public void processar(

            String json,

            InetAddress origem

    ) {



        messageProcessor.processar(

                json,

                origem

        );



    }



}", "package ao.uan.fc.dam.mobile.network;



import java.net.InetAddress;



public class Peer {



    private String nome;



    private InetAddress endereco;



    private int porta;



    public Peer(String nome, InetAddress endereco, int porta){

        this.nome = nome;

        this.endereco = endereco;

        this.porta = porta;

    }



    public String getNome() {

        return nome;

    }



    public InetAddress getEndereco() {

        return endereco;

    }



    public int getPorta() {

        return porta;

    }



    @Override

    public boolean equals(Object obj){

        if(this == obj)

            return true;



        if(!(obj instanceof Peer))

            return false;



        Peer outro = (Peer) obj;

        return endereco.equals(outro.endereco);

    }



    @Override

    public int hashCode(){

        return endereco.hashCode();

    }

}", "package ao.uan.fc.dam.mobile.network;



import android.Manifest;

import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.content.Intent;

import android.content.IntentFilter;

import android.content.pm.PackageManager;

import android.net.wifi.p2p.WifiP2pConfig;

import android.net.wifi.p2p.WifiP2pDevice;

import android.net.wifi.p2p.WifiP2pInfo;

import android.net.wifi.p2p.WifiP2pManager;

import android.os.Build;

import android.os.Handler;

import android.os.Looper;

import android.util.Log;



import androidx.core.app.ActivityCompat;



import java.util.ArrayList;

import java.util.List;



public class WifiDirectManager {



    private static final String TAG = "WifiDirectManager";

    private static final long INTERVALO_DESCOBERTA = 20000; // 20 segundos



    private final Context context;

    private final WifiP2pManager manager;

    private final WifiP2pManager.Channel channel;

    private final IntentFilter intentFilter;

    private final WifiDirectListener listener;



    private BroadcastReceiver receiver;

    private boolean registado = false;

    private boolean ligado = false;



    private String enderecoLocal;

    private boolean aLigar = false;



    private final Handler handler = new Handler(Looper.getMainLooper());

    private boolean autoDescobertaActiva = false;



    private final Runnable discoveryRunnable = new Runnable() {

        @Override

        public void run() {

            if (autoDescobertaActiva && !ligado && !aLigar) {

                descobrirPeers();

            }

            if (autoDescobertaActiva) {

                handler.postDelayed(this, INTERVALO_DESCOBERTA);

            }

        }

    };



    public interface WifiDirectListener {

        void onPeersDisponiveis(List<WifiP2pDevice> peers);

        void onLigacaoEstabelecida(WifiP2pInfo info);

        void onWifiDirectIndisponivel();

    }



    public WifiDirectManager(Context context, WifiDirectListener listener) {

        this.context = context.getApplicationContext();

        this.listener = listener;



        manager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);

        channel = manager.initialize(context, context.getMainLooper(), null);



        intentFilter = new IntentFilter();

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

    }



    public void iniciarAutonomo() {

        registar();

        autoDescobertaActiva = true;

        handler.removeCallbacks(discoveryRunnable);

        handler.post(discoveryRunnable);

        Log.d(TAG, "Modo autónomo iniciado");

    }



    public void pararAutonomo() {

        autoDescobertaActiva = false;

        handler.removeCallbacks(discoveryRunnable);

        desconectar();

        cancelarRegistro();

        Log.d(TAG, "Modo autónomo parado");

    }



    public void registar() {

        if (registado) return;



        receiver = new BroadcastReceiver() {

            @Override

            @SuppressLint("MissingPermission")

            public void onReceive(Context ctx, Intent intent) {

                String action = intent.getAction();

                if (action == null) return;



                switch (action) {

                    case WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION:

                        int estado = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

                        if (estado != WifiP2pManager.WIFI_P2P_STATE_ENABLED) {

                            Log.w(TAG, "WiFi Direct desativado");

                            listener.onWifiDirectIndisponivel();

                        }

                        break;



                    case WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION:

                        WifiP2pDevice thisDevice = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);

                        if (thisDevice != null) {

                            enderecoLocal = thisDevice.deviceAddress;

                        }

                        break;



                    case WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION:

                        if (temPermissoes()) {

                            manager.requestPeers(channel, peerList -> {

                                List<WifiP2pDevice> peers = new ArrayList<>(peerList.getDeviceList());

                                Log.d(TAG, "Peers detectados: " + peers.size());

                                listener.onPeersDisponiveis(peers);

                            });

                        }

                        break;



                    case WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION:

                        if (temPermissoes()) {

                            manager.requestConnectionInfo(channel, info -> {

                                if (info.groupFormed) {

                                    ligado = true;

                                    aLigar = false;

                                    String ipPeer = info.isGroupOwner ? "192.168.49.2" : info.groupOwnerAddress.getHostAddress();

                                    

                                    try {

                                        Peer peer = new Peer("WiFiDirect", java.net.InetAddress.getByName(ipPeer), 8888);

                                        PeerManager.adicionar(peer);

                                    } catch (Exception e) {

                                        Log.e(TAG, "Erro ao processar IP", e);

                                    }

                                    listener.onLigacaoEstabelecida(info);

                                } else {

                                    ligado = false;

                                    // Se a ligação caiu, reinicia descoberta se autónomo

                                    if (autoDescobertaActiva && !aLigar) {

                                        descobrirPeers();

                                    }

                                }

                            });

                        }

                        break;

                }

            }

        };



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            context.registerReceiver(receiver, intentFilter, Context.RECEIVER_EXPORTED);

        } else {

            context.registerReceiver(receiver, intentFilter);

        }

        registado = true;

    }



    public void cancelarRegistro() {

        if (registado && receiver != null) {

            context.unregisterReceiver(receiver);

            registado = false;

        }

    }



    @SuppressLint("MissingPermission")

    public void descobrirPeers() {

        if (!temPermissoes()) return;



        manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

            @Override

            public void onSuccess() {

                Log.d(TAG, "Descoberta automática iniciada");

            }



            @Override

            public void onFailure(int reasonCode) {

                Log.e(TAG, "Falha na descoberta: " + reasonCode);

            }

        });

    }



    @SuppressLint("MissingPermission")

    public void conectarComDesempate(WifiP2pDevice device) {

        if (!temPermissoes() || aLigar || ligado) return;



        if (enderecoLocal == null || enderecoLocal.equals("02:00:00:00:00:00")) {

            return;

        }

        

        // Estratégia de desempate para evitar conflitos de conexão simultânea

        if (enderecoLocal.compareTo(device.deviceAddress) >= 0) {

            return;

        }



        aLigar = true;

        WifiP2pConfig config = new WifiP2pConfig();

        config.deviceAddress = device.deviceAddress;



        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override

            public void onSuccess() {

                Log.d(TAG, "Convite enviado para: " + device.deviceName);

            }



            @Override

            public void onFailure(int reason) {

                aLigar = false;

                Log.e(TAG, "Erro ao conectar: " + reason);

            }

        });

    }



    public void desconectar() {

        ligado = false;

        aLigar = false;

        manager.removeGroup(channel, null);

    }



    private boolean temPermissoes() {

        boolean localizacao = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        boolean nearby = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            nearby = ActivityCompat.checkSelfPermission(context, Manifest.permission.NEARBY_WIFI_DEVICES) == PackageManager.PERMISSION_GRANTED;

        }

        return localizacao && nearby;

    }

}", "package ao.uan.fc.dam.mobile.network;



import android.content.Context;

import android.util.Log;



import java.net.DatagramPacket;

import java.net.DatagramSocket;

import java.nio.charset.StandardCharsets;



public class UdpServidor {

    private final MessageProcessor processor;

    public static final int PORT = 8888;

    private boolean running;

    private DatagramSocket socket;



    public UdpServidor(Context context) {

        processor = new MessageProcessor(context);

    }



    public void iniciar() {

        running = true;



        new Thread(() -> {

            try {

                socket = new DatagramSocket(PORT);

                Log.d("UDP", "Servidor iniciado na porta " + PORT);



                while (running) {

                    byte[] buffer = new byte[8192];

                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                    socket.receive(packet);



                    String mensagem = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

                    Log.d("UDP", "Mensagem recebida: " + mensagem);



                    processor.processar(mensagem, packet.getAddress());

                }

            } catch (Exception e) {



                if(running){

                    Log.e("UDP", "Erro no servidor", e);

                } else {

                    Log.d("UDP", "Servidor UDP encerrado");

                }



            }

        }).start();

    }



    public void parar() {

        Log.d("UDP", "PEDIDO PARA PARAR SERVIDOR");



        running = false;



        if(socket != null){

            socket.close();

        }

    }

}", "package ao.uan.fc.dam.mobile.network;



import android.util.Log;



import java.net.DatagramPacket;

import java.net.DatagramSocket;

import java.net.InetAddress;

import java.nio.charset.StandardCharsets;



public class UdpCliente {



    public static final int PORT = 8888;



    public void enviar(String mensagem,

                       InetAddress destino){



        new Thread(() -> {



            try{



                DatagramSocket socket =

                        new DatagramSocket();



                byte[] dados = mensagem.getBytes(StandardCharsets.UTF_8);



                DatagramPacket packet =

                        new DatagramPacket(

                                dados,

                                dados.length,

                                destino,

                                PORT

                        );



                socket.send(packet);



                socket.close();



            }catch (Exception e){



                Log.e(

                        "UDP",

                        "Erro ao enviar",

                        e

                );



            }



        }).start();



    }



}", "package ao.uan.fc.dam.mobile.network;



public class Protocol {



    public static final String HELLO = "HELLO";



    public static final String HELLO_ACK = "HELLO_ACK";



    public static final String PROFILE_REQUEST = "PROFILE_REQUEST";



    public static final String PROFILE_RESPONSE = "PROFILE_RESPONSE";



    public static final String ADVERTISEMENT = "ADVERTISEMENT";



    public static final String ACK = "ACK";



}

", "package ao.uan.fc.dam.mobile.network;



import java.util.Map;



public class ProfileMatcher {

    public static boolean aceitar(String politica, String restricao, Map<String, String> perfil) {



        if (restricao == null || restricao.trim().isEmpty()) {

            return true;

        }



        String[] partes = restricao.split("=");



        if (partes.length != 2) {

            return true;

        }



        String chave = partes[0].trim();

        String valor = partes[1].trim();



        String valorPerfil = perfil.get(chave);



        if (valorPerfil == null) {

            return politica.equalsIgnoreCase("BLACKLIST");

        }



        boolean igual = valor.equalsIgnoreCase(valorPerfil);



        if ("WHITELIST".equalsIgnoreCase(politica)) {

            return igual;

        }



        return !igual;

    }



}

", "package ao.uan.fc.dam.mobile.network;



import java.util.ArrayList;

import java.util.List;



public class PeerManager {



    private static final List<Peer> peers = new ArrayList<>();



    public static synchronized void adicionar(Peer peer){

        for (int i = 0; i < peers.size(); i++) {

            if (peers.get(i).getEndereco().equals(peer.getEndereco())) {

                peers.set(i, peer); // Atualiza o peer (ex: nome real após handshake)

                return;

            }

        }

        peers.add(peer);

    }



    public static synchronized List<Peer> listar(){

        return new ArrayList<>(peers);

    }



    public static synchronized void limpar(){

        peers.clear();

    }

}

"