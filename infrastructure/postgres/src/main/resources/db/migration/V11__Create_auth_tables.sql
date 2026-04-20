CREATE TABLE magic_link_tokens
(
    token_hash VARCHAR(64) PRIMARY KEY,
    user_id    uuid        NOT NULL,
    user_name  VARCHAR(16) NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now(),
    expires_at timestamptz NOT NULL
);

CREATE TABLE refresh_tokens
(
    id         uuid PRIMARY KEY     DEFAULT gen_random_uuid(),
    user_id    uuid        NOT NULL,
    user_name  VARCHAR(16) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    expires_at timestamptz NOT NULL,
    revoked_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE INDEX idx_refresh_tokens_user_uuid ON refresh_tokens (user_id);