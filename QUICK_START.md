# Быстрый запуск Telegram бота для обедов

## 🚀 Что нужно сделать перед запуском

### 1. Настройка Telegram бота
1. Найдите в Telegram `@BotFather`
2. Отправьте `/newbot`
3. Создайте бота и сохраните **токен** и **username**
4. Добавьте бота в групповой чат
5. Получите **chat ID** через `@userinfobot` или API

### 2. Настройка базы данных
```sql
CREATE DATABASE lunch_bot;
CREATE USER lunch_bot_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE lunch_bot TO lunch_bot_user;
```

### 3. Конфигурация приложения
Откройте `src/main/resources/application.properties`:

```properties
# Замените на ваши данные
telegram.bot.bot-token=1234567890:ABCdefGHIjklMNOpqrsTUVwxyz
telegram.bot.bot-username=your_lunch_bot
telegram.bot.chat-id=-1001234567890

# Пароль от базы данных
spring.datasource.password=your_password
```

## 🏃‍♂️ Запуск

### Способ 1: Через Maven
```bash
.\mvnw.cmd spring-boot:run
```

### Способ 2: Через JAR файл
```bash
.\mvnw.cmd clean package
java -jar target\Lunch_TG_BOT-0.0.1-SNAPSHOT.jar
```

## 🎮 Первые команды

1. Отправьте боту `/start` для инициализации
2. Проверьте работу командой `/test`
3. Установите имя через `/setname`
4. Протестируйте вопрос об обеде через `/testlunch`

## 📋 Команды бота

- `/start` - приветствие
- `/lunch` - вопрос об обеде
- `/list` - список обедающих
- `/setname` - установить имя
- `/test` - тестовое уведомление
- `/testlunch` - тестовый вопрос
- `/trigger` - отправить всем
- `/help` - справка

## ⏰ Автоматическая работа

- **10:00** - отправка вопросов всем
- **Каждые 30 мин** - напоминания
- **11:30** - финальное напоминание
- **12:00** - итоговый список

## 🔧 Если что-то не работает

1. Проверьте токен и chat ID в `application.properties`
2. Убедитесь что база данных запущена
3. Проверьте логи в консоли
4. Убедитесь что бот добавлен в чат

## 📝 База данных

При первом запуске автоматически создаются таблицы:
- `users` - пользователи бота
- `user_responses` - ответы на опросы
- `bot_statistics` - статистика

## 🎉 Готово!

Бот готов к использованию! Пользователи могут отвечать на вопросы об обеде, устанавливать свои имена и получать автоматические напоминания.
