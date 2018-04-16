package lab3;

import com.rabbitmq.client.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.TimeoutException;

public class QueueClient {

    protected Consumer infoConsumer;

    protected Channel channel;

    protected BufferedReader br;

    public QueueClient() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    void initChannel() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        Connection connection = factory.newConnection();
        channel = connection.createChannel();
        channel.basicQos(1);
        channel.exchangeDeclare(Constants.EXCHANGE_NAME_TOPIC, BuiltinExchangeType.TOPIC);
        channel.exchangeDeclare(Constants.EXCHANGE_NAME_INFO, BuiltinExchangeType.FANOUT);
        infoConsumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println("[INFO] Received: " + message);
            }
        };
    }

    void initInfoQueue() throws IOException {
        String infoQueue = channel.queueDeclare().getQueue();
        System.out.println("created id queue: " + infoQueue);
        channel.queueBind(infoQueue, Constants.EXCHANGE_NAME_INFO, "");
        channel.basicConsume(infoQueue, true, infoConsumer);
    }

}
