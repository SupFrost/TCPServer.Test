package Nick.TCPServer.Test1.Server;

/**
 * Created by Nick on 18/10/2014.
 */
public class ServerTest {
    public static void main(String[] args) {
        Server server = new Server(25565);

        new Thread(server).run();
    }
}
