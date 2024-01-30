package fr.guen.dev.centro.services.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    public String extractUserName(String token);

    public String generateToken(UserDetails userDetails);

    public boolean isTokenValid(String token, UserDetails userDetails);

    public ResponseCookie generateJwtCookie(String jwt);

    public String getJwtFromCookie(HttpServletRequest request);

    public ResponseCookie getCleanJwtCookie();
}
