CREATE TABLE jobs (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    type       VARCHAR(50)  NOT NULL,
    user_id    BIGINT       NOT NULL REFERENCES users (id),
    status     VARCHAR(20)  NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);
