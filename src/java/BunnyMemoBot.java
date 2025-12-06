import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.FileInputStream;
import java.util.Properties;

public class BunnyMemoBot extends TelegramLongPollingBot {
    private DiaryStorage diaryStorage;
    private String botToken;
    private String botUsername;

    public BunnyMemoBot() {
        this.diaryStorage = new DiaryStorage();
        this.botToken = loadToken();
        this.botUsername = loadUsername();
    }

    private String loadToken() {
        String token = System.getenv("BOT_TOKEN");

        if (token != null && !token.isEmpty()) {
            System.out.println("✅ Токен загружен из переменных окружения");
            return token;
        }

        try (FileInputStream input = new FileInputStream("bot-config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            token = prop.getProperty("bot.token");

            if (token != null && !token.isEmpty()) {
                System.out.println("✅ Токен загружен из bot-config.properties");
                return token;
            }
        } catch (Exception e) {
            System.out.println("❌ Ошибка загрузки токена из файла: " + e.getMessage());
        }

        throw new RuntimeException("Не удалось загрузить токен бота. Установите переменную окружения BOT_TOKEN");
    }

    private String loadUsername() {
        String username = System.getenv("BOT_USERNAME");

        if (username != null && !username.isEmpty()) {
            System.out.println("✅ Username загружен из переменных окружения: " + username);
            return username;
        }

        return "BunnyMemoBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String text = message.getText();

            try {
                handleMessage(message, text);
            } catch (Exception e) {
                sendMessage(message.getChatId(), "❌ Ошибка: " + e.getMessage());
            }
        }
    }

    private void handleMessage(Message message, String text) {
        Long chatId = message.getChatId();

        if (WeatherCommand.isWaitingForCity(chatId)) {
            Command weatherCommand = CommandFactory.getCommand("🌤 погода");
            if (weatherCommand != null) {
                SendMessage response = weatherCommand.execute(message, diaryStorage);
                sendMessage(response);
            }
            return;
        }

        if (isDeleteNavigationCommand(text)) {
            Command deleteCommand = CommandFactory.getCommand("🗑️ удалить запись");
            if (deleteCommand != null) {
                SendMessage response = deleteCommand.execute(message, diaryStorage);
                sendMessage(response);
            }
            return;
        }

        if (CommandFactory.isCommand(text)) {
            Command command = CommandFactory.getCommand(text);

            if (command != null) {
                SendMessage response = command.execute(message, diaryStorage);
                sendMessage(response);
            } else if (StringUtils.isNumeric(text)) {
                Command deleteCommand = CommandFactory.getCommand("🗑️ удалить запись");
                if (deleteCommand != null) {
                    SendMessage response = deleteCommand.execute(message, diaryStorage);
                    sendMessage(response);
                }
            }
        } else if (!text.startsWith("/")) {
            Command addCommand = CommandFactory.getCommand("📝 добавить запись");
            if (addCommand != null) {
                SendMessage response = addCommand.execute(message, diaryStorage);
                sendMessage(response);
            }
        } else {
            sendMessage(message.getChatId(), "❌ Неизвестная команда");
        }
    }

    private boolean isDeleteNavigationCommand(String text) {
        return "◀️ Назад".equals(text) ||
                "Вперед ▶️".equals(text) ||
                "🔥 Удалить все".equals(text) ||
                "✅ Да, удалить все".equals(text) ||
                "❌ Нет, отменить".equals(text) ||
                "🏠 Главное меню".equals(text);
    }

    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки: " + e.getMessage());
        }
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        sendMessage(message);
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}