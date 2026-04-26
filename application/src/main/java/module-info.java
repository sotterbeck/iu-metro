module iumetro.application {
    requires org.jetbrains.annotations;
    requires iumetro.domain;
    requires com.fasterxml.jackson.annotation;
    requires com.auth0.jwt;
    exports de.sotterbeck.iumetro.app.ticket;
    exports de.sotterbeck.iumetro.app.faregate;
    exports de.sotterbeck.iumetro.app.station;
    exports de.sotterbeck.iumetro.app.retail;
    exports de.sotterbeck.iumetro.app.common;
    exports de.sotterbeck.iumetro.app.network.line;
    exports de.sotterbeck.iumetro.app.network.graph;
    exports de.sotterbeck.iumetro.app.auth;
}
