CREATE TABLE IF NOT EXISTS metro_station_aliases
(
    metro_station_id uuid PRIMARY KEY REFERENCES metro_stations (id) ON DELETE CASCADE,
    alias            TEXT NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS metro_station_positions
(
    metro_station_id uuid PRIMARY KEY REFERENCES metro_stations (id) ON DELETE CASCADE,
    pos_x            INT NOT NULL,
    pos_y            INT NOT NULL,
    pos_z            INT NOT NULL
);


