package fr.guen.dev.centro.controller;

import fr.guen.dev.centro.dto.request.LoginRequestDTO;
import fr.guen.dev.centro.dto.request.RefreshTokenRequestDTO;
import fr.guen.dev.centro.dto.request.RegisterRequestDTO;
import fr.guen.dev.centro.dto.response.LoginResponseDTO;
import fr.guen.dev.centro.dto.response.RefreshTokenResponseDTO;
import fr.guen.dev.centro.dto.response.RegisterResponseDTO;
import fr.guen.dev.centro.services.interfaces.RefreshTokenService;
import fr.guen.dev.centro.services.interfaces.UserAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthenticationController {

    private final UserAuthenticationService userAuthenticationService;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO)
    {
        return ResponseEntity.ok(userAuthenticationService.register(requestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO)
    {
        return ResponseEntity.ok(userAuthenticationService.login(requestDTO));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO requestDTO)
    {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(requestDTO));
    }
}
