package com.example.certif_app.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@Table(name="certificates")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable=false)
    private Long cin;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String instructor;





    @Lob
    @Column(name = "pdf_data", columnDefinition = "LONGBLOB", nullable = false)
    private byte[] pdfData;



    @Column(unique = true, nullable = false)
    private Long certificateNumber;


    @Column(updatable = false)
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDate.now();
    }

}
