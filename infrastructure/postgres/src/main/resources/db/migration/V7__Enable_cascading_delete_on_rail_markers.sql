ALTER TABLE metro_station_rail_markers
    DROP CONSTRAINT metro_station_rail_markers_station_id_fkey,
    ADD CONSTRAINT metro_station_rail_markers_station_id_fkey
        FOREIGN KEY (station_id) REFERENCES metro_stations (id) ON DELETE CASCADE;