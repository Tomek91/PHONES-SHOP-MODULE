package pl.com.app.parsers.json;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.com.app.dto.ProfileDTO;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
public class ProfilesConverter extends JsonConverter<List<ProfileDTO>> {
    @Value("${PROFILES}")
    private String filePath;

    @PostConstruct
    private void init(){
        this.setJsonFilename(filePath);
    }
}
