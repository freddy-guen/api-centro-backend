package fr.guen.dev.centro.services.interfaces;

import fr.guen.dev.centro.dto.request.RefreshTokenRequestDTO;
import fr.guen.dev.centro.dto.response.RefreshTokenResponseDTO;
import fr.guen.dev.centro.models.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface RefreshTokenService {

    public RefreshToken createRefreshToken(Long userId);

    public RefreshToken verifyExpiration(RefreshToken token);

    public RefreshTokenResponseDTO generateNewToken(RefreshTokenRequestDTO request);

    public ResponseCookie generateRefreshTokenCookie(String token);

    public String getRefreshTokenFromCookie(HttpServletRequest request);

    public void deleteByToken(String token);

    public ResponseCookie getCleanRefreshTokenCookie();
}
