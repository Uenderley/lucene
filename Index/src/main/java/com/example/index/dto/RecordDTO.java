package com.example.index.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
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
