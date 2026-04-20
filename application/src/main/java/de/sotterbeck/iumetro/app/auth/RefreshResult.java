package de.sotterbeck.iumetro.app.auth;

public sealed interface RefreshResult permits
        RefreshResult.Expired, RefreshResult.Invalid, RefreshResult.Revoked, RefreshResult.Success {

    record Success(String accessToken, long expiresIn) implements RefreshResult {

    }

    record Expired() implements RefreshResult {

        private static final RefreshResult INSTANCE = new Expired();

    }

    record Invalid() implements RefreshResult {

        private static final RefreshResult INSTANCE = new Invalid();

    }

    record Revoked() implements RefreshResult {

        private static final RefreshResult INSTANCE = new Revoked();

    }

    /**
     * Returns a {@link RefreshResult} instance representing an expired state.
     *
     * @return a singleton instance of {@code RefreshResult.Expired}.
     */
    static RefreshResult expired() {
        return Expired.INSTANCE;
    }

    /**
     * Returns a {@link RefreshResult} instance representing an invalid state,
     * like not found or tampered.
     *
     * @return a singleton instance of {@code RefreshResult.Invalid}.
     */
    static RefreshResult invalid() {
        return Invalid.INSTANCE;
    }

    /**
     * Returns a {@link RefreshResult} instance representing a revoked state.
     *
     * @return a singleton instance of {@code RefreshResult.Revoked}.
     */
    static RefreshResult revoked() {
        return Revoked.INSTANCE;
    }

}
