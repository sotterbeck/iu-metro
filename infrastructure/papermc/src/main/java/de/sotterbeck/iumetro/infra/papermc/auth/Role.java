package de.sotterbeck.iumetro.infra.papermc.auth;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ANYONE(null),
    AUTHENTICATED(null),
    ADMIN("admin");

    private final String claimValue;

    /**
     * Constructor for Role enum.
     *
     * @param claimValue the claim value from the JWT token
     */
    Role(String claimValue) {
        this.claimValue = claimValue;
    }

    /**
     * Checks if the user role is permitted to access the route.
     *
     * @param userRole the user role
     * @return true if the user role is permitted, false otherwise
     */
    public boolean permits(String userRole) {
        if (this == ANYONE) {
            return true;
        }
        if (this == AUTHENTICATED) {
            return hasValidRole(userRole);
        }
        return claimValue != null && claimValue.equals(userRole);
    }

    private boolean hasValidRole(String userRole) {
        return userRole != null && !userRole.isBlank();
    }
}
