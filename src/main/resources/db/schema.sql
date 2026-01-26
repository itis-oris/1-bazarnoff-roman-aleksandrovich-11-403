-- Таблица пользователей
CREATE TABLE users
(
    id                BIGSERIAL PRIMARY KEY,
    email             VARCHAR(255) UNIQUE NOT NULL,
    password_hash     VARCHAR(255)        NOT NULL,
    full_name         VARCHAR(255)        NOT NULL,
    university        VARCHAR(255),
    faculty           VARCHAR(255),
    course            INT,
    reputation_points INT       DEFAULT 0,
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    avatar_url        VARCHAR(500),
    about             TEXT
);


-- Таблица предметов
CREATE TABLE subject
(
    id                 BIGSERIAL PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    description        TEXT,
    faculty            VARCHAR(255),
    semester           INT,
    created_by_user_id BIGINT       REFERENCES users (id) ON DELETE SET NULL,
    created_at         TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TABLE help_request
(
    id          BIGSERIAL PRIMARY KEY,
    author_id   BIGINT REFERENCES users (id) ON DELETE CASCADE,
    subject_id  BIGINT REFERENCES subject (id) ON DELETE CASCADE,
    title       VARCHAR(500) NOT NULL,
    description TEXT         NOT NULL,
    status      VARCHAR(50) DEFAULT 'OPEN', -- открыт запрос (open) / запрос в прогрессе (in_progress) / запрос закрыт (closed)
    created_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    closed_at   TIMESTAMP
);

ALTER TABLE help_request ADD COLUMN author_name VARCHAR()

CREATE TABLE materials
(
    id              BIGSERIAL PRIMARY KEY,
    author_id       BIGINT REFERENCES users (id) ON DELETE CASCADE,
    subject_id      BIGINT REFERENCES subject (id) ON DELETE CASCADE,
    title           VARCHAR(500) NOT NULL,
    description     TEXT,
    material_type   VARCHAR(500),
    file_path       VARCHAR(500),
    upload_date     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    downloads_count INT       DEFAULT 0
);


CREATE TABLE users_subject
(
    user_id           BIGINT REFERENCES users (id) ON DELETE CASCADE,
    subject_id        BIGINT REFERENCES subject (id) ON DELETE CASCADE,
    proficiency_level VARCHAR(50),
    can_help          BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, subject_id)
);

CREATE TABLE responses
(
    id              BIGSERIAL PRIMARY KEY,
    help_request_id BIGINT NOT NULL REFERENCES help_request (id) ON DELETE CASCADE,
    responder_id    BIGINT NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    message         TEXT   NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_accepted     BOOLEAN   DEFAULT FALSE
);



CREATE INDEX idx_help_request_author ON help_request(author_id);
CREATE INDEX idx_help_request_subject ON help_request(subject_id);
CREATE INDEX idx_help_request_status ON help_request(status);
CREATE INDEX idx_materials_subject ON materials(subject_id);
CREATE INDEX idx_responses_request ON responses(help_request_id);
CREATE INDEX idx_users_email ON users(email);