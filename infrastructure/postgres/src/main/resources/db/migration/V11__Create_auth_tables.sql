CREATE TABLE IF NOT EXISTS users
(
    id         uuid PRIMARY KEY,
    name       VARCHAR(16) NOT NULL,
    role       VARCHAR(16) NOT NULL DEFAULT 'player',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS magic_link_tokens
(
    token_hash VARCHAR(64) PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES users (id),
    created_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz NOT NULL
);

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id         uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id uuid NOT NULL REFERENCES users (id),
    token_hash VARCHAR(64) NOT NULL,
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens (user_id);
