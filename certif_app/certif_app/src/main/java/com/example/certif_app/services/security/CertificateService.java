package com.example.certif_app.services.security;

import com.example.certif_app.dto.request.CertificateDownloadRequest;
import com.example.certif_app.dto.request.CertificateRequest;
import com.example.certif_app.entities.Certificate;
import com.example.certif_app.entities.Template;
import com.example.certif_app.exceptions.CertificateGenerationException;
import com.example.certif_app.repos.CertificateRepository;
import com.example.certif_app.repos.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class CertificateService {


    @Value("${app.base-url}")
    private String baseUrl;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private CertificateRepository certificateRepository;
    @Autowired
    private TemplateRepository templateRepository;


    public byte[] generateCertificate(CertificateRequest request, String qrCodeData,Long certificateNumber){

        Template template= templateRepository.findLastInserted();



        try (PDDocument document = PDDocument.load(template.getTemplatedata())) {
            PDAcroForm form = document.getDocumentCatalog().getAcroForm();

            if(form==null){
                throw new CertificateGenerationException("Certificate doesn't contain form fields");
            }

            fillFormFields(form,request,certificateNumber);


            addQRCodeToDocument(document, qrCodeData);

            form.flatten();


            return saveToByteArray(document);

        }
        catch(IOException e){
            throw new CertificateGenerationException("Failed to generate certificate",e);
        }
    }
    public Long generateCertifAndSave(CertificateRequest request) {
        try {
            Optional<Certificate> existingCertificate = certificateRepository
                    .findByFirstNameAndLastNameAndCinAndTitle(
                            request.getFirstName(),
                            request.getLastName(),
                            request.getCin(),
                            request.getTitle()
                    );

            if (existingCertificate.isPresent()) {
                throw new CertificateGenerationException("Certificate already exists for this person and title");
            }
            // Generate certificate number first
            Long certificateNumber = Long.parseLong(generateCertificateId());

            // Generate QR code with the certificate number
            String qrCodeData = generateQRCodeData(request, certificateNumber);

            // Generate PDF with QR code
            byte[] pdfBytes = generateCertificate(request, qrCodeData,certificateNumber);

            // Build and save certificate
            Certificate c = Certificate.builder()
                    .firstName(request.getFirstName())
                    .lastName(request.getLastName())
                    .cin(request.getCin())
                    .title(request.getTitle())
                    .instructor(request.getInstructor())

                    .createdAt(request.getDate())
                    .certificateNumber(certificateNumber)
                    .pdfData(pdfBytes)
                    .build();
            certificateRepository.save(c);
            return certificateNumber;
        } catch (Exception e) {
            throw new CertificateGenerationException("Failed to generate certificate", e);
        }
    }
    private void fillFormFields(PDAcroForm form, CertificateRequest request,Long certificatenumber)throws IOException{
        setFieldValue(form, "first_name", request.getFirstName());
        setFieldValue(form, "last_name", request.getLastName());
        setFieldValue(form, "instructor", request.getInstructor());
        setFieldValue(form, "date", formatDate(request.getDate()));
        setFieldValue(form, "title", request.getTitle());
        setFieldValue(form,"certificate_number",certificatenumber.toString());

    }

    private void setFieldValue(PDAcroForm form, String fieldName, String value) throws IOException {
        PDField field = form.getField(fieldName);
        if (field != null) {
            field.setValue(value);
        } else {
            log.warn("Field {} not found in template", fieldName);
        }
    }
    private String formatDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.FRENCH));
    }

    private String generateCertificateId() {
        return String.valueOf(ThreadLocalRandom.current().nextLong(1000000000L, 9999999999L));

    }

    private void addQRCodeToDocument(PDDocument document, String qrCodeData) throws IOException {
        BufferedImage qrCode = qrCodeService.generateQRCode(qrCodeData);
        PDPage page = document.getPage(0);

        // Convert QR code to PDF image
        PDImageXObject qrCodeImage = LosslessFactory.createFromImage(document, qrCode);

        try (PDPageContentStream contentStream = new PDPageContentStream(
                document, page, PDPageContentStream.AppendMode.APPEND, true, true)) {
            // Add QR code to designated area (adjust coordinates as needed)
            contentStream.drawImage(qrCodeImage, 1650, 237, 250, 250);
        }
    }

    private String generateQRCodeData(CertificateRequest request, Long certificateNumber) {

        return baseUrl +"/"+certificateNumber;

    }


    private byte[] saveToByteArray(PDDocument document) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        return baos.toByteArray();
    }


    public byte[] getCertificate(CertificateDownloadRequest request){
        Certificate c=certificateRepository.findCertificateByCertificateNumber(request.getCertificatenumber());

        if (c != null && c.getPdfData() != null) {
            return c.getPdfData();
        } else {
            throw new CertificateGenerationException("Certificate not found for the user provided.");
        }
    }


    public byte[] getCertificatebycode(Long certifnb) {
        Certificate c = certificateRepository.findCertificateByCertificateNumber(certifnb);
        if (c != null && c.getPdfData() != null) {
            return c.getPdfData();
        } else {
            throw new CertificateGenerationException("Certificate not found for this code provided.");
        }
    }



}








