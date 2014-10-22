package Nick.TCPServer.Test1.Client;

import Nick.TCPServer.Test1.Events.Client.ConnectionEvents.ConnectionCloseEvent;
import Nick.TCPServer.Test1.Events.Client.ConnectionListener;
import Nick.TCPServer.Test1.PackageHandler.Commands.MainCommands;
import Nick.TCPServer.Test1.PackageHandler.Commands.ServerClient;
import Nick.TCPServer.Test1.PackageHandler.PackageReader;
import Nick.TCPServer.Test1.Client.PackageHandler.PackageWriter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nick on 18/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class Connection implements Runnable {
    final Socket clientSocket;
    final Timestamp connectionTime;
    public boolean active;
    DataInputStream input;
    DataOutputStream output;
    short ping;
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
        cleanUp();
        fireCloseEvent();
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

    public void send(ByteBuffer data) {

        try {
            output.write(data.array());
            output.flush();
        } catch (IOException e) {
            //TODO: Insert better error handling
            //The server probably closed down..
            System.out.println("The server could not be reached!");
            System.exit(1);
        }
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
                        long timestamp = pr.readLong();
                        long now = new Date().getTime();

                        long difference = now - timestamp;
                        ping = (short) difference;

                        System.out.println("Ping: " + ping + " ms.");
                        //NEVER forget your break's! Bytes will appear at random!! :D
                        break;
                    }
                    case SERVER: {

                        long timestamp = pr.readLong();
                        PackageWriter pw = new PackageWriter(this);
                        pw.write(mc.ordinal());
                        pw.write(sc.ordinal());
                        pw.write(timestamp);
                        pw.send();
                        break;
                    }
                }


            }
        }
    }


}