import java.time.LocalDateTime;
import java.io.Serializable;
import java.time.format.DateTimeFormatter;

public class DiaryNote implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long chatId;
    private LocalDateTime date;
    private String content;

    public DiaryNote(Long chatId, LocalDateTime date, String content) {
        this.chatId = chatId;
        this.date = date;
        this.content = content;
    }

    public Long getChatId() { return chatId; }
    public Long getUserId() { return chatId; }
    public LocalDateTime getDate() { return date; }
    public String getContent() { return content; }

    public void setChatId(Long chatId) { this.chatId = chatId; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public void setContent(String content) { this.content = content; }

    public String toFormattedString(int index) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return String.format("%d. 📅 %s\n   📝 %s", index, date.format(formatter), content);
    }
}