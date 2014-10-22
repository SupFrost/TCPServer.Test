package Nick.TCPServer.Test1.Server;

import java.util.Scanner;

/**
 * Created by Nick on 18/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class ServerTest {
    public static void main(String[] args) {
        Server server = new Server(25565);

        new Thread(server).start();

        Scanner scanner = new Scanner(System.in);
        String message = "";

        while (!message.equals("STOP")) {
            message = scanner.next();

            switch (message) {
                case "ping": {

                    for (Connection c : Server.connections) {
                        c.ping();
                        System.out.println("Test...");
                    }

                    break;
                }
                case "STOP": {
                    server.close();
                    System.out.println("Bye bye!");
                    break;
                }
                default: {
                    System.out.println("Invalid input!");
                }
            }
        }
    }
}
