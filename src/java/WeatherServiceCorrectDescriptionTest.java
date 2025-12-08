import org.junit.Test;
import java.lang.reflect.Method;
import static org.junit.Assert.*;

public class WeatherServiceCorrectDescriptionTest {

    @Test
    public void testSpecificWeatherCodes() throws Exception {
        WeatherService service = new WeatherService();
        Method method = WeatherService.class.getDeclaredMethod("getWeatherDescription", int.class);
        method.setAccessible(true);

        assertEquals("☀️ ясно", method.invoke(service, 0));
        assertEquals("☀️ преимущественно ясно", method.invoke(service, 1));
        assertEquals("🌧️ дождь", method.invoke(service, 61));
        assertEquals("❄️ снег", method.invoke(service, 71));
        assertEquals("⛈️ гроза", method.invoke(service, 95));
    }
}