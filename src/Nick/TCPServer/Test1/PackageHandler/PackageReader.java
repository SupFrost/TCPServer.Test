package Nick.TCPServer.Test1.PackageHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by Nick on 19/10/2014.
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 */
public class PackageReader {

    ByteBuffer packageBuffer;

    public PackageReader(ByteBuffer buffer) {

        packageBuffer = buffer;
        packageBuffer.position(0);
    }

    public int readInt() {
        return packageBuffer.getInt();
    }

    public long readLong() {
        return packageBuffer.getLong();
    }

    public InetAddress readInetAdress(){
        byte[] inetadress = new byte[4];
        packageBuffer.get(inetadress,0,inetadress.length);

        try {
            return InetAddress.getByAddress(inetadress);
        } catch (UnknownHostException e) {
            //TODO: Better error handling!
            e.printStackTrace();
            return null;
        }
    }

    public short readShort(){
        return packageBuffer.getShort();
    }


}
