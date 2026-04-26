package de.sotterbeck.iumetro.app.auth;

import java.util.UUID;

public sealed interface TokenRotationDto permits TokenRotationDto.Success, TokenRotationDto.Failure {

    record Success(UUID userId, String userName, String role) implements TokenRotationDto {

    }

    sealed interface Failure extends TokenRotationDto {

        record NotFound() implements Failure {

        }

        record Expired() implements Failure {

        }

        record Revoked() implements Failure {

        }

    }

}
