package lab1.client;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.MessageType;
import lab1.util.TcpConnection;
import lab1.util.UdpConnection;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static java.lang.Thread.sleep;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Client started!");
        int portNumber = 12345;
        String hostname = "localhost";
        int clientId;
        TcpConnection tcpConnection = null;
        UdpConnection udpConnection = null;
        TcpReader tcpReader = null;
        ChatWriter chatWriter = null;
        UdpReader udpReader = null;
        Boolean closed = false;
        try {
            tcpConnection = TcpConnection.connectToServer(hostname, portNumber).orElseThrow(IOException::new);
            Message message = tcpConnection.recvMsg();
            System.out.println(message.value);
            if(message.type != MessageType.Rejected){
                clientId = message.clientName;
                udpConnection = UdpConnection.connect(portNumber,hostname);
                udpConnection.send(new Message(clientId,MessageType.HelloUdp,""));
                tcpReader = new TcpReader(tcpConnection, new Gson());
                udpReader = new UdpReader(udpConnection, new Gson());
                CompletableFuture<Boolean> quit = new CompletableFuture<>();
                chatWriter = new ChatWriter(tcpConnection, udpConnection, clientId,quit);
                tcpReader.start();
                udpReader.start();
                chatWriter.start();
                closed = quit.get();
            }
        } catch (Exception e) {
            if(!closed){
                e.printStackTrace();
            }
        } finally {
            if(tcpReader != null){
                tcpReader.close();
            }
            if(chatWriter != null){
                chatWriter.close();
            }
            if(udpReader != null){
                udpReader.close();
            }
            if(tcpConnection != null){
                try {
                    tcpConnection.close();
                } catch (IOException e) {
                    if(!closed){
                        e.printStackTrace();
                    }
                }
            }
            if(udpConnection != null){
                udpConnection.close();
            }
        }
    }
}


