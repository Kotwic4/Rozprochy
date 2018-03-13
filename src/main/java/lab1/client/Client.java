package lab1.client;

import com.google.gson.Gson;
import lab1.util.*;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class Client {

    public static void main(String[] args) {
        System.out.println("Client started!");
        int portNumber = 12345;
        String hostname = "localhost";
        int multicastPort = 23451;
        String multicastHost = "239.0.0.1";
        int clientId;
        TcpConnection tcpConnection = null;
        UdpConnection udpConnection = null;
        MulticastConnection multicastConnection = null;
        TcpReader tcpReader = null;
        ChatWriter chatWriter = null;
        UdpReader udpReader = null;
        MulticastReader multicastReader = null;
        Boolean closed = false;
        try {
            tcpConnection = TcpConnection.connectToServer(hostname, portNumber).orElseThrow(IOException::new);
            Message message = tcpConnection.recvMsg();
            System.out.println(message.value);
            if (message.type != MessageType.Rejected) {
                clientId = message.clientName;
                udpConnection = UdpConnection.connect(portNumber, hostname);
                udpConnection.send(new Message(clientId, MessageType.HelloUdp, ""));
                multicastConnection = MulticastConnection.join(multicastPort, multicastHost);
                tcpReader = new TcpReader(tcpConnection, new Gson());
                udpReader = new UdpReader(udpConnection, new Gson());
                multicastReader = new MulticastReader(multicastConnection, new Gson(), clientId);
                CompletableFuture<Boolean> quit = new CompletableFuture<>();
                chatWriter = new ChatWriter(tcpConnection, udpConnection, multicastConnection, clientId, quit);
                tcpReader.start();
                udpReader.start();
                chatWriter.start();
                multicastReader.start();
                closed = quit.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (tcpReader != null) {
                tcpReader.close();
            }
            if (chatWriter != null) {
                chatWriter.close();
            }
            if (udpReader != null) {
                udpReader.close();
            }
            if (multicastReader != null) {
                multicastReader.close();
            }
            if (tcpConnection != null) {
                try {
                    tcpConnection.close();
                } catch (IOException e) {
                    if (!closed) {
                        e.printStackTrace();
                    }
                }
            }
            if (udpConnection != null) {
                udpConnection.close();
            }
            if (multicastConnection != null) {
                try {
                    multicastConnection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}


