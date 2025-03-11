package com.example.certif_app.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificateRequest {
    private String title;

    private String firstName;
    private String lastName;
    private Long cin;
    private LocalDate date;
    private String instructor;
}
