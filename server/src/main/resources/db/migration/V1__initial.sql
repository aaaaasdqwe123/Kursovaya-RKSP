CREATE TABLE users (
 id UUID PRIMARY KEY, email VARCHAR(254) UNIQUE NOT NULL,
 password_hash TEXT NOT NULL, name VARCHAR(80) NOT NULL,
 phone VARCHAR(16) UNIQUE NOT NULL, dark_theme BOOLEAN NOT NULL DEFAULT FALSE,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE accounts (
 id UUID PRIMARY KEY, user_id UUID UNIQUE NOT NULL REFERENCES users(id),
 balance BIGINT NOT NULL DEFAULT 0 CHECK (balance >= 0),
 currency CHAR(3) NOT NULL DEFAULT 'RUB' CHECK (currency = 'RUB')
);
-- Virtual references only. No PAN, CVV or external bank card credentials.
CREATE TABLE cards (
 id UUID PRIMARY KEY, account_id UUID NOT NULL REFERENCES accounts(id),
 alias VARCHAR(40) NOT NULL, last4 CHAR(4) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE services (
 id VARCHAR(32) PRIMARY KEY, title VARCHAR(100) NOT NULL,
 account_id UUID NOT NULL REFERENCES accounts(id)
);
CREATE TABLE transactions (
 id UUID PRIMARY KEY, from_account UUID NOT NULL REFERENCES accounts(id),
 to_account UUID NOT NULL REFERENCES accounts(id), amount BIGINT NOT NULL CHECK(amount > 0),
 note VARCHAR(200) NOT NULL, kind VARCHAR(10) NOT NULL CHECK(kind IN ('PHONE','CARD','SERVICE')),
 idempotency_key UUID NOT NULL, request_hash CHAR(64) NOT NULL,
 created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
 CHECK(from_account <> to_account), UNIQUE(from_account, idempotency_key)
);
CREATE INDEX transactions_from_time ON transactions(from_account, created_at DESC, id DESC);
CREATE INDEX transactions_to_time ON transactions(to_account, created_at DESC, id DESC);
CREATE TABLE search_history (
 user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 item_id VARCHAR(80) NOT NULL, selected_at TIMESTAMPTZ NOT NULL DEFAULT clock_timestamp(),
 PRIMARY KEY(user_id,item_id)
);
-- Revocable, expiring JWT sessions. Store jti, never bearer tokens.
CREATE TABLE sessions (
 id UUID PRIMARY KEY, user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
 expires_at TIMESTAMPTZ NOT NULL
);
CREATE INDEX sessions_expiry ON sessions(expires_at);
