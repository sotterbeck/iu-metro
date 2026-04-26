package de.sotterbeck.iumetro.app.auth;

import java.util.UUID;

public interface TokenProvider {

    Token generateAccessToken(UUID userId, String userName, String role);

    TokenValidationResult validate(String token);

}
