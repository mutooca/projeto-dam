package ao.uan.fc.dam.mobile.util;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import retrofit2.Response;

public final class ApiErrorUtils {
    private ApiErrorUtils() {
    }

    public static String mensagemFalhaRede(Throwable throwable) {
        if (throwable instanceof UnknownHostException) {
            return "Sem ligacao ao servidor. Confirma o IP/porta do AnunciosLoc.";
        }
        if (throwable instanceof SocketTimeoutException) {
            return "O servidor demorou a responder. Tenta novamente.";
        }
        return "Nao foi possivel contactar o servidor.";
    }

    public static String erroHttp(Response<?> response, String padrao) {
        if (response == null || response.errorBody() == null) {
            return padrao;
        }
        try {
            String erro = response.errorBody().string();
            return erro == null || erro.trim().isEmpty() ? padrao : erro;
        } catch (IOException ignored) {
            return padrao;
        }
    }
}
