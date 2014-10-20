package Nick.TCPServer.Test1.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Nick on 18/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class Server implements Runnable {
    public static ArrayList<Connection> connections;
    final int PORT;
    ServerSocket serverSocket;

    public Server(int port) {
        PORT = port;
        connections = new ArrayList<>();

        try {
            serverSocket = new ServerSocket(PORT, 100);

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        System.out.println("Server running!");

        do {
            try {
                System.out.println("Awaiting connection...");
                Socket clientSocket = serverSocket.accept();
                Connection connection = new Connection(clientSocket);
                connections.add(connection);

                //Adds the event for closing the connection!
                connection.addCloseListener(event -> {
                    connections.remove(event.connection());
                    event.connection().active = false;
                    System.out.println("The Connection " + event.connection().getClientSocket().getInetAddress() + " was removed from the list!");

                });


                Thread thread = new Thread(connection);
                thread.start();

            } catch (IOException e) {
                e.printStackTrace();
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                    System.exit(1);
                }
            }

        } while (!serverSocket.isClosed());

        System.out.println("Server shutdown successfully!");

    }
}
