package de.sotterbeck.iumetro.app.auth;

public sealed interface VerifyResult permits VerifyResult.Success, VerifyResult.Failure {

    record Success(String accessToken, String refreshToken, long expiresIn) implements VerifyResult {

    }

    sealed interface Failure extends VerifyResult {

        record Expired() implements Failure {

            private static final Failure INSTANCE = new Expired();

        }

        record Invalid() implements Failure {

            private static final Failure INSTANCE = new Invalid();

        }
    }

    static VerifyResult success(String accessToken, String refreshToken, long expiresIn) {
        return new Success(accessToken, refreshToken, expiresIn);
    }

    static VerifyResult expired() {
        return Failure.Expired.INSTANCE;
    }

    static VerifyResult invalid() {
        return Failure.Invalid.INSTANCE;
    }

}
