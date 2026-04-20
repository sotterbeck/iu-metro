package de.sotterbeck.iumetro.app.auth;

public sealed interface VerifyResult permits VerifyResult.Success, VerifyResult.Expired, VerifyResult.Invalid {

    record Success(String accessToken, String refreshToken, long expiresIn) implements VerifyResult {

    }

    record Expired() implements VerifyResult {

        private static final VerifyResult INSTANCE = new Expired();

    }

    record Invalid() implements VerifyResult {

        private static final VerifyResult INSTANCE = new Invalid();

    }

    /**
     * Returns a {@link VerifyResult} instance representing an expired state.
     *
     * @return a singleton instance of {@code VerifyResult.Expired}.
     */
    static VerifyResult expired() {
        return Expired.INSTANCE;
    }

    /**
     * Returns a {@link VerifyResult} instance representing an invalid state,
     * like not found, tampered, or reused.
     *
     * @return a singleton instance of {@code VerifyResult.Invalid}.
     */
    static VerifyResult invalid() {
        return Invalid.INSTANCE;
    }

}
