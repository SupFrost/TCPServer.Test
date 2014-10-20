package Nick.TCPServer.Test1.PackageHandler;

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


}
