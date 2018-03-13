package lab1.server;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.MessageType;
import lab1.util.TcpConnection;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ConnectionManager {

    private HashMap<Integer, ClientConnection> clients;
    private final int connectionLimit;
    private Executor executor;
    private int nextId = 0;

    public ConnectionManager(int connectionLimit) {
        this.connectionLimit = connectionLimit;
        executor = Executors.newFixedThreadPool(this.connectionLimit);
        clients = new HashMap<>();
    }

    public synchronized void addClient(TcpConnection tcpConnection) {
        if (clients.size() < connectionLimit) {
            ClientConnection clientConnection = new ClientConnection(tcpConnection, nextId, this, new Gson());
            tcpConnection.send(new Message(nextId, MessageType.HelloTcp, "Your ID is : " + nextId));
            clients.put(nextId, clientConnection);
            executor.execute(clientConnection);
            nextId++;
        } else {
            tcpConnection.send(new Message(-1, MessageType.Rejected, "Too many connections"));
            try {
                tcpConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void removeClient(int id) {
        if (clients.containsKey(id)) {
            ClientConnection clientConnection = clients.remove(id);
            clientConnection.close();
        }
    }

    public synchronized void sendTcpMsg(int senderId, String msgValue) {
        Message message = new Message(senderId, MessageType.MsgTcp, msgValue);
        clients.forEach((id, connection) -> {
            if (id != senderId) {
                connection.sendTcoMsg(message);
            }
        });
    }

    public synchronized void addUdpClient(int id, DatagramPacket datagramPacket) {
        if (clients.containsKey(id)) {
            ClientConnection clientConnection = clients.get(id);
            clientConnection.addUdp(datagramPacket);
        }
    }

    public synchronized void sendUdpMsg(int senderId, String msgValue) {
        Message message = new Message(senderId, MessageType.MsgUdp, msgValue);
        clients.forEach((id, connection) -> {
            if (id != senderId) {
                connection.sendUdpMsg(message);
            }
        });
    }
}