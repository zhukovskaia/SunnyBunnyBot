import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("/start", new StartCommand());
        commands.put("🚀 старт", new StartCommand());
        commands.put("🏠 главное меню", new StartCommand());
        commands.put("📝 добавить запись", new AddNoteCommand());
        commands.put("📖 мои записи", new ShowNotesCommand());
        commands.put("🗑️ удалить запись", new DeleteNoteCommand());
        commands.put("📊 количество записей", new NoteCountCommand());
        commands.put("🌤 погода", new WeatherCommand());
        commands.put("/help", new HelpCommand());
        commands.put("❓ помощь", new HelpCommand());
        commands.put("🆘 поддержка", new HelpCommand());

        commands.put("◀️ назад", new DeleteNoteCommand());
        commands.put("вперед ▶️", new DeleteNoteCommand());
        commands.put("🔥 удалить все", new DeleteNoteCommand());
        commands.put("✅ да, удалить все", new DeleteNoteCommand());
        commands.put("❌ нет, отменить", new StartCommand());
        commands.put("🏠 главное меню", new StartCommand());
    }

    public static Command getCommand(String commandText) {
        return commands.get(commandText.toLowerCase());
    }

    public static boolean isCommand(String text) {
        return commands.containsKey(text.toLowerCase()) ||
                text.startsWith("/") || StringUtils.isNumeric(text) ||
                "◀️ Назад".equals(text) ||
                "Вперед ▶️".equals(text) ||
                "🔥 Удалить все".equals(text) ||
                "✅ Да, удалить все".equals(text) ||
                "❌ Нет, отменить".equals(text) ||
                "🏠 Главное меню".equals(text) ||
                "🆘 Поддержка".equals(text);
    }
}