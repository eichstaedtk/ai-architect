package de.eichstaedt.ai.application;

import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by konrad.eichstaedt@gmx.de on 28.10.25.
 */

@RestController
public class ChatController {

  private final OllamaChatModel chatModel;

  private final OllamaStreamingChatModel streamingChatModel;

  @Autowired
  public ChatController(OllamaChatModel chatModel, OllamaStreamingChatModel streamingChatModel) {
    this.chatModel = chatModel;
    this.streamingChatModel = streamingChatModel;
  }

  @GetMapping("/ai/generate")
  public Map<String, String> generate(
      @RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
    return Map.of("generation", this.chatModel.chat(message));
  }
}
