import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class DeleteNoteCommand implements Command {
    private static Map<Long, Integer> userCurrentPage = new HashMap<>();
    private static Map<Long, Boolean> usersConfirmingDeleteAll = new HashMap<>();

    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        DiaryStorage storage = (DiaryStorage) diaryStorage;
        Long chatId = message.getChatId();
        String text = message.getText();

        if ("🗑️ Удалить запись".equals(text)) {
            userCurrentPage.put(chatId, 0);
            usersConfirmingDeleteAll.put(chatId, false);
            List<DiaryNote> notes = storage.getNotes(chatId);
            return MenuDisplayer.showDeleteMenu(chatId, notes, 0);
        }
        else if ("◀️ Назад".equals(text) || "Вперед ▶️".equals(text)) {
            return handlePageNavigation(chatId, text, storage);
        }
        else if ("🔥 Удалить все".equals(text)) {
            return handleDeleteAllConfirmation(chatId, storage);
        }
        else if ("✅ Да, удалить все".equals(text)) {
            return handleDeleteAllExecution(chatId, storage);
        }
        else if ("❌ Нет, отменить".equals(text) || "🏠 Главное меню".equals(text)) {
            usersConfirmingDeleteAll.put(chatId, false);
            userCurrentPage.remove(chatId);
            return MenuDisplayer.showMainMenu(chatId);
        }
        else {
            return handleDeleteSelection(chatId, text, storage);
        }
    }

    private SendMessage handlePageNavigation(Long chatId, String navigation, DiaryStorage storage) {
        int currentPage = userCurrentPage.getOrDefault(chatId, 0);
        List<DiaryNote> notes = storage.getNotes(chatId);
        int itemsPerPage = 8;
        int totalPages = (int) Math.ceil((double) notes.size() / itemsPerPage);

        if ("◀️ Назад".equals(navigation) && currentPage > 0) {
            userCurrentPage.put(chatId, currentPage - 1);
            return MenuDisplayer.showDeleteMenu(chatId, notes, currentPage - 1);
        }
        else if ("Вперед ▶️".equals(navigation) && currentPage < totalPages - 1) {
            userCurrentPage.put(chatId, currentPage + 1);
            return MenuDisplayer.showDeleteMenu(chatId, notes, currentPage + 1);
        }
        else {
            return MenuDisplayer.showDeleteMenu(chatId, notes, currentPage);
        }
    }

    private SendMessage handleDeleteAllConfirmation(Long chatId, DiaryStorage storage) {
        usersConfirmingDeleteAll.put(chatId, true);
        int noteCount = storage.getNoteCount(chatId);

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("⚠️ Вы уверены, что хотите удалить ВСЕ " + noteCount + " записей?\nЭта операция необратима!");
        response.setReplyMarkup(KeyboardFactory.createConfirmationKeyboard());

        return response;
    }

    private SendMessage handleDeleteAllExecution(Long chatId, DiaryStorage storage) {
        usersConfirmingDeleteAll.put(chatId, false);
        userCurrentPage.remove(chatId);
        List<DiaryNote> notes = storage.getNotes(chatId);
        int deletedCount = notes.size();

        for (int i = notes.size() - 1; i >= 0; i--) {
            storage.removeNote(chatId, i);
        }

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());
        response.setText("✅ Удалены все записи (" + deletedCount + " шт.)");
        response.setReplyMarkup(KeyboardFactory.createMainKeyboard());

        return response;
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

                    notes = storage.getNotes(chatId);
                    if (!notes.isEmpty()) {
                        int currentPage = userCurrentPage.getOrDefault(chatId, 0);
                        int itemsPerPage = 8;
                        if (notes.size() <= currentPage * itemsPerPage && currentPage > 0) {
                            currentPage--;
                            userCurrentPage.put(chatId, currentPage);
                        }
                        response.setReplyMarkup(KeyboardFactory.createDeleteKeyboard(notes, currentPage));
                    } else {
                        response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
                        userCurrentPage.remove(chatId);
                    }
                } else {
                    response.setText("❌ Ошибка при удалении записи");
                    response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
                }
            } else {
                response.setText("❌ Неверный номер записи");
                response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
            }

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