package com.example.index.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecordDTO {
    private String accountNumber;
    private int recordId;
    private String ipRange;
    private String pmsRegistration;
    private String address;
    private String mainPhoneNumber;
    private String type;
}
