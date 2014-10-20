package Nick.TCPServer.Test1.Events.Server;

import Nick.TCPServer.Test1.Events.Server.ConnectionEvents.ConnectionCloseEvent;

public interface ConnectionListener {
    public void ConnectionClosed(ConnectionCloseEvent event);
}
