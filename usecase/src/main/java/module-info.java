module iumetro.usecase {
    requires org.jetbrains.annotations;
    requires iumetro.entity;
    exports de.sotterbeck.iumetro.usecase.ticket;
    exports de.sotterbeck.iumetro.usecase.faregate;
    exports de.sotterbeck.iumetro.usecase.station;
    exports de.sotterbeck.iumetro.usecase.retail;
    exports de.sotterbeck.iumetro.usecase.common;
    exports de.sotterbeck.iumetro.usecase.network.line;
    exports de.sotterbeck.iumetro.usecase.network.graph;
}