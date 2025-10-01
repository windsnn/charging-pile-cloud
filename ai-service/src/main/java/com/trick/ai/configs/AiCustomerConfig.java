package com.trick.ai.configs;

import com.trick.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiCustomerConfig {
    @Autowired
    private DateTimeTools dateTimeTools;

    @Bean
    public ChatClient aiCustomerChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("你是一个客服")
                .defaultTools(dateTimeTools)
                .build();
    }
}
