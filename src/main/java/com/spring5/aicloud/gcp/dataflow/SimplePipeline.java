
package com.spring5.aicloud.gcp.dataflow;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.values.TypeDescriptors;

public class SimplePipeline {
    public static void main(String[] args) {
        var options = PipelineOptionsFactory.fromArgs(args).create();
        options.setJobName("local-dataflow-pipeline");

        Pipeline p = Pipeline.create(options);
        String subscription = System.getenv().getOrDefault("PUBSUB_SUBSCRIPTION", "projects/demo-project/subscriptions/observability-sub");

        p.apply("ReadFromPubSub", PubsubIO.readStrings().fromSubscription(subscription))
         .apply("Transform", MapElements.into(TypeDescriptors.strings()).via(s -> s.toUpperCase()))
         .apply("Print", MapElements.into(TypeDescriptors.strings()).via(s -> { System.out.println(s); return s; }));

        p.run().waitUntilFinish();
    }
}
