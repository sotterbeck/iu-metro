ALTER TABLE tickets
DROP
COLUMN IF EXISTS usage_limit_id,
    DROP
COLUMN IF EXISTS time_limit_id,
    ADD COLUMN IF NOT EXISTS config JSONB NOT NULL DEFAULT '{"constraints":[]}';

ALTER TABLE retail_tickets
DROP
COLUMN IF EXISTS usage_limit_id,
    DROP
COLUMN IF EXISTS time_limit_id,
    ADD COLUMN IF NOT EXISTS config JSONB NOT NULL DEFAULT '{"constraints":[]}';

DROP TABLE if EXISTS ticket_usage_limits CASCADE;
DROP TABLE if EXISTS ticket_time_limits CASCADE;
