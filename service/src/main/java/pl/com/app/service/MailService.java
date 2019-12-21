package pl.com.app.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.com.app.dto.MailSenderDTO;
import pl.com.app.exceptions.exceptions.ExceptionCode;
import pl.com.app.exceptions.exceptions.MyException;

import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MailService {
    private final JavaMailSender javaMailSender;

    public void sendMail(MailSenderDTO mailSenderDTO) {
        try {
            if (mailSenderDTO == null) {
                throw new NullPointerException("MAIL SENDER IS NULL");
            }

            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setTo(mailSenderDTO.getRecipientAddress());
            mimeMessageHelper.setSubject(mailSenderDTO.getSubject());
            mimeMessageHelper.setText(mailSenderDTO.getMessage(), true);

            if (mailSenderDTO.getAttachments() != null && mailSenderDTO.getAttachments().length > 0){
                for (MultipartFile file : mailSenderDTO.getAttachments()){
                    mimeMessageHelper.addAttachment(file.getOriginalFilename(), file);
                }
            }

            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new MyException(ExceptionCode.SERVICE, e.getMessage());
        }
    }
}

