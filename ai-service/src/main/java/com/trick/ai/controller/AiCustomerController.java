package com.trick.ai.controller;

import com.trick.ai.tools.BingCrawlerTools;
import com.trick.ai.vector.SharedVectorStore;
import com.trick.common.utils.ThreadLocalUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/wx/ai")
public class AiCustomerController {

    @Autowired
    @Qualifier("aiCustomerChatClient")
    private ChatClient chatClient;

    @Autowired
    @Qualifier("aiCustomerPromptTemplate")
    private PromptTemplate promptTemplate;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private BingCrawlerTools bingCrawlerTools;

    @Autowired
    private SharedVectorStore sharedVectorStore;

    /**
     * 与ai客服聊天
     *
     * @param chatId    对话ID
     * @param userInput 用户输入
     * @return AI输出
     */
    @PostMapping(value = "/customer/{chatId}", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatCustomer(@PathVariable("chatId") String chatId, @RequestBody String userInput) {
        return chatClient.prompt()
                .advisors(MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(ThreadLocalUtil.getUserId() + ":" + chatId)
                        .build())
                .advisors(QuestionAnswerAdvisor.builder(sharedVectorStore.getVectorStore())
                        .promptTemplate(promptTemplate)
                        .build())
                .tools(bingCrawlerTools)
                .user(userInput)
                .stream()
                .content();
    }

    /**
     * 获取用户的历史记录
     *
     * @param chatId 对话ID
     * @return 记录
     */
    @GetMapping("/customer/{chatId}")
    public List<Message> getChatHistory(@PathVariable("chatId") String chatId) {
        return chatMemory.get(ThreadLocalUtil.getUserId() + ":" + chatId);
    }

}
