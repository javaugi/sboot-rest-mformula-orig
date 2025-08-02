/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;

public class BatchResumeProcessor {
    private static final String INPUT_DIR = "resumes/";
    private static final String OUTPUT_DIR = "outputres/";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4); // 4 threads
    
    @Autowired
    LLMService lLMService;

    public static void main(String[] args) throws IOException {
        BatchResumeProcessor main = new BatchResumeProcessor();
        main.doBatchProcessing();
    }

    private void doBatchProcessing() throws IOException {
        List<Path> resumeFiles = listFiles(INPUT_DIR);
        List<Future<JsonNode>> futures = resumeFiles.stream()
                .map(file -> executor.submit(() -> processResume(file)))
                .collect(Collectors.toList());
        
        // Wait for all tasks to complete
        for (Future<JsonNode> future : futures) {
            try {
                JsonNode result = future.get();
                System.out.println("Processed: " + result.get("name").asText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }

    private static List<Path> listFiles(String dir) throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(dir))) {
            return paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".pdf") || p.toString().endsWith(".docx"))
                .collect(Collectors.toList());
        }
    }

    private JsonNode processResume(Path file) {
        try {
            String text = FileParser.extractText(file.toString());
            JsonNode llmData = lLMService.extractStructuredData(text);
            JsonNode nlpEnhancedData = OpenNLPPostNLPService.enrichWithNLP(llmData);
            
            // Save to output dir
            String outputPath = OUTPUT_DIR + file.getFileName().toString().replace(".pdf", ".json");
            new ObjectMapper().writeValue(new File(outputPath), nlpEnhancedData);
            
            return nlpEnhancedData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process " + file, e);
        }
    }
}

/*
could you also add code for multi-resume processing, NLP post-processing and also use LangChain4J  example
Here's a complete Java implementation that includes:

Multi-resume batch processing

NLP post-processing (using OpenNLP)

LangChain4J integration for LLM calls

Structured JSON output

📌 Dependencies (pom.xml)
xml
<dependencies>
    <!-- File Processing -->
    <dependency>
        <groupId>org.apache.poi</groupId>
        <artifactId>poi-ooxml</artifactId>
        <version>5.2.5</version>
    </dependency>
    <dependency>
        <groupId>org.apache.pdfbox</groupId>
        <artifactId>pdfbox</artifactId>
        <version>3.0.0</version>
    </dependency>

    <!-- NLP -->
    <dependency>
        <groupId>org.apache.opennlp</groupId>
        <artifactId>opennlp-tools</artifactId>
        <version>2.3.0</version>
    </dependency>

    <!-- LangChain4J -->
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j</artifactId>
        <version>0.25.0</version>
    </dependency>
    <dependency>
        <groupId>dev.langchain4j</groupId>
        <artifactId>langchain4j-open-ai</artifactId>
        <version>0.25.0</version>
    </dependency>

    <!-- JSON & HTTP -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>
    <dependency>
        <groupId>org.apache.httpcomponents</groupId>
        <artifactId>httpclient</artifactId>
        <version>4.5.13</version>
    </dependency>

    <!-- Parallel Processing -->
    <dependency>
        <groupId>com.google.guava</groupId>
        <artifactId>guava</artifactId>
        <version>31.1-jre</version>
    </dependency>
</dependencies>
📜 Java Code
1. Multi-Resume Batch Processor
java
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BatchResumeProcessor {
    private static final String INPUT_DIR = "resumes/";
    private static final String OUTPUT_DIR = "output/";
    private static final ExecutorService executor = Executors.newFixedThreadPool(4); // 4 threads

    public static void main(String[] args) throws IOException {
        List<Path> resumeFiles = listFiles(INPUT_DIR);
        List<Future<JsonNode>> futures = resumeFiles.stream()
            .map(file -> executor.submit(() -> processResume(file)))
            .collect(Collectors.toList());

        // Wait for all tasks to complete
        for (Future<JsonNode> future : futures) {
            try {
                JsonNode result = future.get();
                System.out.println("Processed: " + result.get("name").asText());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();
    }

    private static List<Path> listFiles(String dir) throws IOException {
        try (Stream<Path> paths = Files.list(Paths.get(dir))) {
            return paths.filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".pdf") || p.toString().endsWith(".docx"))
                .collect(Collectors.toList());
        }
    }

    private static JsonNode processResume(Path file) {
        try {
            String text = FileParser.extractText(file.toString());
            JsonNode llmData = LLMService.extractStructuredData(text);
            JsonNode nlpEnhancedData = NLPService.enrichWithNLP(llmData);
            
            // Save to output dir
            String outputPath = OUTPUT_DIR + file.getFileName().toString().replace(".pdf", ".json");
            new ObjectMapper().writeValue(new File(outputPath), nlpEnhancedData);
            
            return nlpEnhancedData;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process " + file, e);
        }
    }
}
2. File Parser (PDF/DOCX)
java
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.File;
import java.io.FileInputStream;

public class FileParser {
    public static String extractText(String filePath) throws IOException {
        if (filePath.endsWith(".pdf")) {
            try (PDDocument doc = PDDocument.load(new File(filePath))) {
                return new PDFTextStripper().getText(doc);
            }
        } else if (filePath.endsWith(".docx")) {
            try (XWPFDocument doc = new XWPFDocument(new FileInputStream(filePath))) {
                return new XWPFWordExtractor(doc).getText();
            }
        }
        throw new IllegalArgumentException("Unsupported file format");
    }
}
3. LLM Service (LangChain4J)
java
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LLMService {
    private static final String OPENAI_API_KEY = "your-api-key";
    private static final OpenAiChatModel model = OpenAiChatModel.withApiKey(OPENAI_API_KEY);

    @StructuredPrompt("""
        Extract the following from the resume:
        - Name: {{name}}
        - Email: {{email}}
        - Skills: {{skills}}
        - Education: {{education}}
        - Work Experience: {{work_experience}}
        Return JSON.
    """)
    public static JsonNode extractStructuredData(String resumeText) {
        Prompt prompt = new Prompt(resumeText);
        String llmResponse = model.generate(prompt.text());
        return new ObjectMapper().readValue(llmResponse, JsonNode.class);
    }
}
4. NLP Post-Processing (OpenNLP)
java
import opennlp.tools.namefind.*;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.File;
import java.io.IOException;

public class NLPService {
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
📂 Example Output
json
{
  "name": "John Doe",
  "email": "john.doe@gmail.com",
  "skills": ["Java", "Spring", "AWS"],
  "education": [
    {
      "degree": "BSc Computer Science",
      "university": "Stanford University"
    }
  ],
  "work_experience": [
    {
      "company": "Google",
      "position": "Software Engineer",
      "duration": "2020-2023"
    }
  ],
  "ner_identified_companies": "Google"
}
🔹 Key Features
Batch Processing

Parallelizes resume parsing using a thread pool.

LangChain4J Integration

Structured prompts for consistent JSON output.

NLP Enrichment

Identifies companies/skills using OpenNLP.

Error Handling

Gracefully skips corrupt files.

🚀 Further Enhancements
Resume Scoring: Rank candidates based on skills/experience.

LangChain4J RAG: Augment with job description context.

Validation: Ensure extracted emails/phones are valid.
*/