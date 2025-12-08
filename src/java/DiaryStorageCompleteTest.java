import org.junit.Test;
import java.util.List;
import java.io.File;
import static org.junit.Assert.*;

public class DiaryStorageCompleteTest {

    @Test
    public void testCreateCompareAndCountNotes() {
        File testFile = new File("complete_test.ser");
        if (testFile.exists()) {
            testFile.delete();
        }

        DiaryStorage storage = new DiaryStorage("complete_test.ser");
        Long chatId = 123456789L;

        assertEquals(0, storage.getNoteCount(chatId));

        String[] testNotes = {
                "Первая тестовая запись",
                "Вторая тестовая запись",
                "Третья тестовая запись"
        };

        for (String note : testNotes) {
            storage.addNote(chatId, note);
        }

        List<DiaryNote> savedNotes = storage.getNotes(chatId);

        assertEquals(testNotes.length, savedNotes.size());

        for (int i = 0; i < testNotes.length; i++) {
            assertEquals(testNotes[i], savedNotes.get(i).getContent());
        }

        assertEquals(testNotes.length, storage.getNoteCount(chatId));

        storage.removeNote(chatId, 0);
        assertEquals(testNotes.length - 1, storage.getNoteCount(chatId));
    }

    @Test
    public void testEmptyStorageCount() {
        File testFile = new File("empty_test.ser");
        if (testFile.exists()) {
            testFile.delete();
        }

        DiaryStorage storage = new DiaryStorage("empty_test.ser");
        Long newUser = 999999999L;

        assertEquals(0, storage.getNoteCount(newUser));
    }
}