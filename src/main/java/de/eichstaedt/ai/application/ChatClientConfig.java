package de.eichstaedt.ai.application;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Bean
  public OllamaChatModel chatModel() {
    return OllamaChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("codellama:7b")
        .temperature(0.9)
        .build();
  }

  @Bean
  public OllamaStreamingChatModel streamingChatModel() {
    return OllamaStreamingChatModel.builder()
        .baseUrl("http://localhost:11434")
        .modelName("codellama:7b")
        .temperature(0.9)
        .build();
  }
}