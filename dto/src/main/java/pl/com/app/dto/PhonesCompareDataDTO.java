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
public class PhonesCompareDataDTO {
    private MinMaxDataDTO minMaxDataDTO;
    private List<PhoneDTO> phoneDTOList;
    private List<PhoneDTO> bestPhonesList;
}
