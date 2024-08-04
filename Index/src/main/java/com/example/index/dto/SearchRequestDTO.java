package com.example.index.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequestDTO {
    private String query;
    private List<String> fields;
    private int limit = 10;
}
