package de.sotterbeck.iumetro.domain.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HexFormat;

/**
 * Helper class for hashing.
 */
public final class Hashes {

    private Hashes() {
    }

    /**
     * Computes the SHA-256 hash of the given input string and returns the result as a hexadecimal string.
     *
     * @param input the input string to hash
     * @return the hexadecimal representation of the SHA-256 hash
     */
    public static String sha256Hex(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
