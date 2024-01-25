package fr.guen.dev.centro.services.interfaces;

import fr.guen.dev.centro.dto.request.RefreshTokenRequestDTO;
import fr.guen.dev.centro.dto.response.RefreshTokenResponseDTO;
import fr.guen.dev.centro.models.RefreshToken;

public interface RefreshTokenService {

    public RefreshToken createRefreshToken(Long userId);

    public RefreshToken verifyExpiration(RefreshToken token);

    public RefreshTokenResponseDTO generateNewToken(RefreshTokenRequestDTO request);
}
