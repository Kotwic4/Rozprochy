package lab1.client;

import lab1.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;

public class ChatWriter extends Thread {


    private TcpConnection tcpConnection;
    private UdpConnection udpConnection;
    private MulticastConnection multicastConnection;
    private int clientId;
    private CompletableFuture<Boolean> quit;
    private boolean closed = false;

    public ChatWriter(TcpConnection tcpConnection, UdpConnection udpConnection, MulticastConnection multicastConnection, int clientId, CompletableFuture<Boolean> quit) {
        this.tcpConnection = tcpConnection;
        this.udpConnection = udpConnection;
        this.multicastConnection = multicastConnection;
        this.clientId = clientId;
        this.quit = quit;
    }

    @Override
    public void run() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            while (true) {
                System.out.print(">:");
                String input = br.readLine();
                if (input.equals("Q")) {
                    tcpConnection.send(new Message(clientId, MessageType.Quit, ""));
                    closed = true;
                    break;
                } else if (input.equals("U")) {
                    input = br.readLine();
                    udpConnection.send(new Message(clientId, MessageType.MsgUdp, input));
                } else if (input.equals("M")) {
                    input = br.readLine();
                    multicastConnection.send(new Message(clientId, MessageType.MsgMulti, input));
                } else {
                    tcpConnection.send(new Message(clientId, MessageType.MsgTcp, input));
                }
            }
        } catch (IOException e) {
            if (!closed) {
                e.printStackTrace();
            }
        }
        quit.complete(closed);
    }

    public void close() {
        closed = true;
    }
}
