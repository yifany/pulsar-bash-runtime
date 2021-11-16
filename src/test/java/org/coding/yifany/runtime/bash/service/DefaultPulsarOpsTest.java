package org.coding.yifany.runtime.bash.service;

import org.apache.pulsar.client.api.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.UUID;

@RunWith(JUnit4.class)
public class DefaultPulsarOpsTest {

    private String messageBrokerList = "pulsar://localhost:6650";

    private String inputTopic = "persistent://public/default/bash-runtime-input-1";
    private String outputTopic = "persistent://public/default/bash-runtime-output-1";
    private String logTopic = "persistent://public/default/bash-runtime-logs-1";

    @Test
    public void invokeLocal() throws PulsarClientException {
        String message = UUID.randomUUID().toString();

        PulsarClient
                client = this.pulsarClient();
        DefaultPulsarOps pulsarOps = new DefaultPulsarOps(client);

        Producer<byte[]> producer =
                pulsarOps.createProducer(this.inputTopic);
        producer.send(message.getBytes());
        producer.close();

        Consumer<byte[]> consumer =
                pulsarOps.createConsumer("bash-runtime-output-subscription", Arrays.asList(this.outputTopic, this.logTopic));
        while (true) {
            Message msg = consumer.receive();
            try {
                String actual = new String(msg.getData());

                if (actual.equals(String.format("%s!", message))) {
                    break;
                } else
                    System.out.println(actual);

                consumer.acknowledge(msg);
            } catch (Exception e) {
                consumer.negativeAcknowledge(msg);
            }
        }
        consumer.close();

        client.close();
    }

    private PulsarClient pulsarClient() throws PulsarClientException {
        return PulsarClient.builder()
                .serviceUrl(this.messageBrokerList)
                .build();
    }
}