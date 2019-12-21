package pl.com.app.service;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

@Service
@Slf4j
public class FileService {

    @Value("${imgPath}")
    private String imgPath;

    private String createFilename(String originalFilename) {
        final String[] arr = originalFilename.split("\\.");
        final String extension = arr[arr.length - 1];
        final String filename = Base64.getEncoder().encodeToString(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss"))
                        .getBytes()
        );
        return String.join(".", filename, extension);
    }

    public String addFile(MultipartFile file) {
        try {

            if (file == null) {
                throw new NullPointerException("FILE IS NULL");
            }

            final String filename = createFilename(file.getOriginalFilename());
            final String fullPath = imgPath + filename;
            FileCopyUtils.copy(file.getBytes(), new File(fullPath));
            return fullPath;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.FILE, e.getMessage());
        }
    }

    public String updateFile(MultipartFile file, String fullPath) {
        try {

            if (file == null || file.getBytes().length == 0) {
                return fullPath;
            }

            if (fullPath == null) {
                throw new NullPointerException("FILENAME IS NULL");
            }

            FileCopyUtils.copy(file.getBytes(), new File(fullPath));
            return fullPath;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.FILE, e.getMessage());
        }
    }

    public String deleteFile(String fullPath) {
        try {

            if (fullPath == null) {
                throw new NullPointerException("FILENAME IS NULL");
            }
            new File(fullPath).delete();
            return fullPath;
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.FILE, e.getMessage());
        }
    }

    public byte[] getPhoneImage(String imagePath) {
        try {
            if (imagePath == null) {
                throw new NullPointerException("IMAGE PATH IS NULL");
            }
            return FileCopyUtils.copyToByteArray(new File(imagePath));
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.FILE, e.getMessage());
        }
    }
}
