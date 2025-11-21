package de.eichstaedt.ai.application;

import dev.langchain4j.model.input.PromptTemplate;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by konrad.eichstaedt@gmx.de on 21.11.25.
 */

@Slf4j
public class PromptEngine {

  public static String createPromptWithVariables(Map<String, String> values) {
    String prompt = "";
    try {
      prompt = PromptTemplate.from(Files.readString(Path.of("src/main/resources/system-prompt.st")))
          .apply(Map.of("language", "Deutsch")).text();
    } catch (IOException e) {
      log.error("Error during creating prompt {}", e.getMessage(), e);
    }

    log.info("Creating prompt {}", prompt);
    return prompt;
  }
}
