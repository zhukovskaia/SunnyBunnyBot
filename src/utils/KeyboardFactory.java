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
        row1.add("🏠 Главное меню");
        row1.add("📝 Добавить запись");

        KeyboardRow row2 = new KeyboardRow();
        row2.add("📖 Мои записи");
        row2.add("🗑️ Удалить запись");

        KeyboardRow row3 = new KeyboardRow();
        row3.add("📊 Количество записей");
        row3.add("🌤 Погода");

        KeyboardRow row4 = new KeyboardRow();
        row4.add("🆘 Поддержка");

        keyboardRows.add(row1);
        keyboardRows.add(row2);
        keyboardRows.add(row3);
        keyboardRows.add(row4);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    public static ReplyKeyboardMarkup createDeleteKeyboard(List<DiaryNote> notes, int page) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        int notesCount = notes.size();
        int itemsPerPage = 8;
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, notesCount);
        int totalPages = (int) Math.ceil((double) notesCount / itemsPerPage);

        KeyboardRow currentRow = new KeyboardRow();
        for (int i = startIndex; i < endIndex; i++) {
            currentRow.add(String.valueOf(i + 1));

            if (currentRow.size() == 4 || i == endIndex - 1) {
                keyboardRows.add(currentRow);
                currentRow = new KeyboardRow();
            }
        }

        if (totalPages > 1) {
            KeyboardRow navRow = new KeyboardRow();
            if (page > 0) {
                navRow.add("◀️ Назад");
            }
            if (page < totalPages - 1) {
                navRow.add("Вперед ▶️");
            }
            if (!navRow.isEmpty()) {
                keyboardRows.add(navRow);
            }

            KeyboardRow pageRow = new KeyboardRow();
            pageRow.add("📄 Страница " + (page + 1) + "/" + totalPages);
            keyboardRows.add(pageRow);
        }

        if (notesCount > 1) {
            KeyboardRow deleteAllRow = new KeyboardRow();
            deleteAllRow.add("🔥 Удалить все");
            keyboardRows.add(deleteAllRow);
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

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        KeyboardRow cancelRow = new KeyboardRow();
        cancelRow.add("🏠 Главное меню");
        keyboardRows.add(cancelRow);

        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }

    public static ReplyKeyboardMarkup createConfirmationKeyboard() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow confirmRow = new KeyboardRow();
        confirmRow.add("✅ Да, удалить все");
        confirmRow.add("❌ Нет, отменить");

        KeyboardRow cancelRow = new KeyboardRow();
        cancelRow.add("🏠 Главное меню");

        keyboardRows.add(confirmRow);
        keyboardRows.add(cancelRow);
        keyboard.setKeyboard(keyboardRows);
        return keyboard;
    }
}