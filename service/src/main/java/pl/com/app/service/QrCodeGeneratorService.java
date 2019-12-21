package pl.com.app.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
public class QrCodeGeneratorService {

    @Value("${qrCodePath}")
    private String qrCodePath;

    private String getFileName(String filePath) {
        StringBuilder fullPath = new StringBuilder();
        fullPath
                .append(filePath)
                .append("/")
                .append(
                        Base64.getEncoder().encodeToString(
                                LocalDateTime.now()
                                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
                                        .getBytes())
                )
                .append(".png");
        return fullPath.toString();
    }

    private String generateQRCodeImage(String text, int width, int height, String filePath) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);

            String fullPath = getFileName(filePath);
            Path path = Paths.get(fullPath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
            return fullPath;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }

    public String generateQRCodeImage(String transactionNumber) {
        return generateQRCodeImage(transactionNumber, 350, 350, qrCodePath);
    }
}
