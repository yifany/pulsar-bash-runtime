package org.coding.yifany.runtime.bash.service;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarBashConsumer {

    public static void main(String[] args) throws PulsarClientException {
        String topic = "bash-topic-hw";
        String subscriptionName = "bash-topic-hw-subscription";
        String messageBrokerList = "pulsar://localhost:6650";

        PulsarClient client = PulsarClient.builder()
                .serviceUrl(messageBrokerList)
                .build();

        Consumer consumer = client.newConsumer()
                .topic(topic)
                .subscriptionName(subscriptionName)
                .subscribe();
        int counter = 1;

        while (true) {
            // Wait for a message
            Message msg = consumer.receive();

            try {
                // Do something with the message
                System.out.println("Message received: " + new String(msg.getData()));

                // Acknowledge the message so that it can be deleted by the message broker
                consumer.acknowledge(msg);
            } catch (Exception e) {
                // Message failed to process, redeliver later
                consumer.negativeAcknowledge(msg);
            }
            if (counter == 10) {
                break;
            } else
                counter += 1;
        }
        consumer.close();
        client.close();
    }
}
