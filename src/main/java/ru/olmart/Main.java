package ru.olmart;

public class Main {
    public static void main(String[] args) throws Exception {

        MyServer server = new MyServer();
        server.listen(8989);

    }
}
