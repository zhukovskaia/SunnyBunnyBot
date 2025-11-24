import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    private KeyboardFactory() {
    }

    public static ReplyKeyboardMarkup createMainKeyboard() {
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

    public static ReplyKeyboardMarkup createDeleteKeyboard(int notesCount) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow currentRow = new KeyboardRow();

        for (int i = 1; i <= notesCount; i++) {
            currentRow.add(String.valueOf(i));

            if (i % 5 == 0 || i == notesCount) {
                keyboardRows.add(currentRow);
                currentRow = new KeyboardRow();
            }
        }

        if (notesCount > 10) {
            KeyboardRow navRow = new KeyboardRow();
            navRow.add("⬅️ Предыдущие");
            navRow.add("➡️ Следующие");
            keyboardRows.add(navRow);
        }

        KeyboardRow cancelRow = new KeyboardRow();
        cancelRow.add("🏠 Главное меню");
        keyboardRows.add(cancelRow);

        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    public static ReplyKeyboardMarkup createEmptyKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        keyboard.setKeyboard(new ArrayList<>());
        return keyboard;
    }
}
