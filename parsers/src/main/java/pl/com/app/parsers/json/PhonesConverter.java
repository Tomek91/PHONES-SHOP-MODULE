package pl.com.app.parsers.json;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.com.app.dto.PhoneDTO;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
public class PhonesConverter extends JsonConverter<List<PhoneDTO>> {

    @Value("${PHONES}")
    private String filePath;

    @PostConstruct
    private void init(){
        this.setJsonFilename(filePath);
    }
}
