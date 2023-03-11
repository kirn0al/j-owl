package com.github.pawawudaf.jowl.index;

import com.github.pawawudaf.jowl.parse.ParsedData;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class IndexService {

    private final IndexWriter indexWriter;

    public IndexService() {
        final Analyzer analyzer = new StandardAnalyzer();
        final IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try {
            Path indexPath = Files.createTempDirectory("tempIndex");
            Directory directory = FSDirectory.open(indexPath);
            indexWriter = new IndexWriter(directory, config);
        } catch (IOException e) {
            throw new TempDirectoryCreationException("Error during creating temp directory", e);
        }
    }

    public void indexDocuments(ParsedData data) {
        try {
            indexWriter.addDocuments(createDocuments(data));
        } catch (IOException e) {
            throw new IndexingException("Error indexing documents", e);
        }
    }

    private List<Document> createDocuments(ParsedData data) {
        return data.getDataEntrySet().stream()
            .map(entry -> {
                Document document = new Document();
                document.add(new TextField("key", entry.getKey(), Field.Store.YES));
                document.add(new TextField("value", entry.getValue(), Field.Store.YES));
                return document;
            })
            .toList();
    }

    private static final class TempDirectoryCreationException extends RuntimeException {

        public TempDirectoryCreationException(String message, Exception exception) {
            super(message, exception);
        }
    }

    private static final class IndexingException extends RuntimeException {

        public IndexingException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
