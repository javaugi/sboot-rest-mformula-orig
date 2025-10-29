
package com.spring5.aicloud.gcp.dataflow;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.google.pubsub.v1.PubsubMessage;
import com.google.protobuf.ByteString;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

@Service
public class PubSubPublisher {

    private final Publisher publisher;

    public PubSubPublisher() throws IOException {
        String projectId = System.getenv().getOrDefault("GCP_PROJECT", "demo-project");
        String topicId = System.getenv().getOrDefault("PUBSUB_TOPIC", "observability-topic");
        ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
        this.publisher = Publisher.newBuilder(topicName).build();
    }

    public String publish(String message) {
        try {
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
            ApiFuture<String> future = publisher.publish(pubsubMessage);
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
