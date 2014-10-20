package Nick.TCPServer.Test1.Events.Client;

import Nick.TCPServer.Test1.Events.Client.ConnectionEvents.ConnectionCloseEvent;

public interface ConnectionListener {
    public void ConnectionClosed(ConnectionCloseEvent event);
}
