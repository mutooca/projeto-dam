# Guia de Utilizacao e Testes - AnunciosLoc

Este ficheiro serve como roteiro de uso e validacao do projecto AnunciosLoc, separado pela parte centralizada, que usa o servidor, e pela parte descentralizada, que usa comunicacao entre dispositivos proximos.

## 1. Preparacao

### 1.1. Servicos necessarios

Antes de testar, garantir que estes componentes estao activos quando forem usados:

- Servidor central AnunciosLoc.
- Servidor Kerberos, se a autenticacao estiver configurada para depender dele.
- Servidores de infraestrutura/UDDI, se o ambiente estiver a validar infraestrutura e saldo.
- Aplicacao Android instalada em um ou mais emuladores/dispositivos.

### 1.2. Configuracao do IP da API

Na aplicacao movel, confirmar o endereco do servidor em:

`aplicacao-movel/gradle.properties`

ou via propriedade Gradle:

```bash
./gradlew assembleDebug -PapiBaseUrl=http://10.0.2.2:8080/
```

Para emulador Android local, normalmente usar:

```text
http://10.0.2.2:8080/
```

Para dispositivo fisico, usar o IP da maquina na mesma rede Wi-Fi:

```text
http://IP_DA_MAQUINA:8080/
```

### 1.3. Compilar

Servidor:

```bash
cd anunciosloc_server
./mvnw test
```

Aplicacao movel:

```bash
cd aplicacao-movel
./gradlew assembleDebug
```

## 2. Guia de Utilizacao

### 2.1. Registar utilizador

1. Abrir a aplicacao.
2. Ir para a tela de cadastro.
3. Inserir nome, email e palavra-passe.
4. Confirmar o registo.
5. Validar que o servidor nao permite emails duplicados.

Resultado esperado:

- Utilizador criado no servidor.
- O utilizador consegue fazer login depois do registo.

### 2.2. Login e sessao

1. Inserir email e palavra-passe.
2. Confirmar login.
3. A aplicacao deve abrir o dashboard.
4. O servico de localizacao deve iniciar em foreground.

Resultado esperado:

- Sessao guardada no mobile.
- Headers Kerberos enviados nas chamadas protegidas.
- Cache inicial sincronizada: perfil, locais e anuncios.

### 2.3. Gerir locais

1. Abrir a area de locais.
2. Criar um local com nome, latitude, longitude e raio.
3. Confirmar que o local aparece na lista.
4. Remover um local criado pelo utilizador.

Resultado esperado:

- Local criado no servidor e guardado em cache local.
- Lista de locais actualizada.
- Remocao reflectida no servidor e na cache local.

### 2.4. Editar perfil

1. Abrir o perfil.
2. Adicionar atributos no formato chave/valor, por exemplo:

```text
clube=Real Madrid
profissao=Estudante
bairro=Maianga
```

3. Editar ou remover um atributo.
4. Ao criar anuncio, tocar no campo de restricoes para listar chaves publicas.

Resultado esperado:

- Pares privados do perfil guardados no servidor.
- Chaves publicas disponiveis para ajudar na criacao de politicas.
- Cache local actualizada para filtros offline/P2P.

## 3. Testes da Parte Centralizada

A parte centralizada usa o servidor para publicar, localizar, filtrar e entregar anuncios.

### CT-01 - Registo e login

Passos:

1. Registar utilizador `alice@test.com`.
2. Fazer login com a mesma conta.
3. Fazer logout.
4. Tentar login com palavra-passe errada.

Resultado esperado:

- Login correcto cria sessao.
- Logout termina sessao.
- Login invalido devolve erro.

### CT-02 - Criar e listar locais

Passos:

1. Fazer login.
2. Criar local `Largo da Independencia` com coordenadas GPS e raio.
3. Abrir/listar locais.

Resultado esperado:

- Local aparece na aplicacao.
- Local vem da rota central `/api/locais`.
- Dados sao guardados localmente para uso em cache.

### CT-03 - Publicar anuncio centralizado sem restricao

Passos:

1. Ir para "Postar".
2. Escolher um local.
3. Preencher titulo e mensagem.
4. Escolher politica `Whitelist` sem restricoes.
5. Escolher modo `Centralizado`.
6. Publicar.

Resultado esperado:

- Anuncio enviado para `/api/anuncios/postar`.
- Anuncio fica com estado activo no servidor.
- Autor ve o anuncio em "meus anuncios".

### CT-04 - Publicar anuncio centralizado com whitelist

Preparacao:

- Utilizador receptor deve ter no perfil:

```text
profissao=Estudante
```

Passos:

1. Publicador cria anuncio para um local.
2. Politica: `Whitelist`.
3. Restricao:

```text
profissao=Estudante
```

4. Receptor entra no raio GPS do local ou simula a localizacao no emulador.

Resultado esperado:

- Receptor com perfil correspondente recebe notificacao/anuncio.
- Receptor sem o atributo nao recebe.

### CT-05 - Publicar anuncio centralizado com blacklist

Preparacao:

- Um receptor tem:

```text
clube=Barcelona
```

Passos:

1. Criar anuncio com politica `Blacklist`.
2. Restricao:

```text
clube=Barcelona
```

3. Simular receptor dentro do local.

Resultado esperado:

- Utilizadores com `clube=Barcelona` nao recebem.
- Utilizadores sem esse par recebem.

### CT-06 - Sincronizacao periodica por localizacao

Passos:

1. Fazer login no mobile.
2. Confirmar que o `LocationService` iniciou.
3. Simular coordenadas dentro de um local com anuncios activos.
4. Aguardar o intervalo de sincronizacao.

Resultado esperado:

- Mobile chama `/api/anuncios/sync/localizacao`.
- Servidor compara GPS/Wi-Fi com locais existentes.
- Anuncios elegiveis sao devolvidos ao mobile.

### CT-07 - Janela temporal do anuncio

Passos:

1. Criar anuncio com janela de visibilidade valida.
2. Confirmar recebimento dentro da janela.
3. Criar ou alterar outro anuncio para janela expirada.
4. Simular receptor no local.

Resultado esperado:

- Anuncio dentro da janela e entregue.
- Anuncio expirado nao e entregue.

### CT-08 - Marcar anuncio como lido

Passos:

1. Receptor recebe anuncio.
2. Abrir/visualizar anuncio.
3. Marcar como lido.

Resultado esperado:

- Servidor actualiza entrega para `LIDO`.
- Anuncio continua disponivel localmente depois de recebido.

## 4. Testes da Parte Descentralizada

A parte descentralizada usa comunicacao entre dispositivos proximos. No projecto, a camada P2P usa pacotes UDP para simular descoberta, troca de perfil e entrega de anuncio.

### DT-01 - Iniciar camada descentralizada

Passos:

1. Fazer login no mobile.
2. Confirmar que o `LocationService` esta activo.
3. A camada `DecentralizedManager` deve iniciar junto com o servico.

Resultado esperado:

- Servidor UDP local fica a escutar pacotes.
- Aplicacao esta pronta para descoberta e recepcao P2P.

### DT-02 - Descoberta de vizinho

Preparacao:

- Ter dois emuladores/dispositivos na mesma rede ou ambiente de teste.
- Identificar o IP do segundo dispositivo/emulador.

Passos:

1. No dispositivo A, iniciar descoberta para o IP do dispositivo B.
2. Dispositivo B recebe `HELLO`.
3. Dispositivo B responde `HELLO_ACK`.
4. Dispositivo A solicita perfil com `PROFILE_REQUEST`.

Resultado esperado:

- Vizinhos ficam registados localmente.
- Perfil do vizinho e recebido em `PROFILE_RESPONSE`.

### DT-03 - Publicar anuncio descentralizado

Passos:

1. Ir para "Postar".
2. Escolher local.
3. Preencher titulo, mensagem, politica e restricoes.
4. Escolher modo `Descentralizado`.
5. Publicar.

Resultado esperado:

- Anuncio nao depende do endpoint central de publicacao.
- Anuncio e guardado na Room/local cache do publicador.
- Camada P2P tenta enviar anuncios para vizinhos conhecidos.

### DT-04 - Entrega P2P com whitelist

Preparacao:

- Dispositivo B deve ter no perfil:

```text
profissao=Estudante
```

Passos:

1. Dispositivo A cria anuncio descentralizado com:

```text
Whitelist: profissao=Estudante
```

2. Dispositivo A descobre B.
3. Sincronizacao P2P e executada.

Resultado esperado:

- Dispositivo A compara politica com perfil de B.
- Se o perfil corresponde, A envia pacote `ADVERTISEMENT`.
- B guarda o anuncio localmente e responde com `ACK`.

### DT-05 - Entrega P2P com blacklist

Preparacao:

- Dispositivo B tem:

```text
clube=Barcelona
```

Passos:

1. Dispositivo A cria anuncio descentralizado com:

```text
Blacklist: clube=Barcelona
```

2. Executar descoberta e sincronizacao.

Resultado esperado:

- Dispositivo B nao recebe o anuncio.
- Outro dispositivo sem esse atributo pode receber.

### DT-06 - Receptor guarda anuncio recebido

Passos:

1. Dispositivo B recebe pacote `ADVERTISEMENT`.
2. Verificar lista/cache local de anuncios.

Resultado esperado:

- Anuncio recebido fica guardado no Room.
- Anuncio pode ser lido mesmo sem servidor central.
- Dispositivo B envia `ACK` ao remetente.

### DT-07 - Sem vizinhos conhecidos

Passos:

1. Publicar anuncio descentralizado.
2. Nao executar descoberta ou desligar outros dispositivos.

Resultado esperado:

- Anuncio fica guardado localmente no publicador.
- Nenhuma entrega P2P ocorre ate existir vizinho conhecido.

## 5. Roteiro Rapido de Demonstracao

### Demonstracao centralizada

1. Iniciar servidor central.
2. Abrir app no emulador A.
3. Registar/login.
4. Criar local.
5. Criar perfil com atributo `profissao=Estudante`.
6. Publicar anuncio centralizado com whitelist `profissao=Estudante`.
7. Simular localizacao dentro do local.
8. Confirmar notificacao/recepcao.

### Demonstracao descentralizada

1. Abrir app em dois dispositivos/emuladores.
2. Fazer login em ambos.
3. Configurar perfil do receptor.
4. Executar descoberta entre os dispositivos.
5. Publicar anuncio com modo `Descentralizado`.
6. Confirmar que o receptor recebe e guarda o anuncio sem depender da publicacao no servidor.

## 6. Problemas Comuns

### App nao comunica com servidor

Verificar:

- IP em `apiBaseUrl`.
- Servidor Spring activo.
- Firewall do Windows.
- `android:usesCleartextTraffic="true"` no Manifest.

### Login falha

Verificar:

- Credenciais.
- Servidor Kerberos.
- Chaves/tickets de sessao.
- Logs do servidor.

### Local nao aparece

Verificar:

- Coordenadas dentro de uma infraestrutura activa.
- Servidor de infraestrutura/UDDI.
- Cache local da app.

### Anuncio nao e entregue no modo centralizado

Verificar:

- Coordenadas do receptor dentro do raio do local.
- Janela temporal do anuncio.
- Politica whitelist/blacklist.
- Perfil do receptor.
- Chamada `/api/anuncios/sync/localizacao`.

### Anuncio nao e entregue no modo descentralizado

Verificar:

- Dispositivos na mesma rede.
- IP correcto na descoberta.
- Porta UDP `8888` disponivel.
- Perfil do vizinho recebido.
- Politica do anuncio.
- Firewall.

## 7. Checklist Final

- [ ] Registo de utilizador funciona.
- [ ] Login/logout funciona.
- [ ] Criar/listar/remover locais funciona.
- [ ] Editar perfil e listar chaves publicas funciona.
- [ ] Publicar/remover anuncio funciona.
- [ ] Visualizar anuncio funciona.
- [ ] Entrega centralizada por localizacao funciona.
- [ ] Cache local funciona.
- [ ] Publicacao descentralizada guarda anuncio localmente.
- [ ] Descoberta P2P funciona.
- [ ] Entrega P2P respeita whitelist/blacklist.
- [ ] Build do servidor passa.
- [ ] Build Android passa.
