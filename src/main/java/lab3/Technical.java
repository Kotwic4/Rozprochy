package lab3;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Technical extends QueueClient {

    private String[] specializations;

    private void getSpecializations() throws IOException {
        System.out.println("Specializations:");
        specializations = br.readLine().split(" ");
    }

    private void initTechnicalQueue() throws IOException {
        for (String specialiation : specializations) {
            String key = "*." + specialiation.toLowerCase();
            String queue = channel.queueDeclare(key, false, false, false, null).getQueue();
            System.out.println("created key queue: " + queue);
            channel.queueBind(queue, Constants.EXCHANGE_NAME_TOPIC, key);
            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println("Received: " + message);
                    String[] splittedMessage = message.split(" ");
                    System.out.print("Handling message");
                    String reply = splittedMessage[1] + " " + splittedMessage[0] + " done";
                    channel.basicPublish(Constants.EXCHANGE_NAME_TOPIC, envelope.getRoutingKey().replace("." + splittedMessage[0], ""), null, reply.getBytes("UTF-8"));
                    System.out.println("Sent: " + reply);
                }
            };
            channel.basicConsume(queue, true, consumer);
        }
    }

    private void initQueue() throws IOException {
        initInfoQueue();
        initTechnicalQueue();
        System.out.println("Waiting for messages...");
    }

    public void run() throws IOException, TimeoutException {
        System.out.println("Technical");
        getSpecializations();
        initChannel();
        initQueue();
    }

    public static void main(String[] argv) throws Exception {
        Technical technical = new Technical();
        technical.run();
    }
}
