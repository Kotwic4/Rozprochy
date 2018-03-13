package lab1.util;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;

public class MulticastConnection {

    private MulticastSocket socket;

    private InetAddress address;

    private int portNumber;

    private Gson gson;

    private MulticastConnection(MulticastSocket socket, int portNumber, InetAddress address, Gson gson) {
        this.socket = socket;
        this.address = address;
        this.portNumber = portNumber;
        this.gson = gson;
    }

    public static MulticastConnection join(int portNumber, String host) throws IOException {
        MulticastSocket socket = new MulticastSocket(portNumber);
        InetAddress address = InetAddress.getByName(host);
        socket.joinGroup(address);
        return new MulticastConnection(socket, portNumber, address, new Gson());
    }

    public void close() throws IOException {
        socket.leaveGroup(address);
        socket.close();
    }

    public void send(String msg) throws IOException {
        byte[] sendBuffer = msg.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, address, portNumber);
        socket.send(sendPacket);
    }

    public void send(Message message) throws IOException {
        String msg = gson.toJson(message);
        send(msg);
    }

    public String recv() throws IOException {
        byte[] receiveBuffer = new byte[1024];
        Arrays.fill(receiveBuffer, (byte) 0);
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        socket.receive(receivePacket);
        return new String(receivePacket.getData()).trim();
    }
}
