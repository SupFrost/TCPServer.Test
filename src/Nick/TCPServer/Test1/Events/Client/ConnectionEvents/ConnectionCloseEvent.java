package Nick.TCPServer.Test1.Events.Client.ConnectionEvents;

import java.util.EventObject;

/**
 * Created by Nick on 19/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class ConnectionCloseEvent extends EventObject {
    private Nick.TCPServer.Test1.Client.Connection _connection;

    public ConnectionCloseEvent(Object source, Nick.TCPServer.Test1.Client.Connection connection) {
        super(source);
        _connection = connection;
    }

    public Nick.TCPServer.Test1.Client.Connection connection() {
        return _connection;
    }
}

