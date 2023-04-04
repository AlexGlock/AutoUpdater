package agNBAutoUpdate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class NetBurnerCoreFinder {

    private static final byte[] VERIFY_FROM_PC_TO_NDK = new byte[]{66, 85, 82, 78};

    private static final byte[] VERIFY_FROM_PC_TO_NDKV2 = new byte[]{66, 85, 82, 50};

    private static final byte[] VERIFY_FROM_NDK_TO_PC = new byte[]{78, 69, 84, 66};

    private static final int UDP_NETBURNERID_PORT = 20034;

    private static final byte[] BROADCAST_ADDR = new byte[]{-1, -1, -1, -1};

    private static final char NBAUTO_READ = 'R';

    private static final char NBAUTO_WRITE = 'W';

    private static final byte NBAUTO_VERIFY = 86;

    private static final int NBAUTO_ERR = 3;

    private static final int NBAUTO_OK = 4;

    private static final int WAIT_SECONDS = 1;

    private static byte[] getVerify(byte[] data) {
        int offset = 0;
        int toRead = offset + 4;
        int index = 0;
        byte[] returnByte = new byte[toRead - offset];
        while (offset < toRead) {
            returnByte[index++] = data[offset++];
        }
        return returnByte;
    }

    public static ArrayList<NetBurnerDevice> CoreFinderAction(FindNotify nf) {
        //TODO: The original code does not compile here
        //This I do not understand yet. 
        //"Java does not support the C comma operator, which groups multiple expressions into a single expression. However, you can use multiple comma-separated expressions in the initialization and increment sections of the for loop"
        //However this is the source stating java has a comma operator outside of for loops
        //https://www.cs.umd.edu/~clin/MoreJava/ControlFlow/comma.html
        //The original code with comma operator does currently not compile in java 17 and 18
        //other parts of the code seem to use java 18 or even proposed features, so maybe this is also one.
        ArrayList<NetBurnerDevice> TheList = new ArrayList<NetBurnerDevice>();//, TheList = TheList;
        byte b = 0;
        DatagramPacket dp = null;
        DatagramSocket ds = null;
        boolean bWait = true;
        try {
            SocketAddress broadcast_addr, server_addr = new InetSocketAddress(20035);
            try {
                broadcast_addr = new InetSocketAddress(InetAddress.getByAddress(BROADCAST_ADDR), 20034);
            } catch (UnknownHostException e) {
                return null;
            }
            byte[] buffer = new byte[1024];
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length, server_addr);
            dp = new DatagramPacket(VERIFY_FROM_PC_TO_NDK, 0, VERIFY_FROM_PC_TO_NDK.length, broadcast_addr);
            ds = new DatagramSocket(20034);
            ds.setSoTimeout(1000);
            ds.send(dp);
            while (bWait) {
                try {
                    ds.receive(incomingPacket);
                    if (Arrays.equals(getVerify(incomingPacket.getData()), VERIFY_FROM_NDK_TO_PC)) {
                        NetBurnerDevice nbd = new NetBurnerDevice(incomingPacket);
                        if (TheList == null) {
                            TheList = new ArrayList<NetBurnerDevice>();
                        }
                        TheList.add(nbd);
                        nf.FoundADevice(nbd);
                    }
                } catch (SocketTimeoutException e) {
                    bWait = false;
                }
            }
            ds.close();
        } catch (SocketException socketException) {

        } catch (IOException iOException) {
        }
        
        nf.FindAllDone();
        return TheList;
    }

    public static interface FindNotify {

        void FoundADevice(NetBurnerDevice param1NetBurnerDevice);

        void FindAllDone();
    }
}
