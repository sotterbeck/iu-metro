CREATE TABLE IF NOT EXISTS metro_station_rail_markers
(
    marker_id  BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    station_id uuid NOT NULL REFERENCES metro_stations (id),
    pos_x      INT  NOT NULL,
    pos_y      INT  NOT NULL,
    pos_z      INT  NOT NULL,
    UNIQUE (pos_x, pos_y, pos_z)
)