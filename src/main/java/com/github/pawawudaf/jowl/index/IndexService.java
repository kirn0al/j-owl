package com.github.pawawudaf.jowl.index;

import com.github.pawawudaf.jowl.parse.ParsedHtmlPage;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class IndexService {

    private final IndexWriter indexWriter;

    public IndexService() {
        try {
            Directory directory = FSDirectory.open(Files.createTempDirectory("tempIndex"));
            indexWriter = new IndexWriter(directory, new IndexWriterConfig(new StandardAnalyzer()));
        } catch (IOException e) {
            throw new TempDirectoryCreationException("Error during creating temp directory", e);
        }
    }

    public void indexDocuments(Map<String, ParsedHtmlPage> data) {
        try {
            indexWriter.addDocuments(createDocuments(data));
            indexWriter.commit();
        } catch (Exception e) {
            throw new IndexingException("Error indexing documents", e);
        }
    }

    public List<IndexDto> getAllIndexedDocuments() {
        try (IndexReader reader = DirectoryReader.open(indexWriter)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), 10); // TODO: extract magic number to constant

            return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> {
                    // TODO: extract try-catch to separate method because it so large for map() method
                    try {
                        Document doc = reader.document(scoreDoc.doc); // TODO: use not deprecated method, check java doc - org.apache.lucene.index.IndexReader.document(int, java.util.Set<java.lang.String>)
                        return new IndexDto(
                            doc.get("TITLE"),
                            doc.get("LINK"),
                            topDocs // TODO: set body instead topDocs
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e); // TODO: wrap exception to custom exception
                    }
                })
                .toList();
        } catch (IOException e) {
            throw new IndexReaderException("Error during reading index documents", e);
        }
    }

    private List<Document> createDocuments(Map<String, ParsedHtmlPage> data) {
        return data.entrySet().stream()
            .map(entry -> {
                Document document = new Document();
                // TODO: extract "LINK", "TITLE", "BODY" to constants
                document.add(new TextField("LINK", entry.getKey(), Field.Store.YES));
                document.add(new TextField("TITLE", entry.getValue().getTitle(), Field.Store.YES));
                document.add(new TextField("BODY", entry.getValue().getBody().text(), Field.Store.YES));
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

    public static final class IndexReaderException extends RuntimeException {

        public IndexReaderException(String message, Exception exception) {
            super(message, exception);
        }
    }
}
