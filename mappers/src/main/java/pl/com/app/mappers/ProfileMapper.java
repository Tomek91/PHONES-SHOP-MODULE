package pl.com.app.mappers;

import org.mapstruct.Mapper;
import pl.com.app.dto.ProfileDTO;
import pl.com.app.model.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    ProfileDTO profileToProfileDTO(Profile profile);
    Profile profileDTOToProfile(ProfileDTO profileDTO);
}
