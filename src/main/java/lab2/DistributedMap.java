package lab2;

import org.jgroups.*;
import org.jgroups.protocols.*;
import org.jgroups.protocols.pbcast.GMS;
import org.jgroups.protocols.pbcast.NAKACK2;
import org.jgroups.protocols.pbcast.STABLE;
import org.jgroups.protocols.pbcast.STATE_TRANSFER;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DistributedMap extends ReceiverAdapter implements SimpleStringMap {

    private final Map<String, String> map;
    private JChannel jChannel;

    DistributedMap(String channelName, String multicastAddress) throws Exception {
        map = new ConcurrentHashMap<>();
        System.setProperty("java.net.preferIPv4Stack", "true");
        jChannel = new JChannel(false);
        ProtocolStack protocolStack = new ProtocolStack();
        jChannel.setProtocolStack(protocolStack);
        protocolStack.addProtocol(new UDP().setValue("mcast_group_addr", InetAddress.getByName(multicastAddress)))
                .addProtocol(new PING())
                .addProtocol(new MERGE3())
                .addProtocol(new FD_SOCK())
                .addProtocol(new FD_ALL().setValue("timeout", 12000).setValue("interval", 3000))
                .addProtocol(new VERIFY_SUSPECT())
                .addProtocol(new BARRIER())
                .addProtocol(new NAKACK2())
                .addProtocol(new UNICAST3())
                .addProtocol(new STABLE())
                .addProtocol(new GMS())
                .addProtocol(new UFC())
                .addProtocol(new MFC())
                .addProtocol(new STATE_TRANSFER())
                .addProtocol(new FRAG2());
        protocolStack.init();
        jChannel.setReceiver(this);
        jChannel.connect(channelName);
        jChannel.getState(null, 10000);
    }

    @Override
    public boolean containsKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public String get(String key) {
        return map.get(key);
    }

    @Override
    public String put(String key, String value) {
        update(new Operation(Operation.Type.PUT, key, value));
        return map.put(key, value);
    }

    @Override
    public String remove(String key) {
        update(new Operation(Operation.Type.DELETE, key, null));
        return map.remove(key);
    }

    private void update(Operation operation) {
        try {
            Message message = new Message(null, null, operation);
            jChannel.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Message msg) {
        System.out.println("RECEIVE");
        Operation operation = (Operation) msg.getObject();
        switch (operation.getType()) {
            case PUT:
                map.put(operation.getKey(), operation.getValue());
                break;
            case DELETE:
                map.remove(operation.getKey());
                break;
        }
    }

    @Override
    public void getState(OutputStream output) throws Exception {
        System.out.println("Get State");
        Util.objectToStream(map, new DataOutputStream(output));
    }

    @Override
    public void setState(InputStream input) throws Exception {
        System.out.println("Set State");
        Map<String, String> map = (Map<String, String>) Util.objectFromStream(new DataInputStream(input));
        this.map.clear();
        this.map.putAll(map);

    }

    @Override
    public void viewAccepted(View view) {
        System.out.println("view Accepted");
        if (view instanceof MergeView) {
            ViewHandler handler = new ViewHandler(jChannel, (MergeView) view);
            handler.start();
        }
    }
}
