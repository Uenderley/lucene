package com.example.index.service;

import com.example.index.dto.RecordDTO;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Component
public class LuceneIndex {
    private Directory directoryIndex;
    private Analyzer analyzer;

    public LuceneIndex() throws IOException {
        directoryIndex = FSDirectory.open(Paths.get("/home/uenderley/Documents/index"));
        analyzer = new StandardAnalyzer();
    }

    public void clearIndex() throws IOException {
        try (IndexWriter writer = new IndexWriter(directoryIndex, new IndexWriterConfig(analyzer))) {
            writer.deleteAll();
            writer.commit();
        }
    }

    public void addRecordIndex(RecordDTO record) throws IOException {
        try (IndexWriter writer = new IndexWriter(directoryIndex, new IndexWriterConfig(analyzer))) {
            Document document = new Document();

            document.add(new StringField("id", String.valueOf(record.getRecordId()), Field.Store.YES));
            document.add(new TextField("pmsRegistration", record.getPmsRegistration(), Field.Store.YES));
            document.add(new TextField("ipRange", record.getIpRange(), Field.Store.YES));
            document.add(new TextField("mainPhoneNumber", record.getMainPhoneNumber(), Field.Store.YES));
            document.add(new TextField("address", record.getAddress(), Field.Store.YES));
            writer.addDocument(document);
        }
    }

    public List<RecordDTO> searchUnique(String queryStr) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser("content", analyzer);
        Query query = queryParser.parse(queryStr);

        List<RecordDTO> results = new ArrayList<>();
        try (DirectoryReader directoryReader = DirectoryReader.open(directoryIndex)) {
            IndexSearcher searcher = new IndexSearcher(directoryReader);
            TopDocs topDocs = searcher.search(query, 10);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                RecordDTO record = new RecordDTO();
                record.setRecordId(Integer.parseInt(document.get("id")));
                record.setPmsRegistration(document.get("pmsRegistration"));
                record.setIpRange(document.get("ipRange"));
                record.setMainPhoneNumber(document.get("mainPhoneNumber"));
                record.setAddress(document.get("address"));
                results.add(record);
            }
        }
        return results;
    }

    public List<RecordDTO> search(String[] fields, String[] queries) throws IOException {
        List<RecordDTO> results = new ArrayList<>();

        try (DirectoryReader directoryReader = DirectoryReader.open(directoryIndex)) {
            IndexSearcher searcher = new IndexSearcher(directoryReader);

            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();

            for (int i = 0; i < fields.length; i++) {
                String field = fields[i];
                String queryStr = queries[i];

                if (queryStr.contains("*") || queryStr.contains("?")) {
                    booleanQueryBuilder.add(new WildcardQuery(new Term(field, queryStr.toLowerCase())), BooleanClause.Occur.MUST);
                } else {
                    booleanQueryBuilder.add(new TermQuery(new Term(field, queryStr.toLowerCase())), BooleanClause.Occur.MUST);
                }
            }

            BooleanQuery booleanQuery = booleanQueryBuilder.build();
            TopDocs topDocs = searcher.search(booleanQuery, 10);

            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                RecordDTO record = new RecordDTO();
                record.setRecordId(Integer.parseInt(document.get("id")));
                record.setPmsRegistration(document.get("pmsRegistration"));
                record.setIpRange(document.get("ipRange"));
                record.setMainPhoneNumber(document.get("mainPhoneNumber"));
                record.setAddress(document.get("address"));
                results.add(record);
            }
        }
        return results;
    }

    public List<RecordDTO> search(String queryStr) throws ParseException, IOException {
        List<RecordDTO> results = new ArrayList<>();

        try (DirectoryReader directoryReader = DirectoryReader.open(directoryIndex)) {
            IndexSearcher searcher = new IndexSearcher(directoryReader);
            Query query = buildQuery(queryStr);

            TopDocs topDocs = searcher.search(query, 10);
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.doc(scoreDoc.doc);
                RecordDTO record = new RecordDTO();
                record.setRecordId(Integer.parseInt(document.get("id")));
                record.setPmsRegistration(document.get("pmsRegistration"));
                record.setIpRange(document.get("ipRange"));
                record.setMainPhoneNumber(document.get("mainPhoneNumber"));
                record.setAddress(document.get("address"));
                results.add(record);
            }
        }
        return results;
    }

    private Query buildQuery(String queryStr) throws ParseException {
        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        String[] parts = queryStr.split(" AND ");
        for (String part : parts) {
            String[] fieldAndValue = part.split(":", 2);
            if (fieldAndValue.length == 2) {
                String field = fieldAndValue[0];
                String value = fieldAndValue[1];
                if (value.contains("*") || value.contains("?")) {
                    booleanQueryBuilder.add(new WildcardQuery(new Term(field, value.toLowerCase())), BooleanClause.Occur.MUST);
                } else {
                    booleanQueryBuilder.add(new TermQuery(new Term(field, value.toLowerCase())), BooleanClause.Occur.MUST);
                }
            } else {
                throw new ParseException("Invalid query format: " + part);
            }
        }
        return booleanQueryBuilder.build();
    }

    public List<RecordDTO> retrieveAll() {
        List<RecordDTO> recordDTOS = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(directoryIndex)) {
            for (int i = 0; i < reader.maxDoc(); i++) {
                Document doc = reader.document(i);
                RecordDTO record = new RecordDTO();
                record.setRecordId(Integer.parseInt(doc.get("id")));
                record.setPmsRegistration(doc.get("pmsRegistration"));
                record.setIpRange(doc.get("ipRange"));
                record.setMainPhoneNumber(doc.get("mainPhoneNumber"));
                record.setAddress(doc.get("address"));
                recordDTOS.add(record);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recordDTOS;
    }
}
