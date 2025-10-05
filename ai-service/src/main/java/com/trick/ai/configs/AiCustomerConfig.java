package com.trick.ai.configs;

import com.trick.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.template.st.StTemplateRenderer;
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

    @Bean
    public PromptTemplate aiCustomerPromptTemplate() {
        return PromptTemplate.builder()
                .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .template("""
                        <query>
                        
                        以下是上下文信息。
                        
                        ---------------------
                        <question_answer_context>
                        ---------------------
                        
                        请仅根据以上上下文信息，在没有任何先验知识的情况下回答问题。
                        
                        遵循以下规则：
                        
                        1. 如果答案不在上下文中，就回答“很抱歉，我无法回答您的问题”。
                        2. 避免使用类似“根据上下文…”或“提供的信息显示…”这样的表述。
                        
                        """)
                .build();
    }
}
