package de.sotterbeck.iumetro.app.auth;

import java.util.UUID;

/**
 * Represents the result of a token validation process.
 * This sealed interface allows for handling various outcomes
 * of token validation, such as successful validation, invalid tokens,
 * or expired tokens.
 * <p>
 * Important: For security reasons, do not distinguish between Invalid and Expired
 * when presenting errors to the user interface. Both cases must result in a 401
 * unauthorized response without revealing the specific failure reason.
 */
public sealed interface TokenValidationResult permits TokenValidationResult.Success, TokenValidationResult.Invalid, TokenValidationResult.Expired {

    record Success(UUID userId, String userName, String role) implements TokenValidationResult {

    }

    record Invalid() implements TokenValidationResult {

    }

    record Expired() implements TokenValidationResult {

    }

}
