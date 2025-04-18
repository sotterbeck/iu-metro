DROP TABLE metro_connection_lines;

CREATE TABLE IF NOT EXISTS metro_station_lines
(
    id               BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    metro_station_id uuid   NOT NULL REFERENCES metro_stations (id) ON DELETE CASCADE,
    line_id          BIGINT NOT NULL REFERENCES metro_lines (id) ON DELETE CASCADE,
    UNIQUE (metro_station_id, line_id)
)