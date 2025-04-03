CREATE TABLE IF NOT EXISTS ticket_categories
(
    id   BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name TEXT UNIQUE NOT NULL
);


CREATE TABLE IF NOT EXISTS retail_tickets
(
    id             uuid PRIMARY KEY,
    name           TEXT        NOT NULL UNIQUE,
    description    TEXT        NOT NULL,
    price_cents    BIGINT      NOT NULL,
    category_id    BIGINT REFERENCES ticket_categories (id),
    usage_limit_id BIGINT      NOT NULL REFERENCES ticket_usage_limits,
    time_limit_id  BIGINT      NOT NULL REFERENCES ticket_time_limits,
    is_active      BOOLEAN     NOT NULL,
    created_at     timestamptz NOT NULL
);

CREATE TABLE metro_lines
(
    id    BIGINT NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    name  TEXT   NOT NULL UNIQUE,
    color INTEGER
);

CREATE TABLE metro_connections
(
    id              BIGINT  NOT NULL PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    from_station_id uuid    NOT NULL REFERENCES metro_stations (id),
    to_station_id   uuid    NOT NULL REFERENCES metro_stations (id),
    distance        INTEGER NOT NULL,
    UNIQUE (from_station_id, to_station_id),
    CHECK ( from_station_id != to_station_id )
);

CREATE TABLE metro_connection_lines
(
    line_id         BIGINT REFERENCES metro_lines (id),
    connection_id   BIGINT  NOT NULL REFERENCES metro_connections (id),
    sequence_number INTEGER NOT NULL,
    PRIMARY KEY (line_id, connection_id)
);
