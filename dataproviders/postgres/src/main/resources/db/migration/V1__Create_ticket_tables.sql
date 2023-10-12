CREATE TABLE IF NOT EXISTS ticket_usage_limits
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    max_usages INT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS ticket_time_limits
(
    id         BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    time_limit BIGINT UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS tickets
(
    id             uuid PRIMARY KEY,
    name           TEXT   NOT NULL,
    usage_limit_id BIGINT NOT NULL REFERENCES ticket_usage_limits,
    time_limit_id  BIGINT NOT NULL REFERENCES ticket_time_limits
);

CREATE TABLE IF NOT EXISTS metro_stations
(
    id   uuid PRIMARY KEY,
    name TEXT NOT NULL
);

CREATE TYPE ticket_usage_type AS ENUM ('ENTER', 'EXIT');

CREATE TABLE IF NOT EXISTS ticket_usages
(
    id               uuid PRIMARY KEY                  DEFAULT gen_random_uuid(),
    ticket_id        uuid REFERENCES tickets,
    metro_station_id uuid REFERENCES metro_stations,
    timestamp        TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usage_type       ticket_usage_type        NOT NULL,
    UNIQUE (metro_station_id, ticket_id, timestamp)
);
