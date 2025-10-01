package de.eichstaedt.ai.knowledgebase;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created by konrad.eichstaedt@gmx.de on 01.10.25.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class DocumentIngestingService {


  public void ingestDocuments() {

    DocumentParser parser = new TextDocumentParser();
    Path documentsPath = Paths.get("./");

    List<Document> documents = FileSystemDocumentLoader.loadDocuments(
        documentsPath,
        parser
    );
  }
}
