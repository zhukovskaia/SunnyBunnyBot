import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import java.util.List;

public class MenuDisplayer {

    private MenuDisplayer() {
    }

    public static SendMessage showMainMenu(Long chatId) {
        String welcomeText = "🐰 Привет! Я BunnyMemoBot!\n\n" +
                "Я записываю твои планы на день и веду их подсчет:)\n" +
                "Выберите действие:";

        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(welcomeText);
        message.setReplyMarkup(KeyboardFactory.createMainKeyboard());

        return message;
    }

    public static SendMessage showNotes(Long chatId, List<DiaryNote> notes) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setReplyMarkup(KeyboardFactory.createMainKeyboard());

        if (notes.isEmpty()) {
            message.setText("📝 У вас пока нет записей");
        } else {
            StringBuilder sb = new StringBuilder("📖 Ваши записи:\n\n");
            for (int i = 0; i < notes.size(); i++) {
                sb.append(notes.get(i).toFormattedString(i + 1)).append("\n\n");
            }
            message.setText(sb.toString());
        }

        return message;
    }

    public static SendMessage showDeleteMenu(Long chatId, List<DiaryNote> notes) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());

        if (notes.isEmpty()) {
            message.setText("📝 У вас нет записей для удаления");
            message.setReplyMarkup(KeyboardFactory.createMainKeyboard());
        } else {
            StringBuilder sb = new StringBuilder("🗑️ Выберите номер записи для удаления:\n\n");
            for (int i = 0; i < notes.size(); i++) {
                sb.append(i + 1).append(". ").append(notes.get(i).getContent()).append("\n");
            }
            message.setText(sb.toString());
            message.setReplyMarkup(KeyboardFactory.createDeleteKeyboard(notes.size()));
        }

        return message;
    }

    public static SendMessage showNoteCount(Long chatId, int count) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText("📊 У вас записей: " + count);
        message.setReplyMarkup(KeyboardFactory.createMainKeyboard());
        return message;
    }

