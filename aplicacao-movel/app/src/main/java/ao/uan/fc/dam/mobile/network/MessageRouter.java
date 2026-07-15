package ao.uan.fc.dam.mobile.network;

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

}