package fr.guen.dev.centro.dto.request;

import fr.guen.dev.centro.enums.Role;
import fr.guen.dev.centro.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalid")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @StrongPassword
    private String password;

    @NotNull
    private Role role;

}
