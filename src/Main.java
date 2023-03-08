import java.io.*;
import java.net.Socket;


public class Main {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 6000)) {
            System.out.println("Для получения перечня товаров напишите \"view\"\n"
                    + "Чтобы добавить товар напишите \"add страна-производитель производитель товар количество\"\n"
                    + "Чтобы изменить товар напишите \"edit страна-производитель производитель товар количество\"\n"
                    + "Чтобы удалить товар напишите \"delete страна-производитель производитель товар количество\"\n"
                    + "Чтобы узнать количество товара определенной модели напишите \"товар\"");
            Client client = new Client(socket);
            client.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class Client implements Runnable {
        private Socket socket;

        public Client(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                System.out.println("Создано соединение между клиентом и сервером");
                while (!socket.isOutputShutdown()) {
                    if (reader.ready()) {
                        String clientCommand = reader.readLine();
                        if (clientCommand.equals("-1")) {
                            System.out.println("Получен запрос на уничтожение связи");
                            break;
                        }
                        sendMessage(out, clientCommand);
                        System.out.println("Отправлены данные: " + clientCommand);
                        String input_string = in.readLine();
                        if (!input_string.isEmpty()) {
                            System.out.println("Получены данные: ");
                            System.out.println(input_string);
                        }
                    }
                }
                socket.close();
                in.close();
                out.close();
                System.out.println("Связь уничтожена");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendMessage(BufferedWriter out, String message) {
        try {
            out.write(message + "\n");
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}