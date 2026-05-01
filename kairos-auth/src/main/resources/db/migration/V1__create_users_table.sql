CREATE SEQUENCE users_id_seq
    START WITH 1
    INCREMENT BY 10
    NO MINVALUE
    NO MAXVALUE CACHE 10;

CREATE TABLE users
(
    id              BIGINT PRIMARY KEY    DEFAULT nextval('users_id_seq'),
    username        VARCHAR(64)  NOT NULL,
    password        VARCHAR(255) NOT NULL,
    email           VARCHAR(255) NOT NULL UNIQUE,
    email_confirmed BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMP  NOT NULL DEFAULT NOW()
);

ALTER SEQUENCE users_id_seq OWNED BY users.id;

CREATE UNIQUE INDEX idx_users_email ON users (email);

ALTER TABLE users
    ADD CONSTRAINT chk_users_email_format
        CHECK (email ~* '^[A-Za-z0-9._%+\-]+@[A-Za-z0-9.\-]+\.[A-Za-z]{2,}$'),
    ADD CONSTRAINT chk_users_username_length
    CHECK (char_length (username) >= 3),
    ADD CONSTRAINT chk_users_password_length
    CHECK (char_length (password) >= 8);

COMMENT ON TABLE  users                    IS 'Пользователи сервиса авторизации';
COMMENT ON COLUMN users.id                 IS 'Уникальный идентификатор';
COMMENT ON COLUMN users.username           IS 'Отображаемое имя, 3-64 символа';
COMMENT ON COLUMN users.password           IS 'BCrypt хэш пароля';
COMMENT ON COLUMN users.email              IS 'Email для входа, уникальный';
COMMENT ON COLUMN users.email_confirmed    IS 'Подтверждён ли email';
COMMENT ON COLUMN users.created_at         IS 'Время регистрации';
COMMENT ON COLUMN users.updated_at         IS 'Время последнего изменения';

CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();