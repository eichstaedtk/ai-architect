package de.eichstaedt.ai.application;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/**
 * Created by konrad.eichstaedt@gmx.de on 21.11.25.
 */
class PromptEngineTest {

  @Test
  void createPrompt() {
    String prompt = PromptEngine.createPromptWithVariables();
    assertNotNull(prompt);
  }
}
