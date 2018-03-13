package lab1.server;

import com.google.gson.Gson;
import lab1.util.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;

public class ClientConnection implements Runnable {

    private TcpConnection tcpConnection;
    private UdpConnection udpConnection = null;
    private int id;
    private ConnectionManager connectionManager;
    private boolean closed;
    private Gson gson;

    public ClientConnection(TcpConnection tcpConnection, int id, ConnectionManager connectionManager, Gson gson) {
        this.tcpConnection = tcpConnection;
        this.id = id;
        this.connectionManager = connectionManager;
        this.gson = gson;
        closed = false;
    }

    @Override
    public void run() {
        try {
            Message message;
            String msg;
            while ((msg = tcpConnection.recv()) != null) {
                System.out.println(id + ": " + msg);
                message = gson.fromJson(msg,Message.class);
                if(message.type == MessageType.MsgTcp){
                    connectionManager.sendTcpMsg(id, message.value);
                }
                else if(message.type == MessageType.Quit){
                    break;
                }
            }
        } catch (IOException e) {
            if (!closed) {
                e.printStackTrace();
            }
        } finally {
            connectionManager.removeClient(id);
        }
    }

    public synchronized void sendTcoMsg(Message msg) {
        tcpConnection.send(msg);
    }

    public synchronized void close() {
        if(!closed){
            try {
                tcpConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(udpConnection != null){
                udpConnection.close();
            }
        }
        closed = true;
    }

    public synchronized void addUdp(DatagramPacket datagramPacket) {
        try {
            udpConnection = UdpConnection.connect(datagramPacket.getPort(),datagramPacket.getAddress());
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public synchronized void sendUdpMsg(Message message) {
        if(udpConnection != null){
            try {
                udpConnection.send(gson.toJson(message));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

