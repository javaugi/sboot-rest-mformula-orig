/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud.gcp.dataflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.pubsub.PubsubIO;
import org.apache.beam.sdk.options.*;
import org.apache.beam.sdk.transforms.*;
import com.google.api.services.bigquery.model.TableRow;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public class PubSubToBigQuery {

    public interface Options extends PipelineOptions {

        @Description("Pub/Sub topic to read from")
        String getInputTopic();

        void setInputTopic(String topic);

        @Description("BigQuery table name")
        String getOutputTable();

        void setOutputTable(String table);
    }

    public static class ParseJsonFn extends DoFn<String, TableRow> {

        private static final ObjectMapper mapper = new ObjectMapper();

        @ProcessElement
        public void processElement(@Element String element, OutputReceiver<TableRow> out) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, String> map = mapper.readValue(element, java.util.Map.class);
                TableRow row = new TableRow();
                map.forEach(row::set);
                /*
                map.forEach((key, value) -> {
                    if (value instanceof String) {
                        row.set((String) value); // Cast if already a String
                    } else {
                        row.set(String.valueOf(value)); // Convert using String.valueOf()
                    }
                });
                // */
                
                row.set("processed_at", java.time.Instant.now().toString());
                out.output(row);
            } catch (JsonProcessingException e) {
                out.output(new TableRow().set("error", e.getMessage()).set("raw", element));
            }
        }
    }

    public static void main(String[] args) {
        Options options = PipelineOptionsFactory.fromArgs(args).withValidation().as(Options.class);
        Pipeline p = Pipeline.create(options);

        p.apply("ReadFromPubSub", PubsubIO.readStrings().fromTopic(options.getInputTopic()))
                .apply("ParseJSON", ParDo.of(new ParseJsonFn()))
                .apply("WriteToBigQuery", BigQueryIO.writeTableRows()
                        .to(options.getOutputTable())
                        .withSchema(new com.google.api.services.bigquery.model.TableSchema().setFields(java.util.List.of(
                                new com.google.api.services.bigquery.model.TableFieldSchema().setName("user").setType("STRING"),
                                new com.google.api.services.bigquery.model.TableFieldSchema().setName("action").setType("STRING"),
                                new com.google.api.services.bigquery.model.TableFieldSchema().setName("timestamp").setType("TIMESTAMP"),
                                new com.google.api.services.bigquery.model.TableFieldSchema().setName("processed_at").setType("TIMESTAMP"),
                                new com.google.api.services.bigquery.model.TableFieldSchema().setName("error").setType("STRING"),
                                new com.google.api.services.bigquery.model.TableFieldSchema().setName("raw").setType("STRING")
                        )))
                        .withCreateDisposition(BigQueryIO.Write.CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(BigQueryIO.Write.WriteDisposition.WRITE_APPEND));

        p.run().waitUntilFinish();
    }
}
