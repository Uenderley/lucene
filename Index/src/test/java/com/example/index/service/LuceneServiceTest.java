package com.example.index.service;

import com.example.index.dto.RecordDTO;
import com.example.index.dto.SearchRequestDTO;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class LuceneServiceTest {

    @Autowired
    private LuceneService luceneService;

    @BeforeAll
    public static void indexDocuments(@Autowired LuceneService luceneService) throws IOException {
        RecordDTO record1 = new RecordDTO();
        record1.setRecordId(1);
        record1.setPmsRegistration("PMS123");
        record1.setIpRange("192.168.1.1-192.168.1.255");
        record1.setMainPhoneNumber("123456789");
        record1.setAddress("HARAHH 2345");

        RecordDTO record2 = new RecordDTO();
        record2.setRecordId(2);
        record2.setPmsRegistration("PMS456");
        record2.setIpRange("192.168.2.1-192.168.2.255");
        record2.setMainPhoneNumber("987654321");
        record2.setAddress("Some address 5678");

        RecordDTO record3 = new RecordDTO();
        record3.setRecordId(3);
        record3.setPmsRegistration("PMS999");
        record3.setIpRange("192.168.2.1-192.168.2.255");
        record3.setMainPhoneNumber("9874545454");
        record3.setAddress("Other address 5678");

        luceneService.addDoc(record1);
        luceneService.addDoc(record2);
        luceneService.addDoc(record3);
    }

    @Test
    public void testSearchByIPAddressRange() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("192.168.2");
        searchRequest.setFields(Arrays.asList("ipRange"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(doc -> doc.getIpRange().startsWith("192.168.2")));
    }

    @Test
    public void testSearchAddress5678() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 5678");
        searchRequest.setFields(Arrays.asList("address"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(2, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddress8() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8");
        searchRequest.setFields(Arrays.asList("address"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchAddressO() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address o");
        searchRequest.setFields(Arrays.asList("address"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchAddressO678() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address o 678");
        searchRequest.setFields(Arrays.asList("address"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchAddressSome() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address some");
        searchRequest.setFields(Arrays.asList("address"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddressNada() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address nada");
        searchRequest.setFields(Arrays.asList("address"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchAddress85678456() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8 5678 456");
        searchRequest.setFields(Arrays.asList("address", "pmsRegistration"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddress856784562255() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8 5678 456 2.255");
        searchRequest.setFields(Arrays.asList("address", "pmsRegistration", "ipRange"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddress856784562255Dot() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8 5678 456 2.255 .");
        searchRequest.setFields(Arrays.asList("address", "pmsRegistration", "ipRange"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddressPmsMainPhone() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8 5678 456 987654321");
        searchRequest.setFields(Arrays.asList("address", "pmsRegistration", "mainPhoneNumber"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddressPmsMainPhoneSplited() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8 5678 456 98765 4321");
        searchRequest.setFields(Arrays.asList("address", "pmsRegistration", "mainPhoneNumber"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
        assertTrue(results.stream().anyMatch(doc -> doc.getAddress().equals("Some address 5678")));
    }

    @Test
    public void testSearchAddressPmsMainPhoneSplitedNoResult() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address 8 5678 456 98765 4321 .");
        searchRequest.setFields(Arrays.asList("address", "pmsRegistration", "mainPhoneNumber"));
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(0, results.size());
    }

    @Test
    public void testSearchAnyField() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("address");
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(2, results.size());
    }

    @Test
    public void testSearchAnyFieldByPhoneNumber() throws IOException, ParseException {
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery("987654321");
        searchRequest.setLimit(5);

        List<RecordDTO> results = luceneService.searchDocuments(searchRequest);
        assertEquals(1, results.size());
    }

}
