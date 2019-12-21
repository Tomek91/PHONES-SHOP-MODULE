package pl.com.app.parsers.json;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.com.app.dto.ProfileParametersDTO;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
public class ProfilesParametersConverter extends JsonConverter<List<ProfileParametersDTO>> {
    @Value("${PROFILES_PARAMETERS}")
    private String filePath;

    @PostConstruct
    private void init(){
        this.setJsonFilename(filePath);
    }
}
