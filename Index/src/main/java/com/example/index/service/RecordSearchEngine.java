package com.example.index.service;

import com.example.index.dto.RecordDTO;
import org.springframework.stereotype.Component;

import java.util.List;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RecordSearchEngine {

    private final List<RecordDTO> records;
    private final Map<String, Map<String, Set<Integer>>> indices = new HashMap<>();

    public RecordSearchEngine(List<RecordDTO> records) {
        this.records = records;
        buildIndices();
    }

    private void buildIndices() {
        for (int i = 0; i < records.size(); i++) {
            RecordDTO record = records.get(i);
            indexRecord(record, i);
        }
    }

    private void indexRecord(RecordDTO record, int recordId) {
        indexAttribute("accountNumber", record.getAccountNumber(), recordId);
        indexAttribute("propertyName", record.getPropertyName(), recordId);
    }

    private void indexAttribute(String attributeName, String value, int recordId) {
        indices.computeIfAbsent(attributeName, k -> new HashMap<>())
                .computeIfAbsent(value, k -> new HashSet<>())
                .add(recordId);
    }

    public List<RecordDTO> search(Map<String, String> criteria) {
        Set<Integer> resultIds = new HashSet<>(IntStream.range(0, records.size()).boxed().collect(Collectors.toSet()));
        for (Map.Entry<String, String> entry : criteria.entrySet()) {
            String attributeName = entry.getKey();
            String value = entry.getValue();
            Set<Integer> attributeIds = indices.getOrDefault(attributeName, Collections.emptyMap())
                    .getOrDefault(value, Collections.emptySet());
            resultIds.retainAll(attributeIds);
        }
        return resultIds.stream()
                .map(records::get)
                .collect(Collectors.toList());
    }
}
