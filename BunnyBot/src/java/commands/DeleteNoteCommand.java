import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.List;

public class DeleteNoteCommand implements Command {
    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        DiaryStorage storage = (DiaryStorage) diaryStorage;
        Long chatId = message.getChatId();
        String text = message.getText();

        if ("🗑️ Удалить запись".equals(text)) {
            List<DiaryNote> notes = storage.getNotes(chatId);
            return MenuDisplayer.showDeleteMenu(chatId, notes);
        } else {
            return handleDeleteSelection(chatId, text, storage);
        }
    }

    private SendMessage handleDeleteSelection(Long chatId, String numberText, DiaryStorage storage) {
        try {
            int index = Integer.parseInt(numberText) - 1;
            List<DiaryNote> notes = storage.getNotes(chatId);

            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());

            if (index >= 0 && index < notes.size()) {
                DiaryNote deletedNote = notes.get(index);
                if (storage.removeNote(chatId, index)) {
                    response.setText("✅ Запись удалена:\n\"" + deletedNote.getContent() + "\"");
                } else {
                    response.setText("❌ Ошибка при удалении записи");
                }
            } else {
                response.setText("❌ Неверный номер записи");
            }

            response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
            return response;
        } catch (NumberFormatException e) {
            SendMessage response = new SendMessage();
            response.setChatId(chatId.toString());
            response.setText("❌ Пожалуйста, выберите номер из списка");
            response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
            return response;
        }
    }
}
