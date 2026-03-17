package com.example.lunch_tg_bot.service;

import com.example.lunch_tg_bot.config.BotConfig;
import com.example.lunch_tg_bot.entity.User;
import com.example.lunch_tg_bot.entity.UserResponse;
import com.example.lunch_tg_bot.repository.UserRepository;
import com.example.lunch_tg_bot.repository.UserResponseRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class TelegramBotService extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(TelegramBotService.class);

    private final BotConfig botConfig;
    private final UserRepository userRepository;
    private final UserResponseRepository userResponseRepository;
    private final NotificationService notificationService;
    private final LunchListService lunchListService;
    
    private final ConcurrentHashMap.KeySetView<Long, Boolean> usersWaitingForName = ConcurrentHashMap.newKeySet();

    public TelegramBotService(BotConfig botConfig, 
                            UserRepository userRepository,
                            UserResponseRepository userResponseRepository,
                            NotificationService notificationService,
                            @Lazy LunchListService lunchListService) {
        this.botConfig = botConfig;
        this.userRepository = userRepository;
        this.userResponseRepository = userResponseRepository;
        this.notificationService = notificationService;
        this.lunchListService = lunchListService;
    }

    @Override
    public String getBotUsername() {
        return botConfig.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getBotToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.info("Получено обновление: {}", update);
        
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleMessage(update.getMessage());
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    private void handleMessage(org.telegram.telegrambots.meta.api.objects.Message message) {
        String chatId = message.getChatId().toString();
        String text = message.getText();
        Long userId = message.getFrom().getId();
        
        log.info("Получено сообщение от пользователя {} в чате {}: {}", userId, chatId, text);

        if (usersWaitingForName.contains(userId)) {
            handleNameInput(chatId, userId, text);
            return;
        }

        if (text.equals("/start")) {
            sendWelcomeMessage(chatId, message.getFrom());
        } else if (text.equals("/lunch")) {
            sendLunchQuestion(chatId);
        } else if (text.equals("/list")) {
            sendTodayLunchList(chatId);
        } else if (text.equals("/help")) {
            sendHelpMessage(chatId);
        } else if (text.equals("/setname")) {
            askForName(chatId, userId);
        }
    }

    private void handleCallbackQuery(org.telegram.telegrambots.meta.api.objects.CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getMessage().getChatId().toString();
        String data = callbackQuery.getData();
        Long userId = callbackQuery.getFrom().getId();

        log.info("Получен callback: {} от пользователя {} в чате {}", data, userId, chatId);

        if (data.equals("YES") || data.equals("NO")) {
            log.info("Обработка ответа: {}", data);
            
            log.info("Удаление предыдущего ответа для пользователя {}", userId);
            lunchListService.removeTodayResponse(userId);
            
            saveUserResponse(callbackQuery.getFrom(), UserResponse.Answer.valueOf(data));
            
            String responseText = data.equals("YES") ? "Отлично, ждем тебя на обеде! 🍽️" : "Понятно, сегодня без обеда 😔";
            sendAnswer(chatId, responseText);
            
            notificationService.removeUserFromReminders(userId);
        } else {
            log.info("Неизвестный callback: {}", data);
        }
    }

    private void handleNameInput(String chatId, Long userId, String name) {
        User user = userRepository.findByTelegramUserId(userId).orElse(null);
        if (user != null) {
            user.setDisplayName(name);
            userRepository.save(user);
            sendAnswer(chatId, "Отлично! Теперь я буду называть тебя: " + name + " 😊");
        }
        usersWaitingForName.remove(userId);
    }

    private void sendWelcomeMessage(String chatId, org.telegram.telegrambots.meta.api.objects.User user) {
        User dbUser = createOrUpdateUser(user);
        
        String welcomeText = "Привет, " + dbUser.getDisplayName() + "! 👋\n\n" +
                "Я бот для опроса об обеде. Я буду спрашивать каждый день, будешь ли ты обедать.\n\n" +
                "Команды:\n" +
                "/lunch - ответить на вопрос об обеде\n" +
                "/list - посмотреть список обедающих сегодня\n" +
                "/setname - установить свое имя для бота\n" +
                "/help - помощь";

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(welcomeText)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    public void sendLunchQuestion(String chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да, буду обедать 🍽️");
        yesButton.setCallbackData("YES");
        
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет, не буду 😔");
        noButton.setCallbackData("NO");
        
        row.add(yesButton);
        row.add(noButton);
        markup.setKeyboard(List.of(row));

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text("Будешь ли ты сегодня обедать? 🤔")
                .replyMarkup(markup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    private void sendTodayLunchList(String chatId) {
        String lunchList = lunchListService.getTodayLunchList();
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(lunchList)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    private void sendHelpMessage(String chatId) {
        String helpText = "📋 Справка по командам:\n\n" +
                "/start - начать работу с ботом\n" +
                "/lunch - ответить на вопрос об обеде\n" +
                "/list - посмотреть список обедающих сегодня\n" +
                "/setname - установить свое имя для бота\n" +
                "/help - показать эту справку\n\n" +
                "Бот автоматически будет напоминать об обеде, если ты не ответил!";

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(helpText)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    private void askForName(String chatId, Long userId) {
        String askText = "📝 Как мне тебя называть? Просто напиши свое имя:";

        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(askText)
                .build();

        try {
            execute(message);
            usersWaitingForName.add(userId);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    private void sendAnswer(String chatId, String text) {
        log.info("Отправка ответа в чат {}: {}", chatId, text);
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        try {
            execute(message);
            log.info("Ответ успешно отправлен");
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки ответа: {}", e.getMessage(), e);
        }
    }

    private void saveUserResponse(org.telegram.telegrambots.meta.api.objects.User user, UserResponse.Answer answer) {
        User dbUser = createOrUpdateUser(user);
        
        UserResponse response = new UserResponse();
        response.setUserId((long) user.getId());
        response.setUsername(user.getUserName());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setResponseDate(LocalDate.now());
        response.setAnswer(answer);

        userResponseRepository.save(response);
    }

    private User createOrUpdateUser(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        return userRepository.findByTelegramUserId((long) telegramUser.getId())
                .map(user -> {
                    user.setUsername(telegramUser.getUserName());
                    user.setFirstName(telegramUser.getFirstName());
                    user.setLastName(telegramUser.getLastName());
                    if (user.getDisplayName() == null) {
                        user.setDisplayName(telegramUser.getFirstName());
                    }
                    return userRepository.save(user);
                })
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setTelegramUserId((long) telegramUser.getId());
                    newUser.setUsername(telegramUser.getUserName());
                    newUser.setFirstName(telegramUser.getFirstName());
                    newUser.setLastName(telegramUser.getLastName());
                    newUser.setDisplayName(telegramUser.getFirstName());
                    return userRepository.save(newUser);
                });
    }

    public void sendMessageToChat(String message) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(botConfig.getChatId())
                .text(message)
                .build();
        
        log.info("Отправка сообщения в чат {}: {}", botConfig.getChatId(), message);

        try {
            execute(sendMessage);
            log.info("Сообщение успешно отправлено");
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    public void sendLunchQuestionToUser(Long userId) {
        User user = userRepository.findByTelegramUserId(userId).orElse(null);
        if (user != null) {
            sendLunchQuestionToUser(userId, user.getDisplayName());
        }
    }

    public void sendLunchQuestionToUser(Long userId, String userName) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<InlineKeyboardButton> row = new ArrayList<>();
        
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Да, буду обедать 🍽️");
        yesButton.setCallbackData("YES");
        
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("Нет, не буду 😔");
        noButton.setCallbackData("NO");
        
        row.add(yesButton);
        row.add(noButton);
        markup.setKeyboard(List.of(row));

        String questionText = userName + ", будешь ли ты сегодня обедать? 🤔";

        SendMessage message = SendMessage.builder()
                .chatId(userId.toString())
                .text(questionText)
                .replyMarkup(markup)
                .build();

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage(), e);
        }
    }

    public List<Long> getAllActiveUsers() {
        return userRepository.findByIsActiveTrue().stream()
                .map(User::getTelegramUserId)
                .toList();
    }

    public void addUserToNameWaiting(Long userId) {
        usersWaitingForName.add(userId);
    }
}
