package de.sotterbeck.iumetro.infra.papermc.auth;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ANYONE,
    AUTHENTICATED
}
