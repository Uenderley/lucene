package com.example.index.startup;

import com.example.index.dto.RecordDTO;
import com.example.index.service.LuceneIndex;
import com.example.index.service.LuceneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OnStartUp {

    @Autowired
    LuceneService luceneService;

    public void createIndex() throws IOException {
        RecordDTO record1 = new RecordDTO();
        record1.setRecordId(1);
        record1.setPmsRegistration("PMS123");
        record1.setIpRange("192.168.1.1-192.168.1.255");
        record1.setMainPhoneNumber("123456789");
        record1.setAddress("HARAHH 1234");

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

        RecordDTO record4 = new RecordDTO();
        record4.setRecordId(4);
        record4.setPmsRegistration("PMS9126");
        record4.setIpRange("192.168.2.1-192.168.2.255");
        record4.setMainPhoneNumber("9874545454");
        record4.setAddress("Other HARAHH 88888");

        luceneService.addDoc(record1);
        luceneService.addDoc(record2);
        luceneService.addDoc(record3);
        luceneService.addDoc(record4);
    }
}
