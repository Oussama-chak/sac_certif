package com.example.certif_app.services.security;

import com.example.certif_app.exceptions.CertificateGenerationException;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import java.awt.*;
import java.awt.image.BufferedImage;


@Service
public class QRCodeService {

    public BufferedImage generateQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(
                    data,
                    BarcodeFormat.QR_CODE,
                    200,
                    200
            );

            return createQRImage(bitMatrix);
        } catch (WriterException e) {
            throw new CertificateGenerationException("Failed to generate QR code", e);
        }
    }

    private BufferedImage createQRImage(BitMatrix bitMatrix) {
        BufferedImage qrImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        qrImage.createGraphics();

        Graphics2D graphics = (Graphics2D) qrImage.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 200, 200);
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                if (bitMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        return qrImage;
    }
}
