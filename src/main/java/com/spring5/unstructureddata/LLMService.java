/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.unstructureddata;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@StructuredPrompt(
        """
          Extract the following from the resume:
        - Name: {{name}}
        - Email: {{email}}
        - Skills: {{skills}}
        - Education: {{education}}
        - Work Experience: {{work_experience}}
        Return JSON.
    """)
@Service
public class LLMService {
    // private static final OpenAiChatModel model = OpenAiChatModel.withApiKey(OPENAI_API_KEY);

    @Autowired
    private @Qualifier("deekseekOpenAiChatModel")
    OpenAiChatModel model;

    public JsonNode extractStructuredData(String resumeText) throws Exception {
        Prompt prompt = new Prompt(resumeText);
        String llmResponse = model.call(prompt.text());
        return new ObjectMapper().readValue(llmResponse, JsonNode.class);
    }

    public JsonNode extractStructuredData2(String resumeText) throws Exception {
        // Create structured prompt
        ResumeExtractionPrompt prompt = new ResumeExtractionPrompt(resumeText);
        String structuredPrompt = StructuredPromptProcessor.toPrompt(prompt).text();

        // Call LLM
        String llmResponse = model.call(structuredPrompt);
        return new ObjectMapper().readValue(llmResponse, JsonNode.class);
    }
}
