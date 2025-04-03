ALTER TABLE ticket_time_limits
    ALTER COLUMN time_limit TYPE INTERVAL
        USING MAKE_INTERVAL(secs => ticket_time_limits.time_limit * 1000000000);
