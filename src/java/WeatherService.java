import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;

public class WeatherService {

    public String getWeatherToday(String city) {
        try {
            String geoUrl = String.format("https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=ru",
                    URLEncoder.encode(city, "UTF-8"));

            System.out.println("🔍 Поиск города: " + geoUrl);
            String geoResponse = sendGetRequest(geoUrl);
            JsonNode geoData = new ObjectMapper().readTree(geoResponse);

            if (!geoData.has("results") || geoData.get("results").size() == 0) {
                return "❌ Город '" + city + "' не найден\nПопробуйте проверить написание";
            }

            JsonNode cityData = geoData.get("results").get(0);
            double latitude = cityData.get("latitude").asDouble();
            double longitude = cityData.get("longitude").asDouble();
            String foundCityName = cityData.get("name").asText();
            String country = cityData.get("country").asText();

            String weatherUrl = String.format(
                    "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f&current_weather=true&timezone=auto",
                    latitude, longitude
            ).replace(",", ".");

            System.out.println("🌤 Запрос погоды: " + weatherUrl);
            String weatherResponse = sendGetRequest(weatherUrl);

            return parseWeatherResponse(weatherResponse, foundCityName, country);

        } catch (Exception e) {
            System.out.println("❌ Ошибка: " + e.getMessage());
            return "❌ Ошибка при поиске города '" + city + "'\n" + e.getMessage();
        }
    }

    private String sendGetRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = conn.getResponseCode();
        System.out.println("📊 Код ответа: " + responseCode);

        if (responseCode != 200) {
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            String errorLine;
            StringBuilder errorResponse = new StringBuilder();
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            System.out.println("❌ Ошибка API: " + errorResponse.toString());

            throw new RuntimeException("HTTP ошибка: " + responseCode);
        }

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private String parseWeatherResponse(String response, String city, String country) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode root = objectMapper.readTree(response);

        if (root.has("current_weather")) {
            JsonNode current = root.get("current_weather");
            double temperature = current.get("temperature").asDouble();
            double windSpeed = current.get("windspeed").asDouble();
            int weatherCode = current.get("weathercode").asInt();

            String description = getWeatherDescription(weatherCode);

            return String.format(
                    "🌤 Погода в %s, %s:\n" +
                            "🌡 Температура: %.1f°C\n" +
                            "☁️ Состояние: %s\n" +
                            "💨 Ветер: %.1f м/с",
                    city, country, temperature, description, windSpeed
            );
        } else {
            throw new RuntimeException("Нет данных о погоде");
        }
    }

    private String getWeatherDescription(int weatherCode) {
        switch (weatherCode) {
            case 0: return "☀️ ясно";
            case 1: return "☀️ преимущественно ясно";
            case 2: return "⛅ переменная облачность";
            case 3: return "☁️ пасмурно";
            case 45: case 48: return "🌫️ туман";
            case 51: case 53: case 55: return "🌦️ морось";
            case 61: case 63: case 65: return "🌧️ дождь";
            case 71: case 73: case 75: return "❄️ снег";
            case 80: case 81: case 82: return "💦 ливень";
            case 95: return "⛈️ гроза";
            case 96: case 99: return "⛈️ гроза с градом";
            default: return "☁️ облачно";
        }
    }
}