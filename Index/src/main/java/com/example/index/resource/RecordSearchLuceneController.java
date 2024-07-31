package com.example.index.resource;

import com.example.index.dto.RecordDTO;
import com.example.index.service.LuceneIndex;
import com.example.index.service.RecordSearchEngine;
import org.apache.logging.log4j.util.Strings;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/recordsLucene")
public class RecordSearchLuceneController {

    private final LuceneIndex luceneIndex;

    public RecordSearchLuceneController(LuceneIndex luceneIndex) {
        this.luceneIndex = luceneIndex;
    }

    @GetMapping("search")
    public List<RecordDTO> searchRecords(@RequestParam(required = false) String query) throws ParseException, IOException {
        if (Strings.isBlank(query)) {
            return luceneIndex.retrieveAll();
        }
        return luceneIndex.search(query);
    }
}