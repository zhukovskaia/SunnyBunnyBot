import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class AddNoteCommand implements Command {
    private static Map<Long, Boolean> usersAddingNote = new HashMap<>();

    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        DiaryStorage storage = (DiaryStorage) diaryStorage;
        String text = message.getText();
        Long chatId = message.getChatId();

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());

        boolean isAddingNote = usersAddingNote.getOrDefault(chatId, false);

        if ("📝 Добавить запись".equals(text)) {
            usersAddingNote.put(chatId, true);
            response.setText("✍️ Напишите текст записи:");
            response.setReplyMarkup(KeyboardFactory.createEmptyKeyboard());
        } else if (isAddingNote) {
            storage.addNote(chatId, text);
            response.setText("✅ Запись добавлена: " + text);
            response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
            usersAddingNote.put(chatId, false);
        } else {
            response.setText("Выберите действие:");
            response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
        }
        return response;
    }
}