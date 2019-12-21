package pl.com.app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.com.app.dto.OrderDTO;
import pl.com.app.model.Order;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mappings({
            @Mapping(source = "user", target = "userDTO")
    })
    OrderDTO orderToOrderDTO(Order order);

    @Mappings({
            @Mapping(source = "userDTO", target = "user")
    })
    Order orderDTOToOrder(OrderDTO orderDTO);
}
