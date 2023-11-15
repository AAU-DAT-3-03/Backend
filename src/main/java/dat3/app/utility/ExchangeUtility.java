package dat3.app.utility;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

public abstract class ExchangeUtility {
    public static <T> T parseJsonBody(HttpExchange exchange, int maxSize, Class<T> type) throws Exception {
        int contentLength = Integer.parseInt(exchange.getRequestHeaders().get("Content-Length").get(0));
        if (maxSize < contentLength) throw new Exception("Content length is bigger than allowed size.");

        byte[] buffer = new byte[contentLength];
        int read;
        int totalRead = 0;
        while (totalRead < contentLength) {
            read = exchange.getRequestBody().read(buffer, totalRead, contentLength - totalRead);
            totalRead += read;
        }

        String bodyJson = new String(buffer);
        return new Gson().fromJson(bodyJson, type);
    }
}
