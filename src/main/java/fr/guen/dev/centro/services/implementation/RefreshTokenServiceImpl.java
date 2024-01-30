package fr.guen.dev.centro.services.implementation;

import fr.guen.dev.centro.dto.request.RefreshTokenRequestDTO;
import fr.guen.dev.centro.dto.response.RefreshTokenResponseDTO;
import fr.guen.dev.centro.enums.TokenType;
import fr.guen.dev.centro.exception.TokenException;
import fr.guen.dev.centro.models.RefreshToken;
import fr.guen.dev.centro.models.User;
import fr.guen.dev.centro.repository.RefreshTokenRepository;
import fr.guen.dev.centro.repository.UserRepository;
import fr.guen.dev.centro.services.interfaces.JwtService;
import fr.guen.dev.centro.services.interfaces.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration;

    @Value("${application.security.jwt.refresh-token.cookie-name}")
    private String refreshCookieName;

    @Override
    public RefreshToken createRefreshToken(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        RefreshToken refreshToken = RefreshToken.builder()
                .revoked(false)
                .user(user)
                .token(Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes()))
                .expiryDate(Instant.now().plusMillis(refreshExpiration))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public RefreshToken verifyExpiration(RefreshToken token) {
        if(token == null){
            log.error("Le token est null");
            throw new TokenException(null, "Le token est null");
        }
        if(token.getExpiryDate().compareTo(Instant.now()) < 0 ){
            refreshTokenRepository.delete(token);
            throw new TokenException(token.getToken(), "Le token a expiré. Veuillez vous authentifier.");
        }
        return token;
    }

    @Override
    public RefreshTokenResponseDTO generateNewToken(RefreshTokenRequestDTO request) {
        User user = refreshTokenRepository.findByToken(request.getRefreshToken())
                .map(this::verifyExpiration)
                .map(RefreshToken::getUser)
                .orElseThrow(() -> new TokenException(request.getRefreshToken(),"Le token d'actualisation n'exite pas."));

        String token = jwtService.generateToken(user);
        return RefreshTokenResponseDTO.builder()
                .accessToken(token)
                .refreshToken(request.getRefreshToken())
                .tokenType(TokenType.BEARER.name())
                .build();
    }

    @Override
    public ResponseCookie generateRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshCookieName, token)
                .path("/")
                .maxAge(refreshExpiration / 1000) //15 jour en secondes
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .build();
    }

    @Override
    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshCookieName);

        return cookie != null ? cookie.getValue() : StringUtils.EMPTY;
    }

    @Override
    public void deleteByToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    @Override
    public ResponseCookie getCleanRefreshTokenCookie() {
        return ResponseCookie.from(refreshCookieName, StringUtils.EMPTY)
                .path("/")
                .build();
    }


}
