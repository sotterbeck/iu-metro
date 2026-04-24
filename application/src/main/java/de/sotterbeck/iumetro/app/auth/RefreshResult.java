package de.sotterbeck.iumetro.app.auth;

public sealed interface RefreshResult permits RefreshResult.Success, RefreshResult.Failure {

    record Success(String accessToken, String refreshToken, long expiresIn) implements RefreshResult {

    }

    sealed interface Failure extends RefreshResult {

        record Expired() implements Failure {

            private static final Failure INSTANCE = new Expired();

        }

        record Invalid() implements Failure {

            private static final Failure INSTANCE = new Invalid();

        }

        record Revoked() implements Failure {

            private static final Failure INSTANCE = new Revoked();

        }
    }

    static RefreshResult success(String accessToken, String refreshToken, long expiresIn) {
        return new Success(accessToken, refreshToken, expiresIn);
    }

    static RefreshResult expired() {
        return Failure.Expired.INSTANCE;
    }

    static RefreshResult invalid() {
        return Failure.Invalid.INSTANCE;
    }

    static RefreshResult revoked() {
        return Failure.Revoked.INSTANCE;
    }

}
