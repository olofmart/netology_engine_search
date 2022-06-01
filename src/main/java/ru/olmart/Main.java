package ru.olmart;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {

        try (ServerSocket serverSocket = new ServerSocket(8989)) {
            while (true) {
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));

                    String request = in.readLine();
                    System.out.println(request);
                    List<PageEntry> list = engine.search(request);

                    String json = new Gson().toJson(list);
                    out.print(json);
                    out.flush();
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}
