import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
public class ClientHandler implements Runnable {
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String clientUserName;

    public ClientHandler(Socket socket) {
        try {

            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.clientUserName = reader.readLine();

            clientHandlers.add(this);

            broadCastMessage("Server: " + clientUserName + " joined to the chat!");

        } catch (IOException e) {
            closeAll(socket, reader, writer);
        }
    }

    @Override
    public void run() {
        String clientMessage;

        while (socket.isConnected()) {
            try {
                clientMessage = reader.readLine();
                broadCastMessage(clientMessage);
            } catch (IOException e) {
                closeAll(socket, reader, writer);
                break;
            }
        }
    }

    public void broadCastMessage(String message) {
        for (ClientHandler currentClientHandler : clientHandlers) {
            try {
                if(!currentClientHandler.clientUserName.equals(clientUserName)) {
                    currentClientHandler.writer.write(message);
                    currentClientHandler.writer.newLine();
                    currentClientHandler.writer.flush();
                }
            } catch (IOException e) {
                closeAll(socket, reader, writer);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadCastMessage("Server: " + this.clientUserName + " left!");
    }

    private void closeAll(Socket socket, BufferedReader reader, BufferedWriter writer) {
        removeClientHandler();

        try {
            if (socket != null) socket.close();
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

}
