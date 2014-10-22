package Nick.TCPServer.Test1.Events.Client;

import Nick.TCPServer.Test1.Events.Client.ConnectionEvents.ConnectionCloseEvent;
import Nick.TCPServer.Test1.Events.Client.ConnectionEvents.ServerClosedEvent;

public interface ConnectionListener {
    public void ConnectionClosed(ConnectionCloseEvent event);

    public void ServerClosed(ServerClosedEvent event);
}
