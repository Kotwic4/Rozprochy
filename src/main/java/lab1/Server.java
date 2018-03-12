package lab1;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.MessageType;
import lab1.util.TcpConnection;
import lab1.util.TcpServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) {
        int portNumber = 12345;
        int connectionLimit = 5;
        try {
            TcpServer tcpServer = TcpServer.bootServer(portNumber);
            System.out.println("Server started!");
            System.out.println("Listen on TCP on port:" + portNumber);
            ConnectionManager connectionManager = new ConnectionManager(connectionLimit);
            while (true) {
                TcpConnection tcpConnection = tcpServer.getClient();
                connectionManager.addClient(tcpConnection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ConnectionManager {

    private HashMap<Integer, ClientConnection> clients;
    private final int connectionLimit;
    Executor executor;
    private int nextId = 0;

    public ConnectionManager(int connectionLimit) {
        this.connectionLimit = connectionLimit;
        executor = Executors.newFixedThreadPool(this.connectionLimit);
        clients = new HashMap<>();
    }

    public synchronized void addClient(TcpConnection tcpConnection) {
        if (clients.size() < connectionLimit) {
            ClientConnection clientConnection = new ClientConnection(tcpConnection, nextId, this, new Gson());
            tcpConnection.send(new Message(nextId,MessageType.HelloTcp,"Your ID is : " + nextId));
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
}

class ClientConnection implements Runnable {

    private TcpConnection tcpConnection;
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
                if(message.clientName != id){
                    tcpConnection.send(new Message(id, MessageType.Rejected, "Wrong ID"));
                    break;
                }
                if(message.type == MessageType.MsgTcp){
                    connectionManager.sendTcpMsg(id, message.value);
                }
                else if(message.type == MessageType.Quit){
                    break;
                }
                else{
                    tcpConnection.send(new Message(id, MessageType.Rejected, "Wrong Type"));
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

    public void sendTcoMsg(Message msg) {
        tcpConnection.send(msg);
    }

    public synchronized void close() {
        if(!closed){
            try {
                tcpConnection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        closed = true;
    }
}
