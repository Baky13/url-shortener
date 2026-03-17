# 🔐 Настройка переменных окружения

## 📋 Обязательно к выполнению

Все секреты (токены, пароли) вынесены в переменные окружения и НЕ хранятся в Git.

## 🛠️ Способы настройки

### Способ 1: Файл .env (рекомендуется для локальной разработки)

1. Скопируйте файл `.env.example` в `.env`:
```bash
cp .env.example .env
```

2. Отредактируйте `.env` файл:
```
# Telegram Bot Configuration
TELEGRAM_BOT_TOKEN=ВАШ_НОВЫЙ_ТОКЕН
TELEGRAM_CHAT_ID=ВАШ_CHAT_ID

# Database Configuration  
DB_PASSWORD=ВАШ_ПАРОЛЬ_ПОСТГРЕС
```

### Способ 2: Переменные окружения системы

**Windows (PowerShell):**
```powershell
$env:TELEGRAM_BOT_TOKEN="ВАШ_ТОКЕН"
$env:TELEGRAM_CHAT_ID="ВАШ_CHAT_ID"
$env:DB_PASSWORD="ВАШ_ПАРОЛЬ"
```

**Windows (CMD):**
```cmd
set TELEGRAM_BOT_TOKEN=ВАШ_ТОКЕН
set TELEGRAM_CHAT_ID=ВАШ_CHAT_ID
set DB_PASSWORD=ВАШ_ПАРОЛЬ
```

**Linux/Mac:**
```bash
export TELEGRAM_BOT_TOKEN="ВАШ_ТОКЕН"
export TELEGRAM_CHAT_ID="ВАШ_CHAT_ID"
export DB_PASSWORD="ВАШ_ПАРОЛЬ"
```

### Способ 3: Настройка в IDE

**IntelliJ IDEA:**
1. Run/Debug Configurations
2. Application → LunchTgBotApplication
3. Environment variables
4. Добавьте:
```
TELEGRAM_BOT_TOKEN=ВАШ_ТОКЕН
TELEGRAM_CHAT_ID=ВАШ_CHAT_ID
DB_PASSWORD=ВАШ_ПАРОЛЬ
```

## ⚠️ Важные замечания

1. **Новый токен**: Старый токен скомпрометирован, получите новый через @BotFather
2. **Безопасность**: Никогда не коммитьте `.env` файл в Git
3. **Продукт**: В продакшене используйте системные переменные окружения

## 🔍 Проверка настройки

После настройки переменных, запустите приложение:
```bash
mvn spring-boot:run
```

Если все настроено правильно, увидите:
```
Бот успешно зарегистрирован в Telegram
```

## 🚨 Если не работает

1. Проверьте что переменные установлены: `echo $TELEGRAM_BOT_TOKEN`
2. Перезапустите терминал/IDE после установки переменных
3. Проверьте правильность токена и пароля

---

**Готово!** 🎉 Теперь ваши секреты в безопасности.
