import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BunnyMemoBot extends TelegramLongPollingBot {
    private DiaryStorage diaryStorage;

    public BunnyMemoBot() {
        this.diaryStorage = new DiaryStorage();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            try {
                handleMessage(chatId, text);
            } catch (Exception e) {
                sendMessage(chatId, "❌ Ошибка: " + e.getMessage());
            }
        }
    }

    private void handleMessage(Long chatId, String text) {
        if (isNumeric(text)) {
            handleDeleteSelection(chatId, text);
            return;
        }

        switch (text) {
            case "/start":
            case "🏠 Главное меню":
            case "🚀 Старт":
                showMainMenu(chatId);
                break;

            case "📝 Добавить запись":
                sendMessage(chatId, "✍️ Напишите текст записи:");
                break;

            case "📖 Мои записи":
                showNotes(chatId);
                break;

            case "🗑️ Удалить запись":
                showDeleteMenu(chatId);
                break;

            case "📊 Количество записей":
                showNoteCount(chatId);
                break;

            default:
                // Если это не команда и не число, считаем это новой записью
                if (!text.startsWith("/")) {
                    addNewNote(chatId, text);
                } else {
                    sendMessage(chatId, "❌ Неизвестная команда");
                }
                break;
        }
    }


    private void handleDeleteSelection(Long chatId, String numberText) {
        try {
            int index = Integer.parseInt(numberText) - 1;
            List<DiaryNote> notes = diaryStorage.getNotes(chatId);

            if (index >= 0 && index < notes.size()) {
                DiaryNote deletedNote = notes.get(index);
                if (diaryStorage.removeNote(chatId, index)) {
                    sendMessageWithKeyboard(chatId,
                            "✅ Запись удалена:\n\"" + deletedNote.getContent() + "\"",
                            createMainKeyboard());
                } else {
                    sendMessageWithKeyboard(chatId, "❌ Ошибка при удалении записи", createMainKeyboard());
                }
            } else {
                sendMessageWithKeyboard(chatId, "❌ Неверный номер записи", createMainKeyboard());
            }
        } catch (NumberFormatException e) {
            sendMessageWithKeyboard(chatId, "❌ Пожалуйста, выберите номер из списка", createMainKeyboard());
        }
    }


    private boolean isNumeric(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void showMainMenu(Long chatId) {
        String welcomeText = "🐰 Привет! Я BunnyMemoBot!\n\n" + "Я записываю твои планы на день и веду их подсчет:)\n" +
                "Выберите действие:";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(welcomeText);
        message.setReplyMarkup(createMainKeyboard());

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    private void showNotes(Long chatId) {
        List<DiaryNote> notes = diaryStorage.getNotes(chatId);
        if (notes.isEmpty()) {
            sendMessageWithKeyboard(chatId, "📝 У вас пока нет записей", createMainKeyboard());
        } else {
            StringBuilder sb = new StringBuilder("📖 Ваши записи:\n\n");
            for (int i = 0; i < notes.size(); i++) {
                sb.append(notes.get(i).toFormattedString(i + 1)).append("\n\n");
            }
            sendMessageWithKeyboard(chatId, sb.toString(), createMainKeyboard());
        }
    }

    private void showDeleteMenu(Long chatId) {
        List<DiaryNote> notes = diaryStorage.getNotes(chatId);
        if (notes.isEmpty()) {
            sendMessageWithKeyboard(chatId, "📝 У вас нет записей для удаления", createMainKeyboard());
        } else {
            StringBuilder sb = new StringBuilder("🗑️ Выберите номер записи для удаления:\n\n");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

            for (int i = 0; i < notes.size(); i++) {
                String dateStr = notes.get(i).getDate().format(formatter);
                sb.append(i + 1).append(". ").append(notes.get(i).getContent())
                        .append(" (").append(dateStr).append(")\n");
            }

            ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
            keyboard.setResizeKeyboard(true);
            keyboard.setOneTimeKeyboard(true);

            List<KeyboardRow> keyboardRows = new ArrayList<>();

            KeyboardRow row = new KeyboardRow();
            for (int i = 1; i <= notes.size(); i++) {
                row.add(String.valueOf(i));
                if (i % 5 == 0 || i == notes.size()) {
                    keyboardRows.add(row);
                    row = new KeyboardRow();
                }
            }

            KeyboardRow cancelRow = new KeyboardRow();
            cancelRow.add("🏠 Главное меню");
            keyboardRows.add(cancelRow);

            keyboard.setKeyboard(keyboardRows);

            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(sb.toString());
            message.setReplyMarkup(keyboard);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.err.println("Ошибка отправки сообщения: " + e.getMessage());
            }
        }
    }

    private void showNoteCount(Long chatId) {
        int count = diaryStorage.getNoteCount(chatId);
        sendMessageWithKeyboard(chatId, "📊 У вас записей: " + count, createMainKeyboard());
    }

    private void addNewNote(Long chatId, String text) {
        diaryStorage.addNote(chatId, text);
        sendMessageWithKeyboard(chatId, "✅ Запись добавлена: " + text, createMainKeyboard());
    }

    private ReplyKeyboardMarkup createMainKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRows = new ArrayList<>();


        KeyboardRow row1 = new KeyboardRow();
        row1.add("🚀 Старт");
        row1.add("📝 Добавить запись");


        KeyboardRow row2 = new KeyboardRow();
        row2.add("📖 Мои записи");
        row2.add("🗑️ Удалить запись");


        KeyboardRow row3 = new KeyboardRow();
        row3.add("📊 Количество записей");

        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);

        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    private void sendMessageWithKeyboard(Long chatId, String text, ReplyKeyboardMarkup keyboard) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.setReplyMarkup(keyboard);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            System.err.println("Ошибка отправки сообщения: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return "BunnyMemoBot";
    }

    @Override
    public String getBotToken() {
        return "8450494522:AAGMcoKqR2FnB5PGekoubtRTaP0IeTzIATk";
    }
