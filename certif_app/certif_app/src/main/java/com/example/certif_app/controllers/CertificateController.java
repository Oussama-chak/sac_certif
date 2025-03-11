package com.example.certif_app.controllers;

import com.example.certif_app.dto.request.CertificateDownloadRequest;
import com.example.certif_app.dto.request.CertificateRequest;
import com.example.certif_app.dto.request.TemplateRequest;
import com.example.certif_app.entities.Certificate;
import com.example.certif_app.entities.Template;
import com.example.certif_app.repos.CertificateRepository;
import com.example.certif_app.repos.TemplateRepository;
import com.example.certif_app.services.security.CertificateService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
@CrossOrigin("*")
@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;
    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private TemplateRepository templateRepository;

    @CrossOrigin(origins = "*",methods = {RequestMethod.POST},
            exposedHeaders = {"Content-Disposition"})
    @PostMapping("/generate")
    public ResponseEntity<Resource> generateCertificate(@RequestBody CertificateRequest request) {
        // First, generate and save the certificate, which will return the certificate number
        Long certificateNumber = certificateService.generateCertifAndSave(request);

        // Then get the certificate PDF using the certificate number
        byte[] certificateBytes = certificateService.getCertificatebycode(certificateNumber);

        String fileName = String.format("certificate_%s_%s_%s.pdf",
                        request.getFirstName(),
                        request.getLastName(),
                        request.getTitle())
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        ByteArrayResource resource = new ByteArrayResource(certificateBytes);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
    @CrossOrigin(origins = "*",methods = {RequestMethod.POST},
            exposedHeaders = {"Content-Disposition"})
    @PostMapping("/download")
    public ResponseEntity<Resource> downloadCertificate(@RequestBody CertificateDownloadRequest request) {
        // Retrieve the certificate as a byte array
        byte[] pdfData = certificateService.getCertificate(request);
        Certificate c=certificateRepository.findCertificateByCertificateNumber(request.getCertificatenumber());

        // Check if the PDF data is not null
        if (pdfData != null) {
            // Create a ByteArrayResource from the PDF data
            ByteArrayResource resource = new ByteArrayResource(pdfData);
            String fileName = String.format("certificate_%s_%s_%s.pdf",
                           c.getFirstName(),
                            c.getLastName(),
                            c.getTitle())
                    .replaceAll("[^a-zA-Z0-9._-]", "_");

            // Create ResponseEntity with appropriate headers
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+fileName + "\"")
                    .body(resource);
        } else {
            // Handle the case where no PDF data is found
            return ResponseEntity.notFound().build();
        }
    }

    @CrossOrigin("*")
    @GetMapping("/download/{certifnb}")
    public ResponseEntity<Resource> downloadCertificateFromqr(@PathVariable Long certifnb) {
        // Retrieve the certificate as a byte array
        Certificate c=certificateRepository.findCertificateByCertificateNumber(certifnb);
        byte[] pdfData = certificateService.getCertificatebycode(certifnb);

        // Check if the PDF data is not null
        if (pdfData != null) {
            // Create a ByteArrayResource from the PDF data
            ByteArrayResource resource = new ByteArrayResource(pdfData);

            // Create ResponseEntity with appropriate headers
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate_"+c.getFirstName()+"_"+c.getLastName()+"_"+c.getTitle()+".pdf")
                    .body(resource);
        } else {
            // Handle the case where no PDF data is found
            return ResponseEntity.notFound().build();
        }

    }


    @CrossOrigin("*")
    @PostMapping("/template")
    public ResponseEntity<?> saveTemplate(@RequestBody TemplateRequest request) {
        try {
            byte[] pdfBytes = Base64.getDecoder().decode(request.getPdfdata());

            PDDocument.load(pdfBytes);

            Template template = Template.builder()

                    .templatedata(pdfBytes)
                    .build();

            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            templateRepository.save(template);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new HashMap<String, String>() {{
                        put("message", "Template uploaded successfully");
                        // Optionally add more information
                        // put("filename", "certificate.pdf");
                        // put("status", "success");
                    }});

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }




}

