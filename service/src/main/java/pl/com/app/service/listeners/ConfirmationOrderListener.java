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
public class ConfirmationOrderListener implements ApplicationListener<OnConfirmationOrderEvenData> {

    private final RegistrationService registrationService;
    private final MailService mailService;

    @Override
    public void onApplicationEvent(OnConfirmationOrderEvenData data) {
        sendConfirmationOrderEmail(data);
    }

    private void sendConfirmationOrderEmail(OnConfirmationOrderEvenData data) {

        User user = data.getUser();
        String token = UUID.randomUUID().toString();
        registrationService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "PHONES-APP: confirmation order";
        String url = data.getUrl() + "orders/orderConfirmation?token=" + token;

        String message =
                body(
                        h2("Hello!"),
                        h3("This is Phones-App."),
                        h3("Your order is:"),
                        h3(data.getPhoneOrders()),
                        h3(
                                text("Click "),
                                a("here").withHref(url),
                                text(" confirm order.")
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
