import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class NoteCountCommand implements Command {
    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        DiaryStorage storage = (DiaryStorage) diaryStorage;
        Long chatId = message.getChatId();
        int count = storage.getNoteCount(chatId);

        return MenuDisplayer.showNoteCount(chatId, count);
    }
}
