package de.eichstaedt.ai.application;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialResponse;
import dev.langchain4j.model.chat.response.PartialResponseContext;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.One;

/**
 * Created by konrad.eichstaedt@gmx.de on 28.10.25.
 */

@Slf4j
@RestController
public class ChatController {

  private final OllamaStreamingChatModel streamingChatModel;

  private final ConcurrentHashMap<String, One<Void>> stopSignals = new ConcurrentHashMap<>();

  @Autowired
  public ChatController(OllamaStreamingChatModel streamingChatModel) {
    this.streamingChatModel = streamingChatModel;
  }

  @PostMapping(value = "/ai/askthellm", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<Flux<String>> askTheLLM(@RequestBody String message) {
    String requestId = java.util.UUID.randomUUID().toString();

    Sinks.One<Void> stopSignal = Sinks.one();
    stopSignals.put(requestId, stopSignal);
    HttpHeaders headers = new HttpHeaders();
    headers.add("X-Request-ID", requestId);

    ChatRequest request = createChatRequest(message);
    Flux<String> flux = createFluxResponse(request, requestId)
        // Stoppt den Flux sobald stopSignal getriggert wird
        .takeUntilOther(stopSignal.asMono());

    return ResponseEntity.ok()
        .headers(headers)
        .contentType(MediaType.TEXT_PLAIN)
        .body(flux);
  }

  @PostMapping("/ai/stop/{requestId}")
  public void stopRequest(@PathVariable String requestId) {
    Sinks.One<Void> stopSignal = stopSignals.remove(requestId);
    if (stopSignal != null) {
      stopSignal.tryEmitEmpty();
    }
  }

  @NotNull
  private Flux<String> createFluxResponse(ChatRequest request, String requestId) {
    return Flux.create(sink -> {
      // Executor starten
      var executor = java.util.concurrent.Executors.newSingleThreadExecutor();
      var future = executor.submit(
          () -> streamingChatModel.chat(request, new StreamingChatResponseHandler() {
            @Override
            public void onPartialResponse(
                PartialResponse partialResponse, PartialResponseContext context) {
              if (!stopSignals.containsKey(requestId)) {
                context.streamingHandle().cancel();
              }
              log.info("Partial : {}", partialResponse.text());
              sink.next(partialResponse.text());
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

      // Stop-Signal
      sink.onCancel(() -> {
        stopSignals.remove(requestId);
        future.cancel(true);
        executor.shutdownNow();
      });
    }, OverflowStrategy.BUFFER);
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
