package de.eichstaedt.ai;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by konrad.eichstaedt@gmx.de on 01.10.25.
 */

@Configuration
public class LangChainConfig {

  @Value("${chatmodel.baseurl}")
  private String baseUrl;

  @Value("${chatmodel.modelname}")
  private String modelName;

  @Value("${chatmodel.temperature}")
  private double temperature = 0.1;

  @Bean
  public ChatModel chatLanguageModel() {
    return OllamaChatModel.builder()
        .baseUrl(baseUrl)
        .modelName(modelName)
        .temperature(temperature)
        .build();
  }

  @Bean
  public EmbeddingStore<TextSegment> embeddingStore() {
    return ChromaEmbeddingStore.builder()
        .baseUrl("http://localhost:8000")
        .collectionName("architecture_kb")
        .build();
  }

  @Bean
  public EmbeddingModel embeddingModel() {
    // Lokales Embedding-Modell (offline verfügbar)
    return new AllMiniLmL6V2EmbeddingModel();
  }

}
