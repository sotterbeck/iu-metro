package de.sotterbeck.iumetro.infra.papermc.common.web;

/**
 * Represents a generic response type that can be either a successful fetch of data or a failure,
 * with a generic type parameter for handling custom data.
 *
 * @param <T> The type of data included in the response.
 */
public sealed interface ApiResponse<T> permits ApiResponse.Data, ApiResponse.Failure {

    record Data<T>(T data) implements ApiResponse<T> {

    }

    record Failure<T>(String message) implements ApiResponse<T> {

    }

    /**
     * Creates a successful {@code ApiResponse} instance containing the provided data.
     *
     * @param <T>  The type of the data being included in the response.
     * @param data The data to be included in the successful response.
     * @return An {@code ApiResponse} instance encapsulating the provided data.
     */
    static <T> ApiResponse<T> success(T data) {
        return new Data<>(data);
    }

    /**
     * Creates a failure {@code ApiResponse} instance with the specified error message.
     *
     * @param <T>     The type of the data that would have been included in a successful response.
     * @param message The error message describing the failure.
     * @return An {@code ApiResponse} instance representing the failure.
     */
    static <T> ApiResponse<T> failure(String message) {
        return new Failure<>(message);
    }

}
