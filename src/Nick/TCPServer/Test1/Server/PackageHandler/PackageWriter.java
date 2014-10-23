package Nick.TCPServer.Test1.Server.PackageHandler;

import Nick.TCPServer.Test1.Server.Connection;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Nick on 19/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class PackageWriter {
    Connection connection;
    ByteBuffer packageBuffer;
    ByteArrayOutputStream byteStream;

    public PackageWriter(Connection c) {

        this.connection = c;
        initialize();
    }

    private void initialize() {
        byteStream = new ByteArrayOutputStream();
    }

    public void write(int i) {
        try {
            byteStream.write(ByteBuffer.allocate(4).putInt(i).array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(long l) {
        try {
            byteStream.write(ByteBuffer.allocate(8).putLong(l).array());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(Connection c){
        try {
            byteStream.write(c.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send() {
        try {
            int length = byteStream.size();
            packageBuffer = ByteBuffer.allocate(length + 4);
            packageBuffer.putInt(length);
            packageBuffer.put(byteStream.toByteArray());

            System.out.println("Send package has been made");

            connection.send(packageBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        byteStream.reset();
        packageBuffer.reset();

    }

}
