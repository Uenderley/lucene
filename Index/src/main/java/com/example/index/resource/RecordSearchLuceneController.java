package com.example.index.resource;

import com.example.index.dto.RecordDTO;
import com.example.index.dto.SearchRequestDTO;
import com.example.index.service.LuceneService;
import com.example.index.startup.OnStartUp;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/records")
public class RecordSearchLuceneController {

    private final LuceneService luceneService;
    private final OnStartUp onStartUp;

    public RecordSearchLuceneController(LuceneService luceneService, OnStartUp onStartUp) {
        this.luceneService = luceneService;
        this.onStartUp = onStartUp;
    }

    @PostMapping("query")
    public ResponseEntity<List<RecordDTO>> searchRecords(@RequestBody SearchRequestDTO searchRequest) throws IOException, ParseException {
        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        return ResponseEntity.ok(results);
    }

    @PostMapping("create-index")
    public ResponseEntity<String> createIndex() throws IOException, ParseException {
        onStartUp.createIndex();
        return ResponseEntity.ok("Index Created");
    }
}