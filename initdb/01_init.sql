-- ========================================================
-- users : ユーザー
-- ========================================================
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL       PRIMARY KEY,
    display_name    VARCHAR(64)     NOT NULL,
    email           VARCHAR(255)    NOT NULL,
    auth_provider   VARCHAR(32)     NOT NULL,  -- "firebase" など
    auth_uid        VARCHAR(255)    NOT NULL,  -- Firebase UID など
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS users_email_uq
    ON users (email);

CREATE UNIQUE INDEX IF NOT EXISTS users_auth_provider_uid_uq
    ON users (auth_provider, auth_uid);



-- ========================================================
-- regulations : レギュレーションマスタ
-- ========================================================
CREATE TABLE IF NOT EXISTS regulations (
    id          BIGSERIAL       PRIMARY KEY,
    code        VARCHAR(32)     NOT NULL,      -- "STANDARD" など
    name        VARCHAR(64)     NOT NULL,
    description TEXT,
    valid_from  DATE,
    valid_to    DATE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS regulations_code_uq
    ON regulations (code);



-- ========================================================
-- card_types : カード種別マスタ
-- ========================================================
CREATE TABLE IF NOT EXISTS card_types (
    id      BIGSERIAL       PRIMARY KEY,
    code    VARCHAR(32)     NOT NULL,      -- "POKEMON" など
    name    VARCHAR(64)     NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS card_types_code_uq
    ON card_types (code);



-- ========================================================
-- cards : カードマスタ
-- ========================================================
CREATE TABLE IF NOT EXISTS cards (
    id                  BIGSERIAL       PRIMARY KEY,
    name                VARCHAR(255)    NOT NULL,
    number              VARCHAR(64),
    set_name            VARCHAR(255),
    regulation_id       BIGINT          REFERENCES regulations(id),
    card_type_id        BIGINT          REFERENCES card_types(id),
    image_url           TEXT,
    created_by_user_id  BIGINT          REFERENCES users(id),
    created_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS cards_name_idx
    ON cards (name);

CREATE INDEX IF NOT EXISTS cards_number_idx
    ON cards (number);

CREATE INDEX IF NOT EXISTS cards_regulation_idx
    ON cards (regulation_id);



-- ========================================================
-- user_cards : ユーザー所持カード
-- ========================================================
CREATE TABLE IF NOT EXISTS user_cards (
    id          BIGSERIAL       PRIMARY KEY,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    card_id     BIGINT          NOT NULL REFERENCES cards(id) ON DELETE CASCADE,
    quantity    INTEGER         NOT NULL CHECK (quantity >= 0),
    condition   VARCHAR(64),
    location    VARCHAR(128),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS user_cards_user_card_uq
    ON user_cards (user_id, card_id);

CREATE INDEX IF NOT EXISTS user_cards_user_id_idx
    ON user_cards (user_id);

CREATE INDEX IF NOT EXISTS user_cards_card_id_idx
    ON user_cards (card_id);



-- ========================================================
-- decks : デッキ
-- ========================================================
CREATE TABLE IF NOT EXISTS decks (
    id              BIGSERIAL       PRIMARY KEY,
    user_id         BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name            VARCHAR(128)    NOT NULL,
    regulation_id   BIGINT          REFERENCES regulations(id),
    memo            TEXT,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS decks_user_id_idx
    ON decks (user_id);



-- ========================================================
-- deck_cards : デッキ内カード
-- ========================================================
CREATE TABLE IF NOT EXISTS deck_cards (
    id          BIGSERIAL       PRIMARY KEY,
    deck_id     BIGINT          NOT NULL REFERENCES decks(id) ON DELETE CASCADE,
    card_id     BIGINT          NOT NULL REFERENCES cards(id),
    quantity    INTEGER         NOT NULL CHECK (quantity > 0),
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS deck_cards_deck_card_uq
    ON deck_cards (deck_id, card_id);

CREATE INDEX IF NOT EXISTS deck_cards_deck_id_idx
    ON deck_cards (deck_id);



-- ========================================================
-- groups : グループ
-- ========================================================
CREATE TABLE IF NOT EXISTS groups (
    id              BIGSERIAL       PRIMARY KEY,
    name            VARCHAR(128)    NOT NULL,
    owner_user_id   BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    invite_code     VARCHAR(64)     NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS groups_invite_code_uq
    ON groups (invite_code);

CREATE INDEX IF NOT EXISTS groups_owner_user_id_idx
    ON groups (owner_user_id);



-- ========================================================
-- group_members : グループメンバー
-- ========================================================
CREATE TABLE IF NOT EXISTS group_members (
    id          BIGSERIAL       PRIMARY KEY,
    group_id    BIGINT          NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id     BIGINT          NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role        VARCHAR(32)     NOT NULL,      -- "OWNER" / "MEMBER"
    created_at  TIMESTAMPTZ     NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX IF NOT EXISTS group_members_group_user_uq
    ON group_members (group_id, user_id);

CREATE INDEX IF NOT EXISTS group_members_group_id_idx
    ON group_members (group_id);

CREATE INDEX IF NOT EXISTS group_members_user_id_idx
    ON group_members (user_id);
