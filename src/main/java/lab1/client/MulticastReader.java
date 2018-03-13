package lab1.client;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.MulticastConnection;

import java.io.IOException;

public class MulticastReader extends Thread {

    private MulticastConnection multicastConnection;
    private Gson gson;
    private boolean closed = false;
    private int clientId;

    public MulticastReader(MulticastConnection multicastConnection, Gson gson, int clientId) {
        this.multicastConnection = multicastConnection;
        this.gson = gson;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = multicastConnection.recv()) != null) {
                Message message = gson.fromJson(msg, Message.class);
                if (message.clientName != clientId) {
                    System.out.println("MultiCast: " + message.clientName + ": " + message.value);
                }
            }
        } catch (IOException e) {
            if (!closed) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        closed = true;
    }
}
