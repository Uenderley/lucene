package com.example.index.service;

import com.example.index.dto.RecordDTO;
import com.example.index.dto.SearchRequestDTO;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class LuceneService {

    private Directory index;
    private StandardAnalyzer analyzer;

    public LuceneService() throws IOException {
        this.analyzer = new StandardAnalyzer();
        this.index = new RAMDirectory();
    }

    public void addDoc(RecordDTO record) throws IOException {
        try (IndexWriter writer = new IndexWriter(index, new IndexWriterConfig(analyzer))) {
            Document document = new Document();

            document.add(new StringField("id", String.valueOf(record.getRecordId()), Field.Store.YES));
            document.add(new StringField("pmsRegistration", record.getPmsRegistration(), Field.Store.YES));
            document.add(new StringField("ipRange", record.getIpRange(), Field.Store.YES));
            document.add(new StringField("mainPhoneNumber", record.getMainPhoneNumber(), Field.Store.YES));
            document.add(new TextField("address", record.getAddress(), Field.Store.YES));
            writer.addDocument(document);
        }
    }

    public List<RecordDTO> searchDocuments(SearchRequestDTO searchRequest) throws IOException, ParseException {
        int hitsPerPage = searchRequest.getLimit();

        DirectoryReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);

        Query queryClauses = queryBuilderEngine(searchRequest, reader);

        TopDocs docs = searcher.search(queryClauses, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        List<RecordDTO> results = new ArrayList<>();
        for (ScoreDoc hit : hits) {
            int docId = hit.doc;
            Document document = searcher.doc(docId);

            RecordDTO record = new RecordDTO();
            record.setRecordId(Integer.parseInt(document.get("id")));
            record.setPmsRegistration(document.get("pmsRegistration"));
            record.setIpRange(document.get("ipRange"));
            record.setMainPhoneNumber(document.get("mainPhoneNumber"));
            record.setAddress(document.get("address"));
            results.add(record);
        }
        reader.close();
        return results;
    }

    private Query queryBuilderEngine(SearchRequestDTO searchRequest, DirectoryReader reader) throws ParseException, IOException {
        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();


        if (ObjectUtils.isEmpty(searchRequest.getFields())) {// Search a value in all fields
            Document firstDoc = reader.document(0);
            List<String> allFields = new ArrayList<>();
            for (IndexableField field : firstDoc.getFields()) {
                allFields.add(field.name());
            }
            MultiFieldQueryParser parser = new MultiFieldQueryParser(allFields.toArray(new String[0]), analyzer);
            Query parse = parser.parse(searchRequest.getQuery());
            return parse;
        } else {
            for (String term : searchRequest.getQuery().split("\\s+")) {
                BooleanQuery.Builder fieldQuery = new BooleanQuery.Builder();
                for (String field : searchRequest.getFields()) {
                    Query query = new WildcardQuery(new Term(field, "*" + term + "*"));
                    fieldQuery.add(new BooleanClause(query, BooleanClause.Occur.SHOULD));
                }
                booleanQuery.add(new BooleanClause(fieldQuery.build(), BooleanClause.Occur.MUST));
            }
            return booleanQuery.build();
        }
    }

}
