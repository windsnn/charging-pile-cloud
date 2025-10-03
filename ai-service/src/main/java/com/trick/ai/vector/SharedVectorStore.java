package com.trick.ai.vector;

import lombok.Getter;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

@Component
@Getter
public class SharedVectorStore {
    private final SimpleVectorStore vectorStore;

    public SharedVectorStore(EmbeddingModel embeddingModel) {
        this.vectorStore = SimpleVectorStore.builder(embeddingModel).build();
    }
}