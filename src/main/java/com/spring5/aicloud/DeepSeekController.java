/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.aicloud;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 *
 * @author javaugi
 */
@RestController
@RequestMapping("/api/deepseek")
public class DeepSeekController {
    private static final Logger log = LoggerFactory.getLogger(DeepSeekController.class);
    
    @Autowired
    private @Qualifier("deekseekOpenAiChatModel") OpenAiChatModel chatModel;
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    //Send a POST request (e.g., using curl or Postman):
    //curl -X POST http://localhost:8080/api/deepseek/chat \
    // -H "Content-Type: text/plain" \
    // -d "Explain quantum computing in simple terms." 

    @PostMapping("/chat") //this one does not seem to work
    public String chat(@RequestBody String userPrompt) {
        //userPrompt = ML_Q;
        return deepSeekService.getChatResponse(userPrompt);
    }    

    //- this works though
    @GetMapping("/test") //http://localhost:8080/api/deepseek/test 
    public String chatTest(@RequestParam(value = "userPrompt", defaultValue = "Tell me a joke") String userPrompt) {
        //userPrompt = ML_Q;
        log.info("chatTest userPrompt {}", userPrompt);
        return deepSeekService.getChatResponse(userPrompt);
    }    

    //- this works though
    @GetMapping("/test2") //http://localhost:8080/api/deepseek/test2
    public String chatTest2(@RequestParam(value = "userPrompt", defaultValue = "Tell me a joke") String userPrompt) {
        //userPrompt = ML_Q;
        log.info("chatTest2 userPrompt {}", userPrompt);
        return deepSeekService.getAIResponse(userPrompt);
    }    
 
    //- this DOES NOT works though
    @GetMapping("/chatgen")
    public Map generate(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        //message = ML_Q;
        return Map.of("generation", this.chatModel.call(message));
    }

    //- this DOES NOT works though
    @GetMapping("/stream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        //message = ML_Q;
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }    
    
    /*
    ChatResponse response = chatModel.call(
        new Prompt(
        "Generate the names of 5 famous pirates.",
        OpenAiChatOptions.builder()
            .model("deepseek-chat")
            .temperature(0.4)
        .build()
    ));
    // */
}
