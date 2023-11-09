package dat3.app.server;

import java.io.IOException;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

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

    public void sendResponse(HttpExchange connection, int statusCode, Object msg) throws IOException {
        this.statusCode = statusCode;
        this.msg = msg;
        this.sendResponse(connection);
    }

    public void sendResponse(HttpExchange connection) throws IOException {
        String json = new Gson().toJson(this);
        byte[] bytes = json.getBytes();
        connection.sendResponseHeaders(statusCode, bytes.length);
        connection.getResponseBody().write(bytes);
    }
}
