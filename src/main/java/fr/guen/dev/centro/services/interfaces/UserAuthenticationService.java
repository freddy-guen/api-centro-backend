package fr.guen.dev.centro.services.interfaces;

import fr.guen.dev.centro.dto.request.LoginRequestDTO;
import fr.guen.dev.centro.dto.request.RegisterRequestDTO;
import fr.guen.dev.centro.dto.response.LoginResponseDTO;
import fr.guen.dev.centro.dto.response.RegisterResponseDTO;

public interface UserAuthenticationService {

    public RegisterResponseDTO register(final RegisterRequestDTO requestDTO);

    public LoginResponseDTO login(final LoginRequestDTO requestDTO);

}
