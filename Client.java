import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String userName;

    public Client(Socket socket, String userName) {
        try {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.userName = userName;
        } catch (IOException e) {

            closeAll(socket, reader, writer);
        }
    }

    public void sendMessage(){
        try {
            writer.write(userName);
            writer.newLine();
            writer.flush();

            Scanner scanner = new Scanner(System.in);
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                writer.write(userName+": "+messageToSend);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            closeAll(socket, reader, writer);

        }
    }

    public void listenToTheMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String messageFromChat;
                while(socket.isConnected()) {
                    try {
                        messageFromChat = reader.readLine();
                        System.out.println(messageFromChat);
                    } catch (IOException e) {
                        closeAll(socket, reader, writer);
                    }
                }
            }
        }).start();
    }

    private void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nPlease enter your user name sir: ");
        String userName = scanner.next();

        Socket socket = new Socket("localhost",2002);
        Client client = new Client(socket,userName);

        client.listenToTheMessage();
        client.sendMessage();
    }
}