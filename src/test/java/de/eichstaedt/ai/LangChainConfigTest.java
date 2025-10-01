package de.eichstaedt.ai;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * Created by konrad.eichstaedt@gmx.de on 01.10.25.
 */

@SpringBootTest
@ContextConfiguration(classes = LangChainConfig.class)
public class LangChainConfigTest {

  @Autowired
  private LangChainConfig langChainConfig;

  @Test
  void testCreationLangChainConfig() {
    assertNotNull(langChainConfig);
  }
}
