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

    public BunnyMemoBot() {
        this.diaryStorage = new DiaryStorage();
        this.botToken = loadTokenFromConfig();
    }

    private String loadTokenFromConfig() {
        try (FileInputStream input = new FileInputStream("bot-config.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            String token = prop.getProperty("bot.token");

            if (token != null && !token.isEmpty()) {
                System.out.println("✅ Токен загружен из bot-config.properties");
                return token;
            }
        } catch (Exception e) {
            System.out.println("⚠️ Файл bot-config.properties не найден, использую токен по умолчанию");
        }

        return "8450494522:AAGMcoKqR2FnB5PGekoubtRTaP0IeTzIATk";
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
        return "BunnyMemoBot";
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}
