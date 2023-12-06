package dat3.app.routes.notifications;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import dat3.app.routes.notifications.NotificationRoutes.RegistationTokenJSON;

public class NotificationRoutesTest {
    @Test
    void testReadingOfRegistrationToken() {
        String json = "{\"registrationToken\":\"abcdefg\"}";

        RegistationTokenJSON readToClass = new Gson().fromJson(json, RegistationTokenJSON.class);

        boolean isRead = false;
        try {
            isRead = readToClass.getRegistrationToken().equals("abcdefg");
        } catch (Exception e) {
            isRead = false;
        }

        assertTrue(isRead);
    }
}
