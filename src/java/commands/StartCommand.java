import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class StartCommand implements Command {
    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        return MenuDisplayer.showMainMenu(message.getChatId());
    }
}