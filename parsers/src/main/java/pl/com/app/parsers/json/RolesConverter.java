package pl.com.app.parsers.json;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.com.app.dto.RoleDTO;

import javax.annotation.PostConstruct;
import java.util.List;


@Component
public class RolesConverter extends JsonConverter<List<RoleDTO>> {
    @Value("${ROLES}")
    private String filePath;

    @PostConstruct
    private void init(){
        this.setJsonFilename(filePath);
    }
}
