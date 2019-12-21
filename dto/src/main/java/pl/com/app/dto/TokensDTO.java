package pl.com.app.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokensDTO {
    private final String accessToken;
    private final String refreshToken;
}
