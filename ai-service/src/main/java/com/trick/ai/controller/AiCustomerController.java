package com.trick.ai.controller;

import com.trick.ai.tools.BingCrawlerTools;
import com.trick.ai.vector.SharedVectorStore;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/wx/ai")
public class AiCustomerController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private BingCrawlerTools bingCrawlerTools;
    @Autowired
    private SharedVectorStore sharedVectorStore;

    /**
     * 与ai客服聊天
     *
     * @param userId    用户ID（暂定）
     * @param userInput 用户输入
     * @return AI输出
     */
    @PostMapping(value = "/customer/{userId}", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCustomer(@PathVariable("userId") String userId, @RequestBody String userInput) {
        return chatClient.prompt()
                .tools(bingCrawlerTools)
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(userId)
                        .build())
                .advisors(QuestionAnswerAdvisor.builder(sharedVectorStore.getVectorStore())
                        .build())
                .user(userInput)
                .stream()
                .content();
    }

    /**
     * 获取用户的历史记录
     *
     * @param userId 用户ID（暂定）
     * @return 记录
     */
    @GetMapping("/customer/{userId}")
    public List<Message> getChatHistory(@PathVariable("userId") String userId) {
        return chatMemory.get(userId);
    }
}
