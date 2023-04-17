package com.github.kirn0al.jowl.index;

import com.github.kirn0al.jowl.parse.ParsedHtmlPage;
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
import org.apache.lucene.search.ScoreDoc;
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

    static final String TITLE = "TITLE";
    static final String LINK = "LINK";
    static final String BODY = "BODY";
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

    public int countIndexedDocuments() {
        return indexWriter.getDocStats().numDocs;
    }

    private IndexDto getDocumentFromScoreDoc(ScoreDoc scoreDoc, IndexReader reader) {
        try {
            Document doc = reader.storedFields().document(scoreDoc.doc);
            return new IndexDto(
                doc.get(TITLE),
                doc.get(LINK),
                doc.get(BODY)
            );
        } catch (IOException e) {
            throw new GettingIndexException("", e);
        }
    }

    public List<IndexDto> showCertainNumberOfDocuments(int numberOfDocuments) {
        try (IndexReader reader = DirectoryReader.open(indexWriter)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(new MatchAllDocsQuery(), numberOfDocuments);

            return Arrays.stream(topDocs.scoreDocs)
                .map(scoreDoc -> getDocumentFromScoreDoc(scoreDoc, reader))
                .toList();
        } catch (IOException e) {
            throw new IndexReaderException("Error during reading index documents", e);
        }
    }

    private List<Document> createDocuments(Map<String, ParsedHtmlPage> data) {
        return data.entrySet().stream()
            .map(entry -> {
                Document document = new Document();
                document.add(new TextField(LINK, entry.getKey(), Field.Store.YES));
                document.add(new TextField(TITLE, entry.getValue().getTitle(), Field.Store.YES));
                document.add(new TextField(BODY, entry.getValue().getBody().text(), Field.Store.YES));
                return document;
            })
            .toList();
    }

    private static final class GettingIndexException extends RuntimeException {

        public GettingIndexException(String message, Exception exception) {
            super(message, exception);
        }
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
