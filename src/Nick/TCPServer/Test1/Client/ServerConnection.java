package Nick.TCPServer.Test1.Client;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.util.UUID;

/**
 * Created by Nick on 23/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class ServerConnection {
    public UUID uuid;
    public Timestamp connectionTime;
    public InetAddress IP;
    public short Ping;

    public ServerConnection(UUID a, Timestamp b, InetAddress c, short d){
        uuid = a;
        connectionTime = b;
        IP = c;
        Ping = d;
    }
}
