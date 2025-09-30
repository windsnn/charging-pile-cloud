package com.trick.ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/wx/ai")
public class AiCustomerController {
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;

    @GetMapping(value = "/customer/{userId}", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getCustomer(@PathVariable("userId") String userId, @RequestBody String userInput) {
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(userId)
                        .build())
                .user(userInput)
                .stream()
                .content();
    }
}
