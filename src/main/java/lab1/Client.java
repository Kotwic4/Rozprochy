package lab1;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.MessageType;
import lab1.util.TcpConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.Thread.sleep;

public class Client {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Client started!");
        int portNumber = 12345;
        String hostname = "localhost";
        int clientId;
        TcpConnection tcpConnection = null;
        Thread chatReader = null;
        ChatReader cr = null;
        Boolean closed = false;
        try {
            tcpConnection = TcpConnection.connectToServer(hostname, portNumber)
                    .orElseThrow(IOException::new);
            System.out.println("Listen on TCP on port:" + portNumber);
            Message message = tcpConnection.recvMsg();
            System.out.println(message.value);
            if(message.type != MessageType.Rejected){
                clientId = message.clientName;
                cr = new ChatReader(tcpConnection, new Gson());
                chatReader = new Thread(cr);
                chatReader.start();
                while(true){
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print(">:");
                    String input = br.readLine();
                    if(input.equals("Q")){
                        tcpConnection.send(new Message(clientId,MessageType.Quit,""));
                        closed = true;
                        break;
                    }
                    tcpConnection.send(new Message(clientId,MessageType.MsgTcp,input));
                }
            }
        } catch (IOException e) {
            if(!closed){
                e.printStackTrace();
            }
        } finally {
            if(cr != null){
                cr.close();
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
        }
    }
}

class ChatReader implements Runnable{

    private TcpConnection tcpConnection;
    private Gson gson;
    private boolean closed = false;

    public ChatReader(TcpConnection tcpConnection, Gson gson) {
        this.tcpConnection = tcpConnection;
        this.gson = gson;
    }

    @Override
    public void run() {
        try {
            String msg;
            while((msg = tcpConnection.recv()) != null){
                Message message = gson.fromJson(msg,Message.class);
                System.out.println(message.clientName + ": " + message.value);
            }
        } catch (IOException e) {
            if(!closed){
                e.printStackTrace();
            }
        }
    }

    public void close(){
        closed = true;
    }
}
