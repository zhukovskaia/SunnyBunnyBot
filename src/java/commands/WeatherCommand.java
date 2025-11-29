import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import java.util.HashMap;
import java.util.Map;

public class WeatherCommand implements Command {
    private static Map<Long, Boolean> usersWaitingForCity = new HashMap<>();
    private final WeatherService weatherService;

    public WeatherCommand() {
        this.weatherService = new WeatherService();
    }

    public static boolean isWaitingForCity(Long chatId) {
        return usersWaitingForCity.getOrDefault(chatId, false);
    }

    @Override
    public SendMessage execute(Message message, Object diaryStorage) {
        String text = message.getText();
        Long chatId = message.getChatId();

        SendMessage response = new SendMessage();
        response.setChatId(chatId.toString());

        boolean isWaitingForCity = usersWaitingForCity.getOrDefault(chatId, false);

        if ("🌤 Погода".equals(text)) {
            usersWaitingForCity.put(chatId, true);
            response.setText("🏙 Введите название города:");
            response.setReplyMarkup(KeyboardFactory.createEmptyKeyboard());
        } else if (isWaitingForCity) {

            String weatherInfo = weatherService.getWeatherToday(text);
            response.setText(weatherInfo);
            response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
            usersWaitingForCity.put(chatId, false);
        } else {
            response.setText("❌ Сначала нажмите кнопку '🌤 Погода'");
            response.setReplyMarkup(KeyboardFactory.createMainKeyboard());
        }

        return response;
    }
}