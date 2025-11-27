import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.List;

public class MenuDisplayer {

    private MenuDisplayer() {
    }

    public static SendMessage showMainMenu(Long chatId) {
        String welcomeText = "🌙 *Приветствую! Я BunnyMemoBot* 🐇\n\n" +
                "Твой волшебный помощник в мире записей и планов:\n\n" +
                "📝 *Записывай мысли* - сохраняю их в магическом свитке\n" +
                "📖 *Читай записи* - открываю страницы твоих воспоминаний\n" +
                "🗑️ *Управляй архивом* - очищай ненужные страницы\n" +
                "📊 *Следи за счетом* - знай, сколько мыслей сохранено\n" +
                "🌤️ *Узнавай погоду* - магия предсказаний для твоих планов\n\n" +
                "Выбери заклинание:";

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

    public static SendMessage showDeleteMenu(Long chatId, List<DiaryNote> notes, int page) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());

        if (notes.isEmpty()) {
            message.setText("📝 У вас нет записей для удаления");
            message.setReplyMarkup(KeyboardFactory.createMainKeyboard());
        } else {
            int itemsPerPage = 8;
            int startIndex = page * itemsPerPage;
            int endIndex = Math.min(startIndex + itemsPerPage, notes.size());
            int totalPages = (int) Math.ceil((double) notes.size() / itemsPerPage);

            StringBuilder sb = new StringBuilder("🗑️ Выберите номер записи для удаления:\n\n");

            for (int i = startIndex; i < endIndex; i++) {
                DiaryNote note = notes.get(i);
                sb.append(i + 1).append(". ").append(note.getContent()).append("\n");
            }

            if (totalPages > 1) {
                sb.append("\n📄 Страница ").append(page + 1).append(" из ").append(totalPages);
            }

            message.setText(sb.toString());
            message.setReplyMarkup(KeyboardFactory.createDeleteKeyboard(notes, page));
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
}