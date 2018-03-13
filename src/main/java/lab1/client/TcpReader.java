package lab1.client;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.TcpConnection;

import java.io.IOException;

public class TcpReader extends Thread{

    private TcpConnection tcpConnection;
    private Gson gson;
    private boolean closed = false;

    public TcpReader(TcpConnection tcpConnection, Gson gson) {
        this.tcpConnection = tcpConnection;
        this.gson = gson;
    }

    @Override
    public void run() {
        try {
            String msg;
            while((msg = tcpConnection.recv()) != null){
                Message message = gson.fromJson(msg,Message.class);
                System.out.println("TCP: "+ message.clientName + ": " + message.value);
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
