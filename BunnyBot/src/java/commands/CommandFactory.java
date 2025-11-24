import java.util.HashMap;
import java.util.Map;

public class CommandFactory {
    private static final Map<String, Command> commands = new HashMap<>();

    static {
        commands.put("/start", new StartCommand());
        commands.put("🚀 старт", new StartCommand());
        commands.put("📝 добавить запись", new AddNoteCommand());
        commands.put("📖 мои записи", new ShowNotesCommand());
        commands.put("🗑️ удалить запись", new DeleteNoteCommand());
        commands.put("📊 количество записей", new NoteCountCommand());
        commands.put("/help", new HelpCommand());
        commands.put("❓ помощь", new HelpCommand());
    }

    public static Command getCommand(String commandText) {
        return commands.get(commandText.toLowerCase());
    }

    public static boolean isCommand(String text) {
        return commands.containsKey(text.toLowerCase()) ||
                text.startsWith("/") || StringUtils.isNumeric(text);
    }
}
