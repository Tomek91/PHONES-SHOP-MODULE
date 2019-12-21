package pl.com.app.service;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AmazonService {

    @Value("${amazonProperties.bucketName}")
    private String bucketName;

    @Value("${amazonProperties.accessKey}")
    private String accessKey;

    @Value("${amazonProperties.secretKey}")
    private String secretKey;

    @Value("${amazonProperties.folderName}")
    private String folderName;

    @Value("${amazonProperties.url}")
    private String url;

    private AmazonS3 s3Client;

    @PostConstruct
    public void init() {

        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.US_EAST_2)
                .build();
    }

    public String uploadFile(MultipartFile multipartFile) {

        if (multipartFile == null) {
            throw new MyException(ExceptionCode.FILE, "multipartfile object is null");
        }

        String filename = generateFilename(multipartFile);
        File file = fromMultipartFileToFile(multipartFile);
        s3Client.putObject(bucketName, folderName + "/" + filename, file);
        deleteTempFile(file);
        return url + filename;

    }

    public String uploadFile(String filePath) {

        if (filePath == null) {
            throw new MyException(ExceptionCode.FILE, "file path is null");
        }

        File file = new File(filePath);
        String filename = generateFilename(file);
        s3Client.putObject(bucketName, folderName + "/" + filename, file);
        deleteTempFile(file);
        return url + filename;
    }

    public String downloadFile(String filename) {
        try {
            S3Object s3Object = s3Client.getObject(bucketName, folderName + "/" + filename);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            FileUtils.copyInputStreamToFile(inputStream,new File(s3Object.getKey()));
            return s3Object.getKey();
        } catch (Exception e) {
            throw new MyException(ExceptionCode.FILE, "download file exception: " + e.getMessage());
        }
    }

    public String deleteFile(String filename) {
        try {
            s3Client.deleteObject(bucketName,folderName + "/" + filename);
            return filename;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.FILE, "delete file exception: " + e.getMessage());
        }
    }

    private String getAllBuckets() {
        return s3Client
                .listBuckets()
                .stream()
                .map(Bucket::getName)
                .collect(Collectors.joining());
    }

    private String getAllFiles() {
        return s3Client
                .listObjects(bucketName)
                .getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .collect(Collectors.joining());

    }

    private File fromMultipartFileToFile(MultipartFile multipartFile) {
        try {
            File file = new File(multipartFile.getOriginalFilename());
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(multipartFile.getBytes());
            fos.close();
            return file;
        } catch (Exception e) {
            throw new MyException(ExceptionCode.FILE, "from multipartfile to file conversion exception");
        }
    }

    private boolean deleteTempFile(File file) {
        if (file != null) {
            return file.delete();
        }
        return false;
    }

    private String generateFilename(MultipartFile multipartFile) {
        String extension = multipartFile.getOriginalFilename().split("\\.")[1];
        String filenamePart1 = UUID.randomUUID().toString().replaceAll("\\W", "");
        String filenamePart2 = String.valueOf(System.nanoTime());
        return filenamePart1 + "-" + filenamePart2 + "." + extension;
    }

    private String generateFilename(File file) {
        String extension = FilenameUtils.getExtension(file.getAbsolutePath());
        String filenamePart1 = UUID.randomUUID().toString().replaceAll("\\W", "");
        String filenamePart2 = String.valueOf(System.nanoTime());
        return filenamePart1 + "-" + filenamePart2 + "." + extension;
    }

}
