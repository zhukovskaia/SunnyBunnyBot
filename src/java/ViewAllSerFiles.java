import java.io.*;
import java.util.*;

public class ViewAllSerFiles {
    public static void main(String[] args) {
        System.out.println("=== ЧТЕНИЕ ВСЕХ .ser ФАЙЛОВ ===\n");

        viewFile("complete_test.ser");
        viewFile("test_junit4_1.ser");
        viewFile("test_junit4_2.ser");
        viewFile("test_junit4_3.ser");
    }

    static void viewFile(String filename) {
        System.out.println("🔍 ФАЙЛ: " + filename);

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);

            // Читаем объект
            Object obj = ois.readObject();

            // Проверяем тип
            if (obj instanceof Map) {
                Map<Long, List<DiaryNote>> data = (Map<Long, List<DiaryNote>>) obj;

                System.out.println("   👥 Пользователей: " + data.size());

                for (Map.Entry<Long, List<DiaryNote>> entry : data.entrySet()) {
                    System.out.println("   👤 ID: " + entry.getKey());
                    System.out.println("   📝 Записей: " + entry.getValue().size());

                    // Выводим записи
                    for (DiaryNote note : entry.getValue()) {
                        System.out.println("      • " + note.getContent() +
                                " [" + note.getDate() + "]");
                    }
                }
            } else {
                System.out.println("   ⚠️ Неизвестный формат: " + obj.getClass());
            }

            ois.close();
            fis.close();

        } catch (FileNotFoundException e) {
            System.out.println("   ❌ Файл не найден");
        } catch (EOFException e) {
            System.out.println("   📭 Файл пустой");
        } catch (Exception e) {
            System.out.println("   ⚠️ Ошибка: " + e.getMessage());
        }

        System.out.println();
    }
}