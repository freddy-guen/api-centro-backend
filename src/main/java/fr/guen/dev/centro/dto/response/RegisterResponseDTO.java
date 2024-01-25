package fr.guen.dev.centro.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {

    private String nom;

    private String prenom;

    private String email;

    private Boolean enabled;

    private List<String> roles;

    @JsonProperty("token_info")
    RefreshTokenResponseDTO tokenInfo;

}
