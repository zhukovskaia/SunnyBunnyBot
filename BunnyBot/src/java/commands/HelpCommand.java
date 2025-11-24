import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class HelpCommand implements Command {
    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        Long chatId = message.getChatId();

        String helpText = "🐰 *Помощь по командам BunnyMemoBot*\n\n" +
                "🚀 *Старт* - Главное меню\n" +
                "📝 *Добавить запись* - Создать новую запись\n" +
                "📖 *Мои записи* - Просмотреть все записи\n" +
                "🗑️ *Удалить запись* - Удалить выбранную запись\n" +
                "📊 *Количество записей* - Посчитать ваши записи\n\n" +
                "💡 *Как пользоваться:*\n" +
                "1. Нажмите '📝 Добавить запись'\n" +
                "2. Введите текст записи\n" +
                "3. Просматривайте и управляйте записями через меню";

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText(helpText);
        response.setReplyMarkup(KeyboardFactory.createMainKeyboard());

        return response;
    }
}
