package ru.olmart;

import com.google.gson.Gson;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyServer {

    private final ExecutorService threadPool;
    private Socket socket;

    public MyServer() {
        threadPool = Executors.newFixedThreadPool(64);
    }

    public void listen (int port) {
        try (final ServerSocket serverSocket = new ServerSocket(port)) {
            while(true) {
                final Socket socket = serverSocket.accept();
                threadPool.submit(() -> connection(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connection(Socket socket) {
        try (final var in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
             final var out = new BufferedOutputStream(socket.getOutputStream())) {

            BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

            final var requestLine = in.readLine();
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                return;
            }

            final String word = URLDecoder.decode(parts[1], "UTF-8").substring(1);
            List<PageEntry> list = engine.search(word);
            if (list.isEmpty()) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }

            String json = new Gson().toJson(list);
            int length = json.getBytes().length;
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: application/json\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            out.write(json.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}