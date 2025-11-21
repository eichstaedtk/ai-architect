package de.eichstaedt.ai.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Created by konrad.eichstaedt@gmx.de on 21.11.25.
 */
public class PromptEngineTest {

  @Test
  void createPrompt() {
    String prompt = PromptEngine.createPromptWithVariables(
        Map.of("language", "Deutsch", "format", "HTML"));
    assertNotNull(prompt);
  }
}
