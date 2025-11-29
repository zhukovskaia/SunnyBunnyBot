import java.io.*;
import java.util.*;

public class DiaryReader {
    public static void main(String[] args) {
        String dataFile = "diary_data.ser";

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(dataFile))) {
            Map<Long, List<DiaryNote>> data = (Map<Long, List<DiaryNote>>) ois.readObject();

            System.out.println("=== ДАННЫЕ ИЗ ФАЙЛА diary_data.ser ===");
            System.out.println("Найдено пользователей: " + data.size());
            System.out.println();

            for (Map.Entry<Long, List<DiaryNote>> entry : data.entrySet()) {
                Long chatId = entry.getKey();
                List<DiaryNote> notes = entry.getValue();

                System.out.println("📱 Пользователь ID: " + chatId);
                System.out.println("📝 Количество записей: " + notes.size());

                for (int i = 0; i < notes.size(); i++) {
                    DiaryNote note = notes.get(i);
                    System.out.println("   " + (i + 1) + ". " + note.getContent() +
                            " [📅 " + note.getDate() + "]");
                }
                System.out.println("---");
            }

        } catch (FileNotFoundException e) {
            System.out.println("❌ Файл " + dataFile + " не найден");
        } catch (IOException e) {
            System.out.println("❌ Ошибка чтения файла: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("❌ Ошибка: класс DiaryNote не найден");
        }
    }
}