package com.trick.ai.configs;

import com.trick.ai.tools.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.List;

@Configuration
public class AiCustomerConfig {
    @Autowired
    private DateTimeTools dateTimeTools;

    /**
     * AI客服对话模型
     *
     * @param chatModel AI模型
     * @return 对话客户端
     */
    @Bean
    public ChatClient aiCustomerChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        你是一名专业的在线客服，负责耐心解答用户的问题
                        要求：
                        - 使用简洁、礼貌、友好的语气。
                        - 如果问题涉及公司产品或服务，优先从知识库中检索答案。
                        - 如果无法回答，请明确告知并建议转人工客服。
                        - 回答时避免使用过于专业的术语，确保用户容易理解。
                        """)
                .defaultTools(dateTimeTools)
                .build();
    }

    /**
     * 知识数据库
     *
     * @param resource       数据库文档
     * @param embeddingModel 嵌入式模型
     * @return 知识数据库
     */
    @Bean
    public SimpleVectorStore sharedVectorStore(@Value("classpath:ai-customer.txt") Resource resource, EmbeddingModel embeddingModel) {
        //获取
        TextReader textReader = new TextReader(resource);
        //分割
        List<Document> documents = textReader.get();
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        //写入向量数据库
        SimpleVectorStore shareVectorStore = SimpleVectorStore.builder(embeddingModel).build();
        shareVectorStore.doAdd(splitDocuments);

        return shareVectorStore;
    }

    /**
     * Ai客服知识数据库系统提示词
     *
     * @return 知识数据库提示词
     */
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
