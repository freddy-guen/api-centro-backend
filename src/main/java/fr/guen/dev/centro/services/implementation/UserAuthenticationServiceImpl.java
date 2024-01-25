package fr.guen.dev.centro.services.implementation;

import fr.guen.dev.centro.dto.request.LoginRequestDTO;
import fr.guen.dev.centro.dto.request.RegisterRequestDTO;
import fr.guen.dev.centro.dto.response.LoginResponseDTO;
import fr.guen.dev.centro.dto.response.RefreshTokenResponseDTO;
import fr.guen.dev.centro.dto.response.RegisterResponseDTO;
import fr.guen.dev.centro.enums.TokenType;
import fr.guen.dev.centro.models.User;
import fr.guen.dev.centro.repository.UserRepository;
import fr.guen.dev.centro.services.interfaces.JwtService;
import fr.guen.dev.centro.services.interfaces.RefreshTokenService;
import fr.guen.dev.centro.services.interfaces.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;


    @Override
    public RegisterResponseDTO register(RegisterRequestDTO requestDTO)
    {
        var user = User.builder()
                .nom(requestDTO.getNom())
                .prenom(requestDTO.getPrenom())
                .email(requestDTO.getEmail())
                .password(passwordEncoder.encode(requestDTO.getPassword()))
                .role(requestDTO.getRole())
                .build();
        user = userRepository.save(user);

        var roles = user.getRole().getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();

        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        RefreshTokenResponseDTO tokenInfo = RefreshTokenResponseDTO.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType(TokenType.BEARER.name())
                .build();

        return RegisterResponseDTO.builder()
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .roles(roles)
                .tokenInfo(tokenInfo)
                .build();
    }

    @Override
    public LoginResponseDTO login(LoginRequestDTO requestDTO)
    {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDTO.getEmail(), requestDTO.getPassword())
        );

        var user = userRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect."));

        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getId());
        RefreshTokenResponseDTO tokenInfo = RefreshTokenResponseDTO.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getToken())
                .tokenType(TokenType.BEARER.name())
                .build();

        return LoginResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .tokenInfo(tokenInfo)
                .build();
    }

}
