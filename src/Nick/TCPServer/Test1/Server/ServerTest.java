package Nick.TCPServer.Test1.Server;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
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
                    if(Server.connections.isEmpty())
                        break;
                    for (Connection c : Server.connections) {
                        c.ping();
                    }

                    break;
                }
                case "STOP": {
                    System.out.println("Bye bye!");
                    server.serverActive = false;
                    server.close();
                    break;
                }

                case "list": {
                    System.out.println("Current connected connections: " + server.connections.size());
                    for (Connection c : server.connections) {
                        System.out.println(c.uuid);
                    }
                    break;
                }

                case "kick": {
                    System.out.println("Which connection should be kicked?");
                    System.out.println("Type 'CANCEL' to exit");

                    for (Connection c : server.connections) {
                        System.out.println(c.uuid);
                    }

                    String temp = scanner.next();

                    try {
                        int i = Integer.parseInt(temp);
                        i--;
                        server.connections.get(i).kick();

                    } catch (Exception e) {

                    }
                    break;
                }

                default: {
                    System.out.println("Invalid input!");
                    break;
                }
            }
        }
    }
}
