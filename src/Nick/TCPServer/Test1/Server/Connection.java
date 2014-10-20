package Nick.TCPServer.Test1.Server;

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
import java.util.Iterator;
import java.util.List;

/**
 * Created by Nick on 18/10/2014.
 */
public class Connection implements Runnable {
    final Socket clientSocket;
    final Timestamp connectionTime;
    public boolean active;
    DataInputStream input;
    DataOutputStream output;
    short ping;
    private List listeners = new ArrayList();

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

        System.out.println("Connection made with " + clientSocket.getInetAddress() + " at " + convertTime(getConnectionTime().getTime()));
    }

    public Timestamp getConnectionTime() {
        return connectionTime;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public short getPing() {
        return ping;
    }

    public String convertTime(long time) {
        Date date = new Date(time);
        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
        return format.format(date);
    }

    public void close() {
        cleanUp();
        fireCloseEvent();
    }

    private synchronized void fireCloseEvent() {
        ConnectionCloseEvent closeEvent = new ConnectionCloseEvent(this, this);
        Iterator itListeners = listeners.iterator();
        while (itListeners.hasNext()) {
            ((ConnectionListener) itListeners.next()).ConnectionClosed(closeEvent);
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

                    System.out.println(wrapped.array().length);
                    wrapped.put(buffer);
                }
                handlePackage(wrapped);


            } catch (IOException | BufferOverflowException e) {
                active = false;
            }

        } while (active);
        close();
    }

    private void handlePackage(ByteBuffer data) {
        PackageReader pr = new PackageReader(data);

        MainCommands mc = MainCommands.values()[pr.readInt()];
        switch (mc) {
            case close: {

            }
            case ping: {
                ServerClient sc = ServerClient.values()[pr.readInt()];
                switch (sc) {
                    case CLIENT: {

                        System.out.println("Ping request received!");

                        long timestamp = pr.readLong();
                        PackageWriter pw = new PackageWriter(this);
                        pw.write(mc.ordinal());
                        pw.write(sc.ordinal());
                        pw.write(timestamp);
                        pw.send();

                        break;
                    }
                    case SERVER: {
                        //TODO: Client respond to Server ping request.

                        PackageWriter pw = new PackageWriter(this);
                        pw.write(mc.ordinal());
                        // pw.write(timestamp);
                        pw.send();
                        break;
                    }
                }


            }
        }
    }

    public void send(ByteBuffer data) {

        try {
            output.write(data.array());
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Data was sent!");

    }
}