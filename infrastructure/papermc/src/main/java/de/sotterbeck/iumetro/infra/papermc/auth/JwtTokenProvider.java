package de.sotterbeck.iumetro.infra.papermc.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifier.BaseVerification;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import de.sotterbeck.iumetro.app.auth.TokenProvider;
import de.sotterbeck.iumetro.app.auth.TokenValidationResult;

import java.time.Clock;
import java.util.UUID;

public final class JwtTokenProvider implements TokenProvider {

    private final long expiration;
    private final String issuer;
    private final Clock clock;

    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    private JwtTokenProvider(Builder builder) {
        expiration = builder.expiration;
        issuer = builder.issuer;
        clock = builder.clock;
        algorithm = Algorithm.HMAC256(builder.secret);
        verifier = ((BaseVerification) JWT.require(algorithm).withIssuer(issuer)).build(builder.clock);
    }

    @Override
    public String generateAccessToken(UUID userId, String userName, String role) {
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userId.toString())
                .withClaim("userName", userName)
                .withClaim("role", role)
                .withIssuedAt(clock.instant())
                .withExpiresAt(clock.instant().plusMillis(expiration))
                .sign(algorithm);
    }

    @Override
    public TokenValidationResult validate(String token) {
        try {
            var decodedJWT = verifier.verify(token);
            var userId = UUID.fromString(decodedJWT.getSubject());
            var userName = decodedJWT.getClaim("userName").asString();
            var role = decodedJWT.getClaim("role").asString();
            return new TokenValidationResult.Success(userId, userName, role);
        } catch (TokenExpiredException e) {
            return new TokenValidationResult.Expired();
        } catch (JWTVerificationException e) {
            return new TokenValidationResult.Invalid();
        }
    }

    public static class Builder {

        private String secret;
        private long expiration;
        private String issuer;
        private Clock clock = Clock.systemUTC();

        /**
         * Secret for the token signing.
         *
         * @param secret the secret
         * @return this builder
         */
        public Builder secret(String secret) {
            this.secret = secret;
            return this;
        }

        /**
         * Expiration in milliseconds.
         *
         * @param expiration the expiration in milliseconds
         * @return this builder
         */
        public Builder expiration(long expiration) {
            this.expiration = expiration;
            return this;
        }

        /**
         * The issuer of the token.
         *
         * @param issuer the issuer of the token
         * @return this builder
         */
        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public JwtTokenProvider build() {
            return new JwtTokenProvider(this);
        }

    }

}
