# 🍽️ Lunch Telegram Bot

Telegram бот для управления обедами в команде с автоматическими напоминаниями и списками участников.

## 🚀 Быстрый старт для нового разработчика

### 1. Клонирование и базовая настройка
```bash
git clone <repository-url>
cd Lunch_TG_BOT
```

### 2. Настройка переменных окружения (ОБЯЗАТЕЛЬНО!)

Создайте файл `.env` в корне проекта с вашими данными:
```bash
# Telegram Bot Configuration
TELEGRAM_BOT_TOKEN=ВАШ_ТОКЕН_ОТ_BOTFATHER
TELEGRAM_CHAT_ID=ВАШ_CHAT_ID

# Database Configuration  
DB_PASSWORD=ПАРОЛЬ_ПОСТГРЕС
```

### 3. Настройка базы данных PostgreSQL
```sql
CREATE DATABASE lunch_bot;
CREATE USER lunch_bot WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE lunch_bot TO lunch_bot;
```

### 4. Создание бота в Telegram
1. Найдите @BotFather в Telegram
2. Отправьте `/newbot`
3. Имя: `Lunch Helper Bot`
4. Username: `lunch_helper_v2_bot`
5. Скопируйте полученный токен в `.env` файл

### 5. Запуск приложения
```bash
# Через Maven Wrapper
./mvnw spring-boot:run

# Или через IDE (IntelliJ IDEA)
# LunchTgBotApplication.java → Right Click → Run
```

## 📋 Команды бота
| Команда | Описание |
|---------|----------|
| `/start` | Регистрация пользователя |
| `/lunch` | Вопрос об обеде с кнопками |
| `/list` | Список обедающих сегодня |
| `/setname` | Установить отображаемое имя |
| `/help` | Справка по командам |

## ⏰ Автоматизация
- **10:00** - Утренний вопрос об обеде (только Пн-Пт)
- **Каждые 30 минут** - Напоминания для неответивших (10:00-14:00)  
- **11:30** - Финальный список обедающих (только Пн-Пт)

## 🔒 Безопасность
- Все секреты хранятся в переменных окружения
- `.env` файл добавлен в `.gitignore`
- Токены никогда не хранятся в коде
- Используются подготовленные statement для SQL

## 🏗️ Архитектура проекта
```
src/main/java/com/example/lunch_tg_bot/
├── config/          # Конфигурация приложения
├── model/           # Сущности базы данных
├── repository/       # Репозитории Spring Data
├── service/         # Бизнес-логика
└── LunchTgBotApplication.java  # Главный класс
```

## 🐛 Отладка
```bash
# Уровень логирования (добавить в application.properties)
logging.level.com.example.lunch_tg_bot=DEBUG

# Просмотр логов
tail -f logs/application.log
```

## 🚀 Развертывание
### Production
1. Установите переменные окружения на сервере
2. Настройте PostgreSQL базу данных
3. Разверните приложение:
```bash
java -jar target/Lunch_TG_BOT-0.0.1-SNAPSHOT.jar
```

## 🤝 Участие в разработке
1. Fork проекта
2. Создайте feature branch: `git checkout -b feature/new-feature`
3. Внесите изменения
4. Создайте Pull Request

---

**🎉 Приятного использования!**

- 🤔 Ежедневный вопрос об обеде с кнопками "Да" и "Нет"
- ⏰ Автоматические напоминания для тех, кто не ответил
- 📋 Формирование списков обедающих и не обедающих
- 👤 Возможность установки персональных имен через команду `/setname`
- 🕐 Работа только в будние дни (пн-пт)
- 📊 Сохранение истории ответов в базе данных
- 🎯 Персональные уведомления для каждого пользователя

## Расписание работы

- **10:00** - Первый вопрос об обеде
- **Каждые 30 минут** - Напоминания для не ответивших (10:00-11:45)
- **11:30** - Последнее напоминание с текущим списком
- **12:00** - Финальный список обедающих

## Команды бота

- `/start` - Приветствие и краткая справка
- `/lunch` - Ответить на вопрос об обеде
- `/list` - Посмотреть список обедающих сегодня
- `/setname` - Установить свое имя для бота
- `/test` - Отправить тестовое уведомление
- `/testlunch` - Отправить тестовый вопрос об обеде
- `/trigger` - Отправить вопросы всем пользователям вручную
- `/help` - Показать справку

## Настройка и запуск

### 1. Создание Telegram бота

1. Найдите в Telegram `@BotFather`
2. Отправьте команду `/newbot`
3. Следуйте инструкциям для создания бота
4. Сохраните полученный **токен** и **имя пользователя бота**

### 2. Получение ID чата

1. Добавьте бота в групповой чат
2. Отправьте любое сообщение в чат
3. Перейдите в браузере по адресу:
   ```
   https://api.telegram.org/bot<YOUR_BOT_TOKEN>/getUpdates
   ```
4. Найдите `chat.id` в ответе и сохраните его

### 3. Настройка базы данных PostgreSQL

```sql
CREATE DATABASE lunch_bot;
CREATE USER lunch_bot_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE lunch_bot TO lunch_bot_user;
```

### 4. Настройка приложения

Откройте `src/main/resources/application.properties` и замените:

```properties
# Telegram Bot Configuration
telegram.bot.bot-token=ВАШ_ТОКЕН_БОТА
telegram.bot.bot-username=ВАШ_ИМЯ_БОТА
telegram.bot.chat-id=ВАШ_ID_ЧАТА

# Database Configuration
spring.datasource.password=ВАШ_ПАРОЛЬ_БД
```

### 5. Запуск приложения

```bash
# Через Maven
./mvnw spring-boot:run

# Или через IDE
# Запустите класс LunchTgBotApplication
```

## Структура проекта

```
src/main/java/com/example/lunch_tg_bot/
├── config/
│   ├── BotConfig.java              # Конфигурация бота
│   └── ServiceConfiguration.java   # Решение циклических зависимостей
├── entity/
│   ├── UserResponse.java           # Сущность ответа пользователя
│   └── BotStatistics.java          # Статистика бота
├── repository/
│   ├── UserResponseRepository.java # Репозиторий ответов
│   └── BotStatisticsRepository.java # Репозиторий статистики
├── service/
│   ├── TelegramBotService.java     # Основной сервис бота
│   ├── ReminderService.java        # Сервис напоминаний
│   └── LunchListService.java       # Сервис списков обедающих
└── LunchTgBotApplication.java      # Главный класс приложения
```

## Настройка времени

Измените параметры в `application.properties`:

```properties
# Интервал напоминаний в минутах
telegram.bot.reminder-interval-minutes=30

# Время обеда
telegram.bot.lunch-time=12:00

# Время формирования финального списка
telegram.bot.final-list-time=11:30
```

## База данных

Бот автоматически создает таблицы при первом запуске:

- `user_responses` - ответы пользователей
- `bot_statistics` - статистика работы бота

## Пример работы

1. В 10:00 бот отправляет вопрос: "Будешь ли ты сегодня обедать? 🤔"
2. Пользователи нажимают кнопки "Да, буду обедать 🍽️" или "Нет, не буду 😔"
3. Те, кто не ответил, получают напоминания каждые 30 минут
4. В 12:00 отправляется финальный список обедающих

## Требования

- Java 17+
- PostgreSQL 12+
- Maven 3.6+
- Telegram бот токен

## Поддержка

При возникновении проблем проверьте:
1. Правильность токена бота
2. ID чата
3. Подключение к базе данных
4. Логи приложения в консоли
