package com.trick.ai.vector;

import lombok.Getter;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
public class SharedVectorStore {
    private final SimpleVectorStore vectorStore;

    SharedVectorStore(@Value("classpath:ai-customer.txt") Resource resource, EmbeddingModel embeddingModel) {
        //获取
        TextReader textReader = new TextReader(resource);
        //分割
        List<Document> documents = textReader.get();
        List<Document> splitDocuments = new TokenTextSplitter().apply(documents);
        //写入向量数据库
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
        this.vectorStore.doAdd(splitDocuments);
    }
}