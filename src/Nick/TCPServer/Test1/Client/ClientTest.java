package Nick.TCPServer.Test1.Client;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * Created by Nick on 19/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class ClientTest {
    public static void main(String[] args) {
        try {
            InetAddress IP = InetAddress.getByName("localhost");
            Client client = new Client(25565,IP);

            Scanner scanner = new Scanner(System.in);
            String message = "";

            while (!message.equals("STOP")) {
                message = scanner.next();

                switch (message) {
                    case "ping": {
                        client.ping();
                        break;
                    }
                    case "STOP": {
                        client.close();
                        System.out.println("Bye bye!");
                        break;
                    }
                    case "list": {
                        System.out.println("Current connected connections: " + client.connectionList().size());
                        for(ServerConnection conn : client.connectionList() ){
                            System.out.println(conn.uuid);
                        }

                        break;
                    }
                    default: {
                        System.out.println("Invalid input!");
                    }
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}
