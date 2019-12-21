package pl.com.app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.app.dto.enums.SortType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SortDataDTO {
    private SortType sortType;
    private boolean descending;
}
