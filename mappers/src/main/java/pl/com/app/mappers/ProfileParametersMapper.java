package pl.com.app.mappers;

import org.mapstruct.Mapper;
import pl.com.app.dto.ProfileParametersDTO;
import pl.com.app.model.ProfileParameters;

@Mapper(componentModel = "spring")
public interface ProfileParametersMapper {
    ProfileParametersDTO profileParametersToProfileParametersDTO(ProfileParameters profileParameters);
    ProfileParameters profileParametersDTOToProfileParameters(ProfileParametersDTO profileParametersDTO);
}
