package org.coding.yifany.runtime.bash.service;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;

import java.util.List;

public class DefaultPulsarOps {

    private PulsarClient client;

    public DefaultPulsarOps(PulsarClient client) {
        this.client = client;
    }

    public Producer<byte[]> createProducer(String topic) throws PulsarClientException {
        return this.client.newProducer()
                .topic(topic)
                .create();
    }

    public Consumer<byte[]> createConsumer(String subscriptionName, List<String> topicList) throws PulsarClientException {
        return this.client.newConsumer()
                .topics(topicList)
                .subscriptionName(subscriptionName)
                .subscribe();
    }
}
