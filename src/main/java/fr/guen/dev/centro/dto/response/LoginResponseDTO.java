package fr.guen.dev.centro.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private Long id;

    private String email;

    @JsonProperty("token_info")
    RefreshTokenResponseDTO tokenInfo;

}
