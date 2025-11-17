import java.util.*;
import java.time.LocalDateTime;
import java.io.*;
import java.nio.file.*;

public class DiaryStorage {
    private Map<Long, List<DiaryNote>> userDiaries = new HashMap<>();
    private final String DATA_FILE = "diary_data.ser";

    public DiaryStorage() {
        loadData();
    }

  
    public void addNote(Long chatId, String content) {
        DiaryNote note = new DiaryNote(chatId, LocalDateTime.now(), content);
        userDiaries.computeIfAbsent(chatId, k -> new ArrayList<>()).add(note);
        saveData(); 
    }


    public List<DiaryNote> getNotes(Long chatId) {
        return userDiaries.getOrDefault(chatId, new ArrayList<>());
    }

    private void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(userDiaries);
        } catch (IOException e) {
            System.err.println("Ошибка сохранения: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void loadData() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            userDiaries = (Map<Long, List<DiaryNote>>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("Файл данных не найден, создаем новый");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Ошибка загрузки: " + e.getMessage());
        }
    }


    public boolean removeNote(Long chatId, int index) {
        List<DiaryNote> notes = userDiaries.get(chatId);
        if (notes != null && index >= 0 && index < notes.size()) {
            notes.remove(index);
            saveData(); 
            return true;
        }
        return false;
    }

    public int getNoteCount(Long chatId) {
        return userDiaries.getOrDefault(chatId, new ArrayList<>()).size();
    }
}
