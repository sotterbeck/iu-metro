package de.sotterbeck.iumetro.app.auth;

import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for generating secure tokens using a cryptographically strong random number generator.
 * The generated tokens are URL-safe Base64-encoded strings without padding.
 * This class is designed to be used for creating high-entropy, non-guessable tokens.
 */
public class SecureTokenGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    /**
     * Generates a secure token using a cryptographically strong random number generator.
     *
     * @return a URL-safe Base64-encoded string without padding
     */
    public String generateSecureToken() {
        var row = new byte[32];
        random.nextBytes(row);
        return encoder.encodeToString(row);

    }

}
