package de.sotterbeck.iumetro.app.auth;

public sealed interface LogoutResult permits LogoutResult.Invalid, LogoutResult.NotFound, LogoutResult.Success {

    record Success() implements LogoutResult {

        private static final LogoutResult INSTANCE = new Success();

    }

    record NotFound() implements LogoutResult {

        private static final LogoutResult INSTANCE = new NotFound();

    }

    record Invalid() implements LogoutResult {

        private static final LogoutResult INSTANCE = new Invalid();

    }

    /**
     * Returns a {@link LogoutResult} instance representing a successful logout state.
     *
     * @return a singleton instance of {@code LogoutResult.Success}.
     */
    static LogoutResult success() {
        return Success.INSTANCE;
    }

    /**
     * Returns a {@link LogoutResult} instance representing a token not found state.
     *
     * @return a singleton instance of {@code LogoutResult.NotFound}.
     */
    static LogoutResult notFound() {
        return NotFound.INSTANCE;
    }

    /**
     * Returns a {@link LogoutResult} instance representing an invalid state,
     * like null or blank token.
     *
     * @return a singleton instance of {@code LogoutResult.Invalid}.
     */
    static LogoutResult invalid() {
        return Invalid.INSTANCE;
    }

}
