import java.util.*;
import java.time.LocalDateTime;
import java.io.*;

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
        File dataFile = new File(DATA_FILE);

        if (!dataFile.exists()) {
            System.out.println("📁 Файл данных не найден, создаем новую коллекцию");
            userDiaries = new HashMap<>();
            return;
        }


        if (dataFile.length() == 0) {
            System.out.println("📁 Файл данных пустой, создаем новую коллекцию");
            userDiaries = new HashMap<>();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(DATA_FILE))) {
            Object loadedData = ois.readObject();
            if (loadedData instanceof Map) {
                userDiaries = (Map<Long, List<DiaryNote>>) loadedData;
                System.out.println("✅ Данные успешно загружены, пользователей: " + userDiaries.size());
            } else {
                System.out.println("⚠️ Неверный формат данных, создаем новую коллекцию");
                userDiaries = new HashMap<>();
            }
        } catch (FileNotFoundException e) {
            System.out.println("📁 Файл данных не найден, создаем новую коллекцию");
            userDiaries = new HashMap<>();
        } catch (IOException e) {
            System.err.println("❌ Ошибка чтения файла: " + e.getMessage());
            System.out.println("Создаем новую коллекцию");
            userDiaries = new HashMap<>();
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Ошибка: класс не найден - " + e.getMessage());
            System.out.println("Создаем новую коллекцию");
            userDiaries = new HashMap<>();
        } catch (Exception e) {
            System.err.println("❌ Неизвестная ошибка: " + e.getMessage());
            System.out.println("Создаем новую коллекцию");
            userDiaries = new HashMap<>();
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
