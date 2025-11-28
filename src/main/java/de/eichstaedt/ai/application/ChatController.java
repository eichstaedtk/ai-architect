package de.eichstaedt.ai.application;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;

/**
 * Created by konrad.eichstaedt@gmx.de on 28.10.25.
 */

@Slf4j
@RestController
public class ChatController {

  private final OllamaStreamingChatModel streamingChatModel;

  @Autowired
  public ChatController(OllamaStreamingChatModel streamingChatModel) {
    this.streamingChatModel = streamingChatModel;
  }

  @PostMapping(value = "/ai/askthellm", produces = MediaType.TEXT_PLAIN_VALUE)
  public Flux<String> askTheLLM(@RequestBody String message) {
    ChatRequest request = createChatRequest(message);
    return createFluxResponse(request);
  }

  @NotNull
  private Flux<String> createFluxResponse(ChatRequest request) {
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
        log.error("Error during chat with llm", error);
        sink.error(error);
      }
    }), OverflowStrategy.BUFFER);
  }

  private static ChatRequest createChatRequest(String message) {
    List<ChatMessage> messages = List.of(
        SystemMessage.from(PromptEngine.createPromptWithVariables()),
        UserMessage.from(message)
    );

    return ChatRequest.builder()
        .messages(messages)
        .maxOutputTokens(1000)
        .build();
  }
}
