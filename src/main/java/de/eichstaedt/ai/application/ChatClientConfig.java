package de.eichstaedt.ai.application;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

  @Value("${ollama.base-url}")
  private String baseUrl;

  @Value("${ollama.model-name}")
  private String modelName;

  @Value("${ollama.temperature}")
  private Double temperature;

  @Value("${ollama.chat-memory.max-messages:10}")
  private Integer maxMessages;

  @Bean
  public OllamaChatModel chatModel() {
    return OllamaChatModel.builder()
        .baseUrl(baseUrl)
        .modelName(modelName)
        .temperature(temperature)
        .build();
  }

  @Bean
  public OllamaStreamingChatModel streamingChatModel() {
    return OllamaStreamingChatModel.builder()
        .baseUrl(baseUrl)
        .modelName(modelName)
        .temperature(temperature)
        .build();
  }

  @Bean
  public ChatMemory chatMemory() {
    return MessageWindowChatMemory.withMaxMessages(maxMessages);
  }
}