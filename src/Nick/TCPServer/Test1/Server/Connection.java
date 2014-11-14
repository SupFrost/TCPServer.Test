package Nick.TCPServer.Test1.Server;

import Nick.TCPServer.Test1.PackageHandler.Commands.ConnectionCommands;
import Nick.TCPServer.Test1.Server.PackageHandler.PackageWriter;
import Nick.TCPServer.Test1.Events.Server.ConnectionEvents.ConnectionCloseEvent;
import Nick.TCPServer.Test1.Events.Server.ConnectionListener;
import Nick.TCPServer.Test1.PackageHandler.Commands.MainCommands;
import Nick.TCPServer.Test1.PackageHandler.Commands.ServerClient;
import Nick.TCPServer.Test1.PackageHandler.PackageReader;

import java.io.*;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Nick on 18/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class Connection implements Runnable, Serializable {
    final Socket clientSocket;
    final Timestamp connectionTime;
    public boolean active;
    DataInputStream input;
    DataOutputStream output;
    short ping;

    UUID uuid;

    private List<ConnectionListener> listeners = new ArrayList<>();

    public Connection(Socket clientSocket) {
        this.clientSocket = clientSocket;
        active = true;

        try {
            input = new DataInputStream(clientSocket.getInputStream());
            output = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        connectionTime = new Timestamp(new Date().getTime());
        uuid = UUID.randomUUID();

        System.out.println("Connection made with " + clientSocket.getInetAddress() + " at " + convertTime(getConnectionTime().getTime()));
    }

    public Timestamp getConnectionTime() {
        return connectionTime;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    public void close() {
        fireCloseEvent();
        cleanUp();

        System.out.println("The Connection " + this.uuid.toString() + " was removed from the list!");
    }

    public void serverTerminated() {

        PackageWriter pw = new PackageWriter(this);

        MainCommands mc = MainCommands.CLOSE;
        pw.write(mc.ordinal());
        pw.send();
    }

    private synchronized void fireCloseEvent() {
        ConnectionCloseEvent closeEvent = new ConnectionCloseEvent(this, this);
        for (Object listener : listeners) {
            ((ConnectionListener) listener).ConnectionClosed(closeEvent);
        }
    }

    public synchronized void addCloseListener(ConnectionListener connectionListener) {
        listeners.add(connectionListener);
    }

    public synchronized void removeCloseListener(ConnectionListener connectionListener) {
        listeners.remove(connectionListener);
    }

    private void cleanUp() {

        try {
            clientSocket.close();
            output.close();
            input.close();

        } catch (IOException e) {
            //TODO: Better error handling!
        }
    }

    @Override
    public void run() {
        byte[] buffer;

        do {
            try {
                int packageLength = input.readInt();
                ByteBuffer wrapped = ByteBuffer.allocate(packageLength);

                while (packageLength > 0) {

                    if (packageLength >= 1024) {
                        buffer = new byte[1024];
                        input.read(buffer, 0, 1024);
                        packageLength -= 1024;
                    } else {

                        buffer = new byte[packageLength];

                        input.read(buffer);
                        packageLength = 0;
                    }

                    wrapped.put(buffer);
                }
                handlePackage(wrapped);


            } catch (IOException | BufferOverflowException e) {
                active = false;
            }

        } while (active);
    }

    private void handlePackage(ByteBuffer data) {
        PackageReader pr = new PackageReader(data);

        MainCommands mc = MainCommands.values()[pr.readInt()];
        switch (mc) {
            case CLOSE: {
                ServerClient sc = ServerClient.values()[pr.readInt()];
                if (sc == ServerClient.CLIENT) {
                    //closes connection, removes it from the list and sends it to all the clients
                    close();
                }
                break;
            }
            case PING: {
                ServerClient sc = ServerClient.values()[pr.readInt()];
                switch (sc) {
                    case CLIENT: {

                        long timestamp = pr.readLong();
                        PackageWriter pw = new PackageWriter(this);
                        pw.write(mc.ordinal());
                        pw.write(sc.ordinal());
                        pw.write(timestamp);
                        pw.send();

                        break;
                    }
                    case SERVER: {
                        long timestamp = pr.readLong();
                        long now = new Date().getTime();

                        long difference = now - timestamp;
                        ping = (short) difference;

                        System.out.println(this.uuid + ": Ping: " + ping + " ms.");
                        break;
                    }
                }
                break;
            }
            case CONNECTION:{
                ConnectionCommands cc = ConnectionCommands.values()[pr.readInt()];
                switch(cc){
                    case LIST: {
                        List<Connection> cs = Server.connections;
                        int amount = cs.size();

                        PackageWriter pw = new PackageWriter(this);
                        pw.write(MainCommands.CONNECTION.ordinal());
                        pw.write(ConnectionCommands.LIST.ordinal());
                        pw.write(amount);

                        for(Connection c : cs){
                            pw.write(c);
                        }

                        pw.send();
                        break;
                    }

                }


                break;
            }

        }
    }

    public void send(ByteBuffer data) {

        try {
            output.write(data.array());
            output.flush();
        } catch (IOException e) {
            //TODO: Better error handling
        }

    }

    public void ping() {
        PackageWriter pw = new PackageWriter(this);

        MainCommands mc = MainCommands.PING;
        pw.write(mc.ordinal());

        ServerClient sc = ServerClient.SERVER;
        pw.write(sc.ordinal());

        pw.write(new Timestamp(new Date().getTime()).getTime());
        pw.send();
    }

    public byte[] toByteArray(){
        ByteBuffer buffer = ByteBuffer.wrap(new byte[(Long.BYTES + Long.BYTES + Long.BYTES + 4 + Short.BYTES)]);

        //The UUID of the connection
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());

        //Client connection time
        buffer.putLong(connectionTime.getTime());

        //Client IP
        buffer.put(clientSocket.getInetAddress().getAddress());

        //Current ping
        buffer.putShort(ping);

        return buffer.array();

    }

    public void kick() {
        //Send kicked command to client.
        PackageWriter pw = new PackageWriter(this);
        pw.write(MainCommands.CONNECTION.ordinal());
        pw.write(ConnectionCommands.KICK.ordinal());
        pw.send();


        close();
    }

}