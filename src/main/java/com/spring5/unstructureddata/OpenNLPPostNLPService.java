/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import opennlp.tools.namefind.*;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;

/*
Key Features
Batch Processing

Parallelizes resume parsing using a thread pool.

LangChain4J Integration

Structured prompts for consistent JSON output.

NLP Enrichment

Identifies companies/skills using OpenNLP.

Error Handling

Gracefully skips corrupt files.
*/
public class OpenNLPPostNLPService {
    private static final SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;

    public static JsonNode enrichWithNLP(JsonNode data) throws IOException {
        ObjectNode enriched = (ObjectNode) data;
        
        // Extract entities using NLP
        String text = data.get("work_experience").asText();
        String[] tokens = tokenizer.tokenize(text);
        
        // Load NER models (e.g., for skills)
        NameFinderME nameFinder = new NameFinderME(
            new TokenNameFinderModel(new File("en-ner-person.bin"))
        );
        
        Span[] names = nameFinder.find(tokens);
        if (names.length > 0) {
            enriched.put("ner_identified_companies", tokens[names[0].getStart()]);
        }
        
        return enriched;
    }
}