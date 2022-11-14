package Chat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) {
        Server server = new Server();
        server.openConnection();

    }

    private void openConnection() {
        Socket socket = null;
        try (ServerSocket serverSocket = new ServerSocket(Param.PORT)) {
            System.out.println("Waiting for connection");
            socket = serverSocket.accept();
            System.out.println("Client connected");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataoutputStream = new DataOutputStream(socket.getOutputStream());
            dataoutputStream.writeUTF("Connected to server, port: " + Param.PORT);
            while (true) {
                String message = dataInputStream.readUTF();
                if (message.equals(Param.STOP_WORD)) {
                    dataoutputStream.writeUTF(Param.STOP_WORD);
                    break;
                }
                System.out.println("Client send: " + message);
                dataoutputStream.writeUTF("Echo: " + message);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected");
            openConnection();
        }
    }

}
