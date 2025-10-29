package com.spring5.aicloud.gcp.dataflow;

import com.google.api.services.bigquery.model.TableRow;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.PCollection;

public class DataflowPipeline {

    public interface Options extends PipelineOptions {
        @Description("Input Pub/Sub topic")
        ValueProvider<String> getInputTopic();

        void setInputTopic(ValueProvider<String> value);

        @Description("BigQuery table")
        ValueProvider<String> getOutputTable();

        void setOutputTable(ValueProvider<String> value);
    }

    public static void main(String[] args) {
        Options options = PipelineOptionsFactory.fromArgs(args).as(Options.class);
        Pipeline pipeline = Pipeline.create(options);

        // Read from Pub/Sub
        PCollection<String> messages = pipeline
            .apply("Read from Pub/Sub", PubsubIO.readStrings().fromTopic(options.getInputTopic()));

        // Transform and write to BigQuery
        messages
            .apply("Transform Data", ParDo.of(new TransformFn()))
            .apply("Write to BigQuery", BigQueryIO.writeTableRows()
                .to(options.getOutputTable())
                .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

        pipeline.run();
    }

    static class TransformFn extends DoFn<String, TableRow> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            String message = c.element();
            // Simple transformation - in real scenario, parse JSON and transform
            TableRow row = new TableRow()
                .set("message", message)
                .set("processed_at", new java.util.Date().toString())
                .set("length", message.length());
            
            c.output(row);
        }
    }
}