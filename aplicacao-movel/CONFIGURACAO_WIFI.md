# Configuracao WiFi do AnunciosLoc

Para testar num telemovel fisico, o computador que executa o servidor e o telemovel devem estar ligados a mesma rede WiFi.

## Passos

1. Ligue o computador e o telemovel ao mesmo WiFi.
2. No Windows, confirme o IP WiFi com:

```powershell
ipconfig
```

3. Procure `Wireless LAN adapter WiFi` e copie o `IPv4 Address`.
   Nesta maquina, no momento da configuracao, o IP e:

```text
192.168.223.229
```

4. Garanta que a app aponta para o servidor:

```properties
apiBaseUrl=http://192.168.223.229:8080/
```

5. O servidor AnunciosLoc deve estar acessivel nessa porta pelo WiFi. Se o servidor Spring estiver preso a `localhost`, ele nao sera visivel pelo telemovel. Use `0.0.0.0` ou o IP WiFi como endereco de bind.

6. Se o Windows Firewall bloquear a porta, libere a porta `8080` para rede privada.

## Build para outro IP

Sem editar ficheiros, pode gerar o APK assim:

```powershell
.\gradlew.bat :app:assembleDebug -PapiBaseUrl=http://NOVO_IP:8080/
```

## Notas

- Emulador Android usa normalmente `http://10.0.2.2:8080/`.
- Telemovel fisico usa o IP WiFi real do computador, por exemplo `http://192.168.223.229:8080/`.
- Se trocar de WiFi/hotspot, confirme novamente o IP.
