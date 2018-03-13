package lab1.server;

import com.google.gson.Gson;
import lab1.util.Message;
import lab1.util.MessageType;
import lab1.util.UdpServer;

import java.io.IOException;
import java.net.DatagramPacket;

public class UdpChannel extends Thread {
    private final UdpServer udpServer;
    private final ConnectionManager connectionManager;
    private final Gson gson;

    public UdpChannel(UdpServer udpServer, ConnectionManager connectionManager, Gson gson) {
        this.udpServer = udpServer;
        this.connectionManager = connectionManager;
        this.gson = gson;
    }

    @Override
    public void run() {
        try {
            while (true) {
                DatagramPacket datagramPacket = udpServer.recv();
                String msg = new String(datagramPacket.getData());
                msg = msg.trim();
                System.out.println("UDP :" + msg);
                Message message = gson.fromJson(msg,Message.class);
                if(message.type == MessageType.HelloUdp){
                    connectionManager.addUdpClient(message.clientName,datagramPacket);
                }
                if(message.type == MessageType.MsgUdp) {
                    connectionManager.sendUdpMsg(message.clientName, message.value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
