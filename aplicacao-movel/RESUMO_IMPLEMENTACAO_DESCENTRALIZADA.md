# Resumo da Implementação: Modo Descentralizado - AnunciosLoc

Este documento resume todas as alterações e novas funcionalidades implementadas no projeto para suportar a comunicação P2P e armazenamento descentralizado.

## 1. Armazenamento e Persistência
*   **AnuncioRecebidoRepository**: Adicionado o método `receberAnuncioDescentralizado` que verifica a existência do `msgId` no Room antes de inserir, evitando duplicados na rede.
*   **DAOs (AnuncioDao / AnuncioRecebidoDao)**: Atualizados para retornar `LiveData`. Isso permite que a interface reaja instantaneamente a novas inserções via rede.

## 2. Comunicação e Rede Autónoma
*   **WifiDirectManager**: Transformado em um componente autónomo.
    *   **Auto-Descoberta**: Inicia a procura de peers a cada 20 segundos automaticamente.
    *   **Auto-Conexão**: Estabelece ligação com peers próximos usando uma lógica de desempate por endereço MAC.
    *   **Ciclo de Vida**: Gerido pela `DashboardActivity`, iniciando mal a aplicação é aberta.
*   **MessageProcessor (ProtocolHandler)**:
    *   Converte JSON de anúncios UDP em objetos `AnuncioRecebido`.
    *   Valida políticas de perfil (ProfileMatcher).
    *   Dispara notificações e guarda no Room.
    *   Envia confirmação (ACK) após a gravação.

## 3. Interface Reativa (UI)
*   **InicioFragment**:
    *   Implementado `MediatorLiveData` para combinar anúncios locais (servidor) e descentralizados (P2P) numa única lista reativa.
    *   Atualização automática do RecyclerView assim que o UDP recebe uma mensagem, sem necessidade de recarregar a página.
*   **Visualização de Detalhes**:
    *   Criado o `DetailAnuncioFragment` e o layout `fragment_detail_anuncio.xml`.
    *   Exibição completa: Título, Conteúdo, Autor, Data, Local, Política aplicada e Modo de Entrega.
*   **AnuncioModel**: Atualizado para suportar `msgId`, essencial para identificar anúncios na rede P2P.

## 4. Notificações Android
*   **NotificationHelper**: Nova utilidade que gere o `NotificationChannel` (Android 8+) e a exibição de alertas visuais.
*   **Integração**: Ao clicar na notificação "Novo anúncio perto de si", a app abre diretamente os detalhes do anúncio recebido.

## Ficheiros Criados/Alterados:
1.  `ao.uan.fc.dam.mobile.data.dao.AnuncioDao` (Alterado)
2.  `ao.uan.fc.dam.mobile.data.dao.AnuncioRecebidoDao` (Alterado)
3.  `ao.uan.fc.dam.mobile.data.repository.AnuncioRepository` (Alterado)
4.  `ao.uan.fc.dam.mobile.data.repository.AnuncioRecebidoRepository` (Alterado)
5.  `ao.uan.fc.dam.mobile.network.WifiDirectManager` (Melhorado)
6.  `ao.uan.fc.dam.mobile.network.MessageProcessor` (Alterado)
7.  `ao.uan.fc.dam.mobile.ui.activity.DashboardActivity` (Alterado)
8.  `ao.uan.fc.dam.mobile.ui.fragment.InicioFragment` (Alterado)
9.  `ao.uan.fc.dam.mobile.ui.fragment.DetailAnuncioFragment` (Novo)
10. `ao.uan.fc.dam.mobile.ui.model.AnuncioModel` (Alterado)
11. `ao.uan.fc.dam.mobile.util.NotificationHelper` (Novo)
12. `layout/fragment_detail_anuncio.xml` (Novo)
