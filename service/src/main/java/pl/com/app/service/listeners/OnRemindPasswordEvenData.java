package pl.com.app.service.listeners;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pl.com.app.model.User;

@Getter
public class OnRemindPasswordEvenData extends ApplicationEvent {

    private final String url;
    private final User user;

    public OnRemindPasswordEvenData(String url, User user) {
        super(user);
        this.url = url;
        this.user = user;
    }
}
