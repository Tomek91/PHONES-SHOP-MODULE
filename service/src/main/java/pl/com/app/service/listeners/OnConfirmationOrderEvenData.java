package pl.com.app.service.listeners;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pl.com.app.model.User;


@Getter
public class OnConfirmationOrderEvenData extends ApplicationEvent {

    private final String url;
    private final User user;
    private final String phoneOrders;

    public OnConfirmationOrderEvenData(String url, User user, String phoneOrders) {
        super(user);
        this.url = url;
        this.user = user;
        this.phoneOrders = phoneOrders;
    }
}
