package de.eichstaedt.ai.application;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * Created by konrad.eichstaedt@gmx.de on 28.10.25.
 */

@Slf4j
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

  @GetMapping(value = "/ai/generate-reactive", produces = MediaType.TEXT_PLAIN_VALUE)
  public Flux<String> generateReactive(
      @RequestParam(value = "message", defaultValue = "Erzähle einen guten Witz") String message) {

    List<ChatMessage> messages = List.of(
        SystemMessage.from(PromptEngine.createPromptWithVariables(Map.of("language", "Deutsch"))),
        UserMessage.from(message)
    );

    ChatRequest request = ChatRequest.builder()
        .messages(messages)
        .maxOutputTokens(1000)
        .build();

    return Flux.create(sink -> streamingChatModel.chat(request, new StreamingChatResponseHandler() {

      @Override
      public void onPartialResponse(String s) {
        sink.next(s);
        log.info("Partial response: {}", s);
      }

      @Override
      public void onCompleteResponse(ChatResponse chatResponse) {
        sink.complete();
      }

      @Override
      public void onError(Throwable error) {
        sink.error(error);
      }
    }));
  }
}
