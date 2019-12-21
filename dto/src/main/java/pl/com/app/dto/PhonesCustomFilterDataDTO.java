package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhonesCustomFilterDataDTO {
    private List<PhoneDTO> phoneListToShow;
    private List<PhoneDTO> phoneListBase;
}
