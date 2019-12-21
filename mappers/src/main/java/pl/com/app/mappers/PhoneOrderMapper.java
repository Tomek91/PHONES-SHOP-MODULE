package pl.com.app.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.com.app.dto.PhoneOrderDTO;
import pl.com.app.model.PhoneOrder;

@Mapper(componentModel = "spring")
public interface PhoneOrderMapper {
    @Mappings({
            @Mapping(source = "order", target = "orderDTO"),
            @Mapping(source = "phone", target = "phoneDTO")
    })
    PhoneOrderDTO phoneOrderToPhoneOrderDTO(PhoneOrder phoneOrder);

    @Mappings({
            @Mapping(source = "orderDTO", target = "order"),
            @Mapping(source = "phoneDTO", target = "phone")
    })
    PhoneOrder phoneOrderDTOToPhoneOrder(PhoneOrderDTO phoneOrderDTO);
}
