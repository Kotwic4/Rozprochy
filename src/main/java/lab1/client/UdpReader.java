package lab1.client;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.UdpConnection;

import java.io.IOException;

public class UdpReader extends Thread {

    private UdpConnection udpConnection;
    private Gson gson;
    private boolean closed = false;

    public UdpReader(UdpConnection udpConnection, Gson gson) {
        this.udpConnection = udpConnection;
        this.gson = gson;
    }

    @Override
    public void run() {
        try {
            String msg;
            while ((msg = udpConnection.recv()) != null) {
                Message message = gson.fromJson(msg, Message.class);
                System.out.println("UDP: " + message.clientName + ": " + message.value);
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