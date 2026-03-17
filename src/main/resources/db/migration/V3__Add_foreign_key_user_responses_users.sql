-- Добавление внешнего ключа между user_responses и users
-- Это обеспечит целостность данных между таблицами

-- Сначала добавим колонку user_ref_id в таблицу user_responses для связи с таблицей users
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'user_responses' 
        AND column_name = 'user_ref_id'
    ) THEN
        ALTER TABLE user_responses ADD COLUMN user_ref_id BIGINT;
    END IF;
END $$;

-- Заполняем user_ref_id на основе user_id (telegram_user_id) из таблицы users
UPDATE user_responses 
SET user_ref_id = u.id 
FROM users u 
WHERE user_responses.user_id = u.telegram_user_id;

-- Добавляем внешний ключ
ALTER TABLE user_responses 
ADD CONSTRAINT fk_user_responses_user 
FOREIGN KEY (user_ref_id) REFERENCES users(id) ON DELETE SET NULL;

-- Создаем индекс для ускорения запросов
CREATE INDEX IF NOT EXISTS idx_user_responses_user_ref_id ON user_responses(user_ref_id);

-- Устанавливаем NOT NULL constraint для user_ref_id (теперь все записи должны быть связаны с пользователями)
ALTER TABLE user_responses 
ALTER COLUMN user_ref_id SET NOT NULL;
