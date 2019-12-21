package pl.com.app.mappers;

import org.mapstruct.Mapper;
import pl.com.app.dto.PhoneDTO;
import pl.com.app.model.Phone;

@Mapper(componentModel = "spring")
public interface PhoneMapper {
    PhoneDTO phoneToPhoneDTO(Phone phone);
    Phone phoneDTOToPhone(PhoneDTO phoneDTO);
}