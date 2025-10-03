package com.trick.ai.vector;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
class MyTextReader {
    private final Resource resource;

    @Autowired
    private SharedVectorStore sharedVectorStore;

    MyTextReader(@Value("classpath:ai-customer.txt") Resource resource) {
        this.resource = resource;
    }

    @PostConstruct
    void loadText() {
        //获取
        TextReader textReader = new TextReader(this.resource);

        //分割
        List<Document> documents = textReader.get();
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);

        //写入向量数据库
        sharedVectorStore.getVectorStore().doAdd(splitDocuments);
    }
}