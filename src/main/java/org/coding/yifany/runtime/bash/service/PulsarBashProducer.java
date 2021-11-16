package org.coding.yifany.runtime.bash.service;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

public class PulsarBashProducer {

    public static void main(String[] args) throws PulsarClientException, InterruptedException {
        String topic = "persistent://public/default/bash-runtime-input-1";
        String messageBrokerList = "pulsar://localhost:6650";

        PulsarClient client = PulsarClient.builder()
                .serviceUrl(messageBrokerList)
                .build();

        Producer<byte[]> producer = client.newProducer()
                .topic(topic)
                .create();

        for (int i = 0; i < 10; i++) {
            producer.send(String.format("hw sent to topic [%s]", topic).getBytes());
            Thread.sleep(500);
        }
        producer.close();
        client.close();
    }
}
