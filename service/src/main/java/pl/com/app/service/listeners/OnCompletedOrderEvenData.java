package pl.com.app.service.listeners;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;
import pl.com.app.model.User;

import java.math.BigDecimal;


@Getter
public class OnCompletedOrderEvenData extends ApplicationEvent {

    private final String url;
    private final User user;
    private final String phoneOrders;
    private final BigDecimal price;
    private final String qrCodePath;

    public OnCompletedOrderEvenData(String url, User user, String phoneOrders, BigDecimal price, String qrCodePath) {
        super(user);
        this.url = url;
        this.user = user;
        this.phoneOrders = phoneOrders;
        this.price = price;
        this.qrCodePath = qrCodePath;
    }
}
