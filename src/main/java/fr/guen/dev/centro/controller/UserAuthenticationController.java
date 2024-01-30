package fr.guen.dev.centro.controller;

import fr.guen.dev.centro.dto.request.LoginRequestDTO;
import fr.guen.dev.centro.dto.request.RefreshTokenRequestDTO;
import fr.guen.dev.centro.dto.request.RegisterRequestDTO;
import fr.guen.dev.centro.dto.response.LoginResponseDTO;
import fr.guen.dev.centro.dto.response.RefreshTokenResponseDTO;
import fr.guen.dev.centro.dto.response.RegisterResponseDTO;
import fr.guen.dev.centro.services.interfaces.JwtService;
import fr.guen.dev.centro.services.interfaces.RefreshTokenService;
import fr.guen.dev.centro.services.interfaces.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthenticationController {

    private final UserAuthenticationService userAuthenticationService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseDTO> register(@Valid @RequestBody RegisterRequestDTO requestDTO)
    {
        return ResponseEntity.ok(userAuthenticationService.register(requestDTO));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO requestDTO)
    {
        LoginResponseDTO responseDTO = userAuthenticationService.login(requestDTO);

        ResponseCookie jwtCookie = jwtService.generateJwtCookie(responseDTO.getTokenInfo().getAccessToken());
        ResponseCookie refreshCookie = refreshTokenService.generateRefreshTokenCookie(responseDTO.getTokenInfo().getRefreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(responseDTO);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO requestDTO)
    {
        return ResponseEntity.ok(refreshTokenService.generateNewToken(requestDTO));
    }

    @PostMapping("/refresh-token-cookie")
    public ResponseEntity<Void> refreshTokenCookie(HttpServletRequest request)
    {
        String refreshToken = refreshTokenService.getRefreshTokenFromCookie(request);
        RefreshTokenResponseDTO refreshTokenResponseDTO = refreshTokenService.generateNewToken(new RefreshTokenRequestDTO(refreshToken));

        ResponseCookie newJwtCookie = jwtService.generateJwtCookie(refreshTokenResponseDTO.getAccessToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, newJwtCookie.toString())
                .build();
    }

    @GetMapping("/info")
    public Authentication getAuthentication(@RequestBody LoginRequestDTO request){
        return  authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),request.getPassword()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request){

        String refreshToken = refreshTokenService.getRefreshTokenFromCookie(request);

        //Suppression du refreshToken de la base
        if(refreshToken != null) {
            refreshTokenService.deleteByToken(refreshToken);
        }

        //Suppression des cookies
        ResponseCookie jwtCookie = jwtService.getCleanJwtCookie();
        ResponseCookie refreshTokenCookie = refreshTokenService.getCleanRefreshTokenCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE,refreshTokenCookie.toString())
                .build();
    }
}
