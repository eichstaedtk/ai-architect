package de.eichstaedt.ai.knowledgebase;

import java.time.LocalDateTime;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

/**
 * Created by konrad.eichstaedt@gmx.de on 01.10.25.
 */
@Data
@Builder
public class ArchitectureDocument {

  private String id;
  private String title;
  private String content;
  private String category;
  private String source;
  private Map<String, String> metadata;
  private LocalDateTime createdAt;
}

