package de.eichstaedt.ai.application;

import dev.langchain4j.model.input.PromptTemplate;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Created by konrad.eichstaedt@gmx.de on 21.11.25.
 */

@Slf4j
public class PromptEngine {

  private PromptEngine() {
  }

  public static final Map<String, Object> PROMPT_VARIABLES = Map.of("concepts",
      "Domain Driven Design, Clean Code, Test Driven Development", "role",
      "Softwarearchitekt", "language", "Deutsch", "format", "HTML");

  public static String createPromptWithVariables() {
    String prompt = "";
    log.info("Creating prompt with values {}", PROMPT_VARIABLES);
    try {
      Resource resource = new ClassPathResource("system-prompt.st");
      prompt = PromptTemplate.from(new String(
              resource.getInputStream().readAllBytes(),
              StandardCharsets.UTF_8
          ))
          .apply(PROMPT_VARIABLES).text();
    } catch (IOException e) {
      log.error("Error during creating prompt {}", e.getMessage(), e);
    }

    log.info("Creating prompt {}", prompt);
    return prompt;
  }
}
