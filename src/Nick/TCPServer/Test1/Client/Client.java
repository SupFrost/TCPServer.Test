package Nick.TCPServer.Test1.Client;

import Nick.TCPServer.Test1.Client.PackageHandler.PackageWriter;
import Nick.TCPServer.Test1.Events.Client.ConnectionEvents.ConnectionCloseEvent;
import Nick.TCPServer.Test1.Events.Client.ConnectionEvents.ServerClosedEvent;
import Nick.TCPServer.Test1.Events.Client.ConnectionListener;
import Nick.TCPServer.Test1.PackageHandler.Commands.MainCommands;
import Nick.TCPServer.Test1.PackageHandler.Commands.ServerClient;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Nick on 18/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class Client {
    final int PORT;
    final InetAddress IP;
    Connection connection;

    public Client(int Port, InetAddress IP) {
        this.PORT = Port;
        this.IP = IP;
        try {
            Socket clientSocket = new Socket(IP, PORT);
            connection = new Nick.TCPServer.Test1.Client.Connection(clientSocket);
            new Thread(connection).start();


            //Adds the event for closing the connection!
            connection.addCloseListener(new ConnectionListener() {
                @Override
                public void ConnectionClosed(ConnectionCloseEvent event) {
                    event.connection().close();
                    System.out.println("The connection was closed!");
                }

                @Override
                public void ServerClosed(ServerClosedEvent event) {
                    event.connection().close();
                    System.out.println("The server was shutdown! \n The connection has been broken..");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            //TODO: Insert better error handling
        }
    }

    public void ping() {
        PackageWriter pw = new PackageWriter(connection);

        MainCommands mc = MainCommands.ping;
        pw.write(mc.ordinal());

        ServerClient sc = ServerClient.CLIENT;
        pw.write(sc.ordinal());

        pw.write(new Timestamp(new Date().getTime()).getTime());
        pw.send();

        System.out.println("Ping request sent!");
    }

    public void close() {
        connection.close();
    }


}
