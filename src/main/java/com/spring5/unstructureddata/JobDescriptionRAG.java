/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.util.ArrayList;
import java.util.List;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
/*
2. LangChain4J RAG (Job Description Augmentation) - Concept
Use a job description as context to:
    Improve resume scoring (dynamic weights).
    Generate tailored interview questions.

Example Output (LLM Response)
    text
    1. Score: 85/100 (Strong match in Java/Spring, lacks AWS experience).  
    2. Interview Questions:  
       - "How did you optimize Spring Boot applications in past projects?"  
       - "Describe your experience with microservices."  
       - "What AWS services are you familiar with?"  

Retrieval-augmented generation, or RAG, introduces some serious capabilities to your large language models (LLMs). These applications
    can answer questions about your specific corpus of knowledge, while leveraging all the nuance and sophistication of a traditional LLM.


*/
//https://dev.to/mongodb/how-to-make-a-rag-application-with-langchain4j-1mad#:~:text=LangChain4J%20for%20RAG,components%20in%20your%20AI%20applications.
@Service
public class JobDescriptionRAG {
       
    @Autowired
    private @Qualifier("deekseekOpenAiChatModel") OpenAiChatModel model;

    public static void main(String[] args) {
        JobDescriptionRAG main = new JobDescriptionRAG();
        main.doRAGJobDesc();
    }    

    private void doRAGJobDesc() {
        // Step 1: Load Job Description
        //Document jobDescDoc = Document.from(Path.of("job_desc.txt"), new TextDocumentParser());
        Document jobDescDoc = Document.from("job_desc.txt");
        List<TextSegment> jobDescSegments = DocumentSplitters.recursive(300, 900).split(jobDescDoc);

        // Step 2: Generate Embeddings
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        jobDescSegments.forEach(seg -> {
            embeddingStore.add(embeddingModel.embed(seg).content(), seg);
        });
        
        
        
        // Step 3: Retrieve Relevant Context for a Resume
        String resumeText = "Experienced Java developer with Spring Boot...";
        //EmbeddingStoreRetriever retriever = EmbeddingStoreRetriever.from(embeddingStore, embeddingModel, 1);
        
        EmbeddingSearchRequest reg = EmbeddingSearchRequest.builder().build();
        
        EmbeddingSearchResult<TextSegment> results = embeddingStore.search(reg);
        
        //List<TextSegment> relevantContext = retriever.findRelevant(resumeText);
        List<TextSegment> relevantContext = new ArrayList();
        results.matches().forEach(a -> {
            relevantContext.add(a.embedded());
        });

        // Step 4: Augment LLM Prompt with Job Context
        //OpenAiChatModel llm = OpenAiChatModel.withApiKey(OPENAI_API_KEY);
        String prompt = """
                                            Based on the job description:
                                            {{context}}
                                
                                            Evaluate this resume:
                                            {{resume}}
                                
                                            Tasks:
                                            1. Score the candidate (0-100).
                                            2. Suggest 3 interview questions.
                                            """.replace("{{context}}", relevantContext.get(0).text())
                .replace("{{resume}}", resumeText);
        
        String llmResponse = model.call(prompt);
        System.out.println(llmResponse);
    }    
}

/*
 Key Enhancements
Feature	Implementation
Dynamic Skill Weights	Adjust weights based on job description keywords.
Multi-Model RAG	Combine OpenAI + local embeddings for cost savings.
Bulk Processing	Score 1000s of resumes using parallel streams.
🚀 Full Pipeline
Ingest Resumes → Extract data (LLM + NLP).

Load Job Description → Generate embeddings.

Retrieve Context → Augment scoring/questions.

Rank Candidates → Sort by score + RAG insights.
*/
