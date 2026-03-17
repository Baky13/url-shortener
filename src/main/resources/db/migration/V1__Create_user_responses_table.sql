-- Создание таблицы для ответов пользователей
CREATE TABLE user_responses (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    username VARCHAR(255),
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    response_date DATE NOT NULL,
    response_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    answer VARCHAR(10) NOT NULL CHECK (answer IN ('YES', 'NO')),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание уникального индекса для предотвращения дублирования ответов в один день
CREATE UNIQUE INDEX idx_user_date_unique ON user_responses (user_id, response_date);

-- Создание индекса для быстрого поиска по дате
CREATE INDEX idx_response_date ON user_responses (response_date);

-- Создание таблицы для статистики бота
CREATE TABLE bot_statistics (
    id BIGSERIAL PRIMARY KEY,
    stat_date DATE NOT NULL UNIQUE,
    total_users BIGINT DEFAULT 0,
    yes_count BIGINT DEFAULT 0,
    no_count BIGINT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Создание индекса для быстрого поиска по дате статистики
CREATE INDEX idx_stat_date ON bot_statistics (stat_date);
