package agNBAutoUpdate;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetBurnerDevice {

    private String AppName;

    private String ModuleName;

    private String RootMac;

    private InetAddress IpAddress;

    private InetAddress RxIpAddress;

    private boolean bDhcp;

    private boolean bActive;

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

    public NetBurnerDevice(DatagramPacket incomingPacket) {
        String[] name = getNameAndAppName(incomingPacket.getData());
        this.IpAddress = getIP(incomingPacket.getData());
        this.ModuleName = name[0];
        this.AppName = name[1];
        this.RootMac = getParentMacAddr(incomingPacket.getData());
        if (this.IpAddress.toString().equalsIgnoreCase(incomingPacket.getAddress().toString())) {
            this.bActive = true;
            this.bDhcp = false;
            this.RxIpAddress = incomingPacket.getAddress();
        } else if (this.IpAddress.getHostAddress().equalsIgnoreCase("0.0.0.0")) {
            this.bActive = true;
            this.bDhcp = true;
            this.RxIpAddress = getDHCPIP(incomingPacket.getData());
        }
    }

    public NetBurnerDevice(byte b) {
        String sip = "10.1.1.";
        sip = String.valueOf(sip) + b;
        this.AppName = "APP" + b;
        this.ModuleName = "Mod" + b;
        this.RootMac = "00-00-00-00-" + b;
        try {
            this.IpAddress = InetAddress.getByName(sip);
        } catch (UnknownHostException uh) {
            this.bActive = false;
        }
        this.RxIpAddress = this.IpAddress;
        this.bDhcp = false;
        this.bActive = true;
    }

    private static String getTrailingMacAddr(String mac) {
        StringBuilder sb = new StringBuilder();
        sb.append(mac.charAt(12));
        sb.append(mac.charAt(13));
        sb.append(mac.charAt(15));
        sb.append(mac.charAt(16));
        return sb.toString();
    }

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

    private static InetAddress getDHCPIP(byte[] data) {
        int offset = 131;
        int toRead = offset + 4;
        int index = 0;
        byte[] returnByte = new byte[toRead - offset];
        while (offset < toRead) {
            returnByte[index++] = data[offset++];
        }
        try {
            return InetAddress.getByAddress(returnByte);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static InetAddress getIP(byte[] data) {
        int offset = 9;
        int toRead = offset + 4;
        int index = 0;
        byte[] returnByte = new byte[toRead - offset];
        while (offset < toRead) {
            returnByte[index++] = data[offset++];
        }
        try {
            return InetAddress.getByAddress(returnByte);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    private static String getMacAddr(byte[] data) {
        int offset = 112;
        int toRead = offset + 6;
        int index = 0;
        byte[] returnByte = new byte[toRead - offset];
        while (offset < toRead) {
            returnByte[index++] = data[offset++];
        }
        return toHexMacString(returnByte);
    }

    private static String getParentMacAddr(byte[] data) {
        int offset = 123;
        int toRead = offset + 6;
        int index = 0;
        byte[] returnByte = new byte[toRead - offset];
        while (offset < toRead) {
            returnByte[index++] = data[offset++];
        }
        return toHexMacString(returnByte);
    }

    public static int getInterface(byte[] data) {
        int offset = 129;
        int toRead = offset + 1;
        int index = 0;
        byte[] returnByte = new byte[toRead - offset];
        while (offset < toRead) {
            returnByte[index++] = data[offset++];
        }
        return byteArrayToInt(returnByte, 1);
    }

    private static String[] getNameAndAppName(byte[] data) {
        int offset = 153;
        int nameIndex = 0;
        int appNameIndex = 0;
        byte[] Name = new byte[16];
        for (int i = 0; i < 16; i++) {
            Name[i] = 0;
        }
        byte[] AppName = new byte[64];
        while (data[offset] != 0) {
            Name[nameIndex++] = data[offset++];
        }
        Name[nameIndex++] = data[offset++];
        while (data[offset] != 0) {
            AppName[appNameIndex++] = data[offset++];
        }
        String[] returnStr = {new String(Name, 0, nameIndex - 1), new String(AppName, 0, appNameIndex)};
        return returnStr;
    }

    static char[] hexChar = new char[]{
        '0', '1', '2', '3',
        '4', '5', '6', '7',
        '8', '9',
        'A', 'B',
        'C', 'D', 'E', 'F'};

    public static String toHexMacString(byte[] b) {
        StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; i++) {
            sb.append(hexChar[(b[i] & 0xF0) >>> 4]);
            sb.append(hexChar[b[i] & 0xF]);
            if (i != 5) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    private static int charToNibble(char c) {
        if ('0' <= c && c <= '9') {
            return c - 48;
        }
        if ('a' <= c && c <= 'f') {
            return c - 97 + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 65 + 10;
        }
        throw new IllegalArgumentException("Invalid hex character: " + c);
    }

    private static int byteArrayToInt(byte[] b, int j) {
        int value = 0;
        for (int i = 0; i < j; i++) {
            int shift = (j - 1 - i) * 8;
            value += (b[i] & 0xFF) << shift;
        }
        return value;
    }

    public boolean SameDevice(NetBurnerDevice nb) {
        if (this.RootMac.equals(nb.RootMac)) {
            return true;
        }
        return false;
    }

    boolean equal(NetBurnerDevice nb) {
        if (SameDevice(nb)) {
            if (this.IpAddress.equals(nb.IpAddress)) {
                return true;
            }
        }
        return false;
    }

    public String GetName() {
        return this.AppName;
    }

    public String GetModuleName() {
        return this.ModuleName;
    }

    public String GetRootMac() {
        return this.RootMac;
    }

    public InetAddress GetIpAddress() {
        return this.IpAddress;
    }

    public InetAddress GetRxIpAddress() {
        return this.RxIpAddress;
    }

    public String toString() {
        if (this.bDhcp) {
            String str = this.RxIpAddress.toString();
            if (str.indexOf("/") == 0) {
                str = str.substring(1);
            }
            return String.valueOf(this.ModuleName) + " dhcped at " + str + " Running " + this.AppName;
        }
        String TrimIp = this.IpAddress.toString();
        if (TrimIp.indexOf("/") == 0) {
            TrimIp = TrimIp.substring(1);
        }
        return String.valueOf(this.ModuleName) + " at " + TrimIp + " Running " + this.AppName;
    }
}
