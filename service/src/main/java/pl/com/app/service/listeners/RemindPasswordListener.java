package pl.com.app.service.listeners;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pl.com.app.dto.MailSenderDTO;
import pl.com.app.model.User;
import pl.com.app.service.MailService;
import pl.com.app.service.RegistrationService;

import java.util.UUID;

import static j2html.TagCreator.*;

@Component
@RequiredArgsConstructor
public class RemindPasswordListener implements ApplicationListener<OnRemindPasswordEvenData> {

    private final RegistrationService registrationService;
    private final MailService mailService;

    @Override
    public void onApplicationEvent(OnRemindPasswordEvenData data) {
        sendRemindPasswordEmail(data);
    }

    private void sendRemindPasswordEmail(OnRemindPasswordEvenData data) {

        User user = data.getUser();
        String token = UUID.randomUUID().toString();
        registrationService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "PHONES-APP: password remind";
        String url = data.getUrl() + "login/remind-password/reset?token=" + token;

        String message =
                body(
                        h2("Hello!"),
                        h3("This is Phones-App."),
                        h3(
                                text("Click "),
                                a("here").withHref(url),
                                text(" to change password.")
                        )
                ).render();

        mailService.sendMail(
                MailSenderDTO.builder()
                        .recipientAddress(recipientAddress)
                        .subject(subject)
                        .message(message)
                        .build()
        );
    }

}
