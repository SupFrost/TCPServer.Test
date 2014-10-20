package Nick.TCPServer.Test1.Events.Server.ConnectionEvents;

import Nick.TCPServer.Test1.Server.Connection;

import java.util.EventObject;

/**
 * Created by Nick on 19/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class ConnectionCloseEvent extends EventObject {
    private Connection _connection;

    public ConnectionCloseEvent(Object source, Connection connection) {
        super(source);
        _connection = connection;
    }

    public Connection connection() {
        return _connection;
    }
}

