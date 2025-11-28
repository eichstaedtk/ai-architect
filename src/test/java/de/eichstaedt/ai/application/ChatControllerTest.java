package de.eichstaedt.ai.application;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Created by konrad.eichstaedt@gmx.de on 28.11.25.
 */
@WebFluxTest(ChatController.class)
@Import(ChatClientConfig.class)
@TestPropertySource(properties = {
    "ollama.base-url=http://localhost:11434",
    "ollama.model-name=test-model",
    "ollama.temperature=0.0"
})
class ChatControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @MockitoBean
  private OllamaStreamingChatModel streamingChatModel;

  @Test
  void testAskTheLLM_shouldStreamTokens() {
    // Arrange
    String userMessage = "Hello, AI!";
    String[] expectedTokens = {"Hello", " ", "from", " ", "AI"};

    // Mock Streaming
    doAnswer(invocation -> {
      StreamingChatResponseHandler handler = invocation.getArgument(1);

      String[] tokens = {"Hello", " ", "from", " ", "AI"};
      for (String token : tokens) {
        handler.onPartialResponse(token);
      }

      handler.onCompleteResponse(
          ChatResponse.builder()
              .aiMessage(AiMessage.from(String.join("", tokens)))
              .build()
      );
      return null;
    }).when(streamingChatModel)
        .chat(any(ChatRequest.class), any(StreamingChatResponseHandler.class));

    // Act & Assert
    webTestClient.post()
        .uri("/ai/askthellm")
        .contentType(MediaType.TEXT_PLAIN)
        .bodyValue(userMessage)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(String.class)
        .hasSize(1)
        .contains("Hello from AI");

    verify(streamingChatModel).chat(any(ChatRequest.class),
        any(StreamingChatResponseHandler.class));
  }
}
