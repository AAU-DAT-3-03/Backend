package dat3.app.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

/**
 * Major useful class for creating responses. Basically, set a response code, set a message, and then when sending the response, it will automatically parse the message to JSON and send it. To do this it uses GSON.
 */
public class Response {
    private int statusCode;
    private Object msg;

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Object getMsg() {
        return msg;
    }

    /**
     * Sends a response with the given statusCode and message.
     * @param connection The HttpExchange that is tied to the current client communication
     * @param statusCode The statusCode to send (not HTTP response code, but for our own protocol)
     * @param msg The object to parse to JSON.
     * @throws IOException Throws an IOException the response couldn't be sent.
     */
    public void sendResponse(HttpExchange connection, int statusCode, Object msg) throws IOException {
        this.statusCode = statusCode;
        this.msg = msg;
        this.sendResponse(connection);
    }

    /**
     * Sends this response.
     * @param connection The HttpExchange that is tied to the current client communication
     * @throws IOException Throws an IOException the response couldn't be sent.
     */
    public void sendResponse(HttpExchange connection) throws IOException {
        // CORS fix.
        connection.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        // We ALWAYS send JSON.
        connection.getResponseHeaders().add("Content-Type", "application/json");

        // Convert this object to JSON looks like: {"statusCode": 0, "msg": {some parsed object}}
        String json = new Gson().toJson(this);
        byte[] bytes = json.getBytes();
        
        // Send it!
        connection.sendResponseHeaders(200, bytes.length);
        connection.getResponseBody().write(bytes);
    }
}
