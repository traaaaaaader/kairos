CREATE SEQUENCE servers_id_seq
    START WITH 1
    INCREMENT BY 10
    MINVALUE 1
    NO MAXVALUE
    CACHE 10;

CREATE SEQUENCE channels_id_seq
    START WITH 1
    INCREMENT BY 10
    MINVALUE 1
    NO MAXVALUE
    CACHE 10;

CREATE SEQUENCE server_invites_id_seq
    START WITH 1
    INCREMENT BY 10
    MINVALUE 1
    NO MAXVALUE
    CACHE 10;

CREATE TYPE channel_type AS ENUM ('TEXT', 'VOICE');

CREATE TABLE servers
(
    id         BIGINT       PRIMARY KEY DEFAULT nextval('servers_id_seq'),
    name       VARCHAR(100) NOT NULL,
    owner_id   BIGINT       NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL    DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL    DEFAULT NOW()
);

ALTER SEQUENCE servers_id_seq OWNED BY servers.id;

CREATE TABLE channels
(
    id         BIGINT       PRIMARY KEY DEFAULT nextval('channels_id_seq'),
    server_id  BIGINT       NOT NULL REFERENCES servers (id) ON DELETE CASCADE,
    name       VARCHAR(100) NOT NULL,
    type       channel_type NOT NULL    DEFAULT 'TEXT',
    position   INT          NOT NULL    DEFAULT 0,
    created_at TIMESTAMPTZ  NOT NULL    DEFAULT NOW(),
    updated_at TIMESTAMPTZ  NOT NULL    DEFAULT NOW()
);

ALTER SEQUENCE channels_id_seq OWNED BY channels.id;

CREATE TABLE server_members
(
    server_id BIGINT      NOT NULL REFERENCES servers (id) ON DELETE CASCADE,
    user_id   BIGINT      NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (server_id, user_id)
);

CREATE TABLE server_invites
(
    id         BIGINT      PRIMARY KEY DEFAULT nextval('server_invites_id_seq'),
    server_id  BIGINT      NOT NULL REFERENCES servers (id) ON DELETE CASCADE,
    code       VARCHAR(10) NOT NULL UNIQUE,
    created_by BIGINT      NOT NULL,
    expires_at TIMESTAMPTZ,
    max_uses   INT,
    uses       INT         NOT NULL    DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL    DEFAULT NOW()
);

ALTER SEQUENCE server_invites_id_seq OWNED BY server_invites.id;

CREATE INDEX idx_servers_owner_id ON servers (owner_id);
CREATE INDEX idx_channels_server_id ON channels (server_id);
CREATE INDEX idx_members_server_id ON server_members (server_id);
CREATE INDEX idx_members_user_id ON server_members (user_id);
CREATE INDEX idx_invites_code ON server_invites (code);
CREATE INDEX idx_invites_server_id ON server_invites (server_id);

CREATE OR REPLACE FUNCTION update_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER servers_updated_at
    BEFORE UPDATE ON servers
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER channels_updated_at
    BEFORE UPDATE ON channels
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();