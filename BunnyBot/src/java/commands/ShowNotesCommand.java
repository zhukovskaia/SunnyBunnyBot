import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.List;

public class ShowNotesCommand implements Command {
    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        DiaryStorage storage = (DiaryStorage) diaryStorage;
        Long chatId = message.getChatId();
        List<DiaryNote> notes = storage.getNotes(chatId);

        return MenuDisplayer.showNotes(chatId, notes);
    }
}
