package Nick.TCPServer.Test1.Server;

import Nick.TCPServer.Test1.PackageHandler.Commands.ConnectionCommands;
import Nick.TCPServer.Test1.PackageHandler.Commands.MainCommands;
import Nick.TCPServer.Test1.Server.PackageHandler.PackageWriter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nick on 18/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class Server implements Runnable {
    public static CopyOnWriteArrayList<Connection> connections;
    final int PORT;
    ServerSocket serverSocket;

    public Server(int port) {
        PORT = port;
        connections = new CopyOnWriteArrayList<>();

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

                //Send new connection to each connection
                sendAddConnectionToAll();

                //Adds the event for closing the connection!
                connection.addCloseListener(event -> {
                    connections.remove(event.connection());
                    event.connection().active = false;
                    System.out.println("The Connection " + event.connection().uuid.toString() + " was removed from the list!");

                    new Thread() {
                        public void run() {
                            for (Connection c : connections) {
                                PackageWriter pw = new PackageWriter(c);
                                pw.write(MainCommands.CONNECTION.ordinal());
                                pw.write(ConnectionCommands.REMOVE.ordinal());

                                pw.write(event.connection().uuid.getMostSignificantBits());
                                pw.write(event.connection().uuid.getLeastSignificantBits());

                                pw.send();
                            }
                        }
                    }.start();

                });

                Thread thread = new Thread(connection);
                thread.start();

            } catch (IOException e) {
                //TODO: Better error handling
                try {
                    serverSocket.close();
                } catch (IOException e1) {
                    //TODO: Better error handling
                    System.exit(1);
                }
            }

        } while (!serverSocket.isClosed());

        System.out.println("Server shutdown successfully!");

    }
    public void close() {
        try {

            for (Connection c : connections) {
                c.serverTerminated();
                c.close();
            }

            serverSocket.close();
        } catch (IOException e) {
            //TODO: Insert better error handling!
        }
        connections.clear();
    }

    public void sendAddConnectionToAll(){
        PackageWriter pw;
        for(Connection c : connections){
            pw = new PackageWriter(c);
            pw.write(MainCommands.CONNECTION.ordinal());
            pw.write(ConnectionCommands.ADD.ordinal());
            pw.write(c);
            pw.send();
        }
    }
}
