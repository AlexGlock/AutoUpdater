package agNBAutoUpdate;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Random;

public class NetBurnerCoreUpdate {

    update_state m_eState;

    long m_dwCurrent_acked_addr;

    long m_dwCurrent_sent_addr;

    long m_dwRandomValue;

    long m_nTimeOutCount;

    long m_nLastInterval;

    long m_dwBaseaddress;

    long m_dwRecordLen;

    long m_dwExpectedNextAck;

    long m_nLastRTT;

    int m_nRetries;

    int m_nOutOfOrder;

    int m_nMaxRetry;

    UpdateNotify m_nbu;

    InetAddress m_Device_Address;

    String m_FileName;

    boolean m_RebootWhenDone;

    String m_UserName;

    String m_PassWord;

    S19Blob m_ConvertedS19;

    DatagramSocket m_ds;

    private static final int FIRSTDEF_TIMEOUT = 20;

    private static final int MAX_RETRY = 9;

    private static final int MAX_DATABLOCK = 470;

    private static final long VERIFY_FROM_PC_TO_NDK = 1112887886L;

    private static final long VERIFY_FROM_NDK_TO_PC = 2020181094L;

    private static final int UDP_NETBURNERID_PORT = 20034;

    private static final char NBAUTO_UPDATE = 'A';

    private static final int UPDATE_ACTION_START = 1;

    private static final int UPDATE_ACTION_DATA = 2;

    private static final int UPDATE_ACTION_EXECUTE = 3;

    private static final int UPDATE_ACTION_REBOOT = 4;

    private static final int AUTO_ACK = 0;

    private static final int AUTO_AUTH_NEEDED = 1;

    private static final int AUTO_AUTH_FAILED = 2;

    private static final int AUTO_NOMEM = 3;

    private static final int AUTO_NPSHUTDOWN = 4;

    private static final int AUTO_RECORD_MISMATCH = 5;

    private static final int AUTO_CSUM_FAILED = 6;

    private static final int AUTO_OUT_OF_SEQ = 7;

    private static final int AUTO_RESULT_ERR = 8;

    private enum update_state {
        Starting, NeedPassword, AuthFailed, SendingData, Programming, RebootWait, Complete, TimedOut;
    }

    public static interface UpdateNotify {

        public static final int Cause_NetWorkError = 1;

        public static final int Cause_DeviceOutOfMemeory = 2;

        public static final int Cause_DeviceWontClose = 3;

        public static final int Cause_PlatformMisMatch = 4;

        public static final int Cause_PasswordNeeded = 5;

        public static final int Cause_PasswordFailed = 6;

        public static final int Cause_TimeOut = 7;

        public static final int Cause_Aborted = 8;

        public static final int Cause_Failed = 9;

        public static final int Cause_Badfile = 10;

        void SetPercentDone(int param1Int);

        void NotifyError(String param1String, int param1Int);

        boolean ShouldAbort();
    }

    protected static class NB_Update_DataGramRecord {

        public long dwIdKey;

        public int bNetBurnerPktType;

        public int bAction;

        public int bExtra1;

        public int bExtra2;

        public long Random_Record_Num;

        public long dwAddressOfThisRecord;

        public long dwThisLen;

        public byte[] bData;

        class BadNBPacketException extends Exception {

            public String toString() {
                return "BadNBPacketException";
            }
        }

        public static NB_Update_DataGramRecord RecieveFromSocket(DatagramSocket ds) {
            byte[] buffer = new byte[1024];
            DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
            try {
                ds.receive(incomingPacket);
                return new NB_Update_DataGramRecord(incomingPacket);
            } catch (Exception e) {
                return null;
            }
        }

        final int ExtractByte(byte[] buf, int offset) {
            int i = buf[offset];
            if (i < 0) {
                i += 256;
            }
            return i;
        }

        final long ExtractDWord(byte[] buf, int offset) {
            long lval = ExtractByte(buf, offset++);
            lval = (lval << 8L) + ExtractByte(buf, offset++);
            lval = (lval << 8L) + ExtractByte(buf, offset++);
            lval = (lval << 8L) + ExtractByte(buf, offset++);
            return lval;
        }

        public NB_Update_DataGramRecord(DatagramPacket incomingPacket) throws BadNBPacketException {
            byte[] raw_packet = incomingPacket.getData();
            int len = incomingPacket.getLength();
            this.dwIdKey = 0L;
            this.bNetBurnerPktType = 0;
            this.bAction = 0;
            this.bExtra1 = 0;
            this.bExtra2 = 0;
            this.Random_Record_Num = 0L;
            this.dwAddressOfThisRecord = 0L;
            this.dwThisLen = 0L;
            this.bData = null;
            this.dwIdKey = ExtractDWord(raw_packet, 0);
            this.bNetBurnerPktType = ExtractByte(raw_packet, 4);
            this.bAction = ExtractByte(raw_packet, 5);
            this.bExtra1 = ExtractByte(raw_packet, 6);
            this.bExtra2 = ExtractByte(raw_packet, 7);
            this.Random_Record_Num = ExtractDWord(raw_packet, 8);
            this.dwAddressOfThisRecord = ExtractDWord(raw_packet, 12);
            this.dwThisLen = ExtractDWord(raw_packet, 16);
            this.bData = Arrays.copyOfRange(raw_packet, 20, len);
            if (this.dwIdKey != 1112887886L) {
                throw new BadNBPacketException();
            }
        }

        static void StuffOneByte(byte[] buf, int i, int offset) {
            i &= 0xFF;
            if (i >= 128) {
                i -= 256;
            }
            buf[offset] = (byte) i;
        }

        static void StuffDWData(byte[] buf, long l, int offset) {
            l &= 0xFFFFFFFFFFFFFFFFL;
            StuffOneByte(buf, (int) ((l & 0xFFFFFFFFFF000000L) >> 24L), offset++);
            StuffOneByte(buf, (int) ((l & 0xFF0000L) >> 16L), offset++);
            StuffOneByte(buf, (int) ((l & 0xFF00L) >> 8L), offset++);
            StuffOneByte(buf, (int) (l & 0xFFL), offset++);
        }

        static void StuffBytes(byte[] buf, byte[] data, int offset) {
            if (data != null) {
                System.arraycopy(data, 0, buf, offset, data.length);
            }
        }

        void SendTo(DatagramSocket ds, InetAddress addr) throws IOException {
            int siz = 20;
            if (this.bData != null) {
                siz += this.bData.length;
            } else {
                siz += 4;
            }
            byte[] buffer = new byte[siz];
            StuffDWData(buffer, this.dwIdKey, 0);
            StuffOneByte(buffer, this.bNetBurnerPktType, 4);
            StuffOneByte(buffer, this.bAction, 5);
            StuffOneByte(buffer, this.bExtra1, 6);
            StuffOneByte(buffer, this.bExtra2, 7);
            StuffDWData(buffer, this.Random_Record_Num, 8);
            StuffDWData(buffer, this.dwAddressOfThisRecord, 12);
            StuffDWData(buffer, this.dwThisLen, 16);
            if (this.bData != null) {
                StuffBytes(buffer, this.bData, 20);
            } else {
                StuffOneByte(buffer, 0, 20);
                StuffOneByte(buffer, 0, 21);
                StuffOneByte(buffer, 0, 22);
                StuffOneByte(buffer, 0, 23);
            }
            DatagramPacket dp = new DatagramPacket(buffer, siz, addr, 20034);
            ds.send(dp);
        }

        public NB_Update_DataGramRecord() {
            this.dwIdKey = 0L;
            this.bNetBurnerPktType = 0;
            this.bAction = 0;
            this.bExtra1 = 0;
            this.bExtra2 = 0;
            this.Random_Record_Num = 0L;
            this.dwAddressOfThisRecord = 0L;
            this.dwThisLen = 0L;
            this.bData = null;
        }
    }

    protected NetBurnerCoreUpdate(UpdateNotify nbu, String Device_Address, String FileName, boolean RebootWhenDone, String UserName, String PassWord) {
        this.m_nbu = nbu;
        try {
            this.m_Device_Address = InetAddress.getByName(Device_Address);
        } catch (Exception exception) {
        }
        this.m_FileName = FileName;
        this.m_RebootWhenDone = RebootWhenDone;
        this.m_UserName = UserName;
        this.m_PassWord = PassWord;
    }

    long CalcTimeOut() {
        if (this.m_nLastRTT <= 0L) {
            return 4L;
        }
        return 4L * this.m_nLastRTT;
    }

    protected void SendPacket() {
        long offset;
        NB_Update_DataGramRecord Txb = new NB_Update_DataGramRecord();
        Txb.Random_Record_Num = this.m_dwRandomValue;
        long len = 20L;
        switch (this.m_eState) {
            case Starting:
                Txb.bAction = 1;
                Txb.dwAddressOfThisRecord = this.m_dwBaseaddress;
                Txb.dwThisLen = this.m_dwRecordLen;
                Txb.bData = null;
                this.m_nTimeOutCount = 20L;
                break;
            case NeedPassword:
                Txb.bAction = 1;
                Txb.dwAddressOfThisRecord = this.m_dwBaseaddress;
                Txb.dwThisLen = this.m_dwRecordLen;
                if (this.m_UserName != null && this.m_PassWord != null) {
                    int slen = this.m_UserName.length() + this.m_PassWord.length() + 2;
                    len += slen;
                    byte[] ba = new byte[slen];
                    for (int i = 0; i < this.m_UserName.length();) {
                        ba[i] = (byte) this.m_UserName.charAt(i);
                        i++;
                    }
                    ba[this.m_UserName.length()] = 0;
                    int buf_offset = this.m_UserName.length() + 1;
                    for (int j = 0; j < this.m_PassWord.length();) {
                        ba[j + buf_offset] = (byte) this.m_PassWord.charAt(j);
                        j++;
                    }
                    ba[buf_offset + this.m_PassWord.length()] = 0;
                    Txb.bData = ba;
                    this.m_nTimeOutCount = 20L;
                    this.m_nLastInterval = 20L;
                }
                break;
            case SendingData:
                Txb.bAction = 2;
                Txb.dwAddressOfThisRecord = this.m_dwCurrent_acked_addr;
                if (Txb.dwAddressOfThisRecord + 470L > this.m_dwBaseaddress + this.m_dwRecordLen) {
                    this.m_nTimeOutCount = CalcTimeOut();
                    this.m_nLastInterval = this.m_nTimeOutCount;
                    Txb.dwThisLen = this.m_dwBaseaddress + this.m_dwRecordLen - Txb.dwAddressOfThisRecord;
                } else {
                    Txb.dwThisLen = 470L;
                    if (Txb.dwAddressOfThisRecord + 470L < this.m_dwBaseaddress + 940L) {
                        this.m_nLastInterval = 20L;
                    } else {
                        this.m_nLastInterval = CalcTimeOut();
                    }
                    this.m_nTimeOutCount = this.m_nLastInterval;
                    this.m_dwExpectedNextAck = Txb.dwAddressOfThisRecord + Txb.dwThisLen;
                }
                offset = Txb.dwAddressOfThisRecord - this.m_dwBaseaddress;
                Txb.bData = Arrays.copyOfRange(this.m_ConvertedS19.Blob, (int) offset, (int) (offset + Txb.dwThisLen));
                this.m_dwCurrent_sent_addr = this.m_dwCurrent_acked_addr + Txb.dwThisLen;
                len += Txb.dwThisLen;
                break;
            case Programming:
                Txb.bAction = 3;
                Txb.dwAddressOfThisRecord = this.m_dwBaseaddress + this.m_dwRecordLen;
                Txb.dwThisLen = this.m_ConvertedS19.GetCsum();
                this.m_nTimeOutCount = 20L;
                break;
            case RebootWait:
                Txb.bAction = 4;
                Txb.dwAddressOfThisRecord = this.m_dwBaseaddress + this.m_dwRecordLen;
                Txb.dwThisLen = this.m_ConvertedS19.GetCsum();
                this.m_nTimeOutCount = 20L;
                break;
            case Complete:
                return;
            //TODO: The original code here does not compile 
            //"case null" is beeing implemented in https://openjdk.java.net/jeps/420 which in java 17 and 18 (switch pattern matching?) only avaliable as preview feature
            //furthermore there were significant changes to switch expressions in java since java 12/13 (preview) 14 (standard).
            //https://docs.oracle.com/en/java/javase/18/language/java-language-changes.html
            //The case null works when preview features are enabled in Java 18, but then either complains about missing cases (at least default) or wants to use switch expressions.
            //case null:
            //  return;
            //We add a default instead, but then the question remains what the code below is supposed to do?
            default:
                return;
        }
        try {
            Txb.bNetBurnerPktType = 65;
            Txb.dwIdKey = 1112887886L;
            Txb.Random_Record_Num = this.m_dwRandomValue;
            Txb.SendTo(this.m_ds, this.m_Device_Address);
        } catch (IOException iOException) {
        }
    }

    protected void ProcessPacket(NB_Update_DataGramRecord Rxp) {
        if (Rxp.Random_Record_Num != this.m_dwRandomValue) {
            this.m_nbu.NotifyError("Record Identifier does not match", 1);
            return;
        }
        this.m_nRetries = 0;
        if (Rxp.bAction == 5) {
            this.m_nbu.NotifyError("Returned Update REcord Mismatch", 1);
            return;
        }
        switch (this.m_eState) {
            case Starting:
            case NeedPassword:
                switch (Rxp.bAction) {
                    case 0:
                        this.m_eState = update_state.SendingData;
                        if ((((Rxp.bData != null) ? 1 : 0) & ((Rxp.bData.length > 5) ? 1 : 0)) != 0) {
                            int i = 4;
                            for (; i < Rxp.bData.length && Rxp.bData[i] != 0; i++);
                            String pName = new String(Rxp.bData, 4, i - 4);
                            if (pName != null && this.m_ConvertedS19.getS0Value() != null && !pName.equals(this.m_ConvertedS19.getS0Value())) {
                                this.m_nbu.NotifyError("Platform Mismatch Device =" + pName + " S19 =" + this.m_ConvertedS19.getS0Value(), 4);
                                this.m_eState = update_state.TimedOut;
                                return;
                            }
                        }
                        SendPacket();
                        return;
                    case 1:
                        this.m_eState = update_state.NeedPassword;
                        if (this.m_UserName == null || this.m_PassWord == null) {
                            this.m_nbu.NotifyError("Device needs UserName Password", 5);
                            this.m_eState = update_state.AuthFailed;
                            return;
                        }
                        this.m_eState = update_state.NeedPassword;
                        SendPacket();
                        return;
                    case 2:
                        this.m_nbu.NotifyError("Username Password Failed", 6);
                        this.m_eState = update_state.AuthFailed;
                        return;
                    case 3:
                        this.m_nbu.NotifyError("Device is out of Memory", 2);
                        this.m_eState = update_state.AuthFailed;
                        return;
                    case 4:
                        this.m_nbu.NotifyError("Device refused to shutdown", 3);
                        System.out.println("authorization Error - reboot device");
                        this.m_eState = update_state.AuthFailed;
                        return;
                }
                break;
            case SendingData:
                if (Rxp.bAction == 0) {
                    this.m_dwCurrent_acked_addr = Rxp.dwAddressOfThisRecord;
                    if (this.m_dwCurrent_acked_addr >= this.m_dwBaseaddress + this.m_dwRecordLen) {
                        if (this.m_RebootWhenDone) {
                            this.m_eState = update_state.RebootWait;
                        } else {
                            this.m_eState = update_state.Programming;
                        }
                        this.m_nbu.SetPercentDone(100);
                    } else {
                        long per = 100L * (this.m_dwCurrent_acked_addr - this.m_dwBaseaddress) / this.m_dwRecordLen;
                        this.m_nbu.SetPercentDone((int) per);
                        this.m_nLastRTT = this.m_nLastInterval - this.m_nTimeOutCount;
                    }
                }
                if (Rxp.bAction == 7) {
                    this.m_dwCurrent_acked_addr = Rxp.dwAddressOfThisRecord;
                    this.m_nOutOfOrder++;
                    SendPacket();
                }
                break;
            case Programming:
            case RebootWait:
                if (Rxp.bAction == 0) {
                    this.m_eState = update_state.Complete;
                    return;
                }
                if (Rxp.bAction == 6) {
                    this.m_nbu.NotifyError("Download check sum failed", 9);
                    this.m_eState = update_state.AuthFailed;
                    return;
                }
                break;
            case Complete:
                return;
        }
    }

    void OnTimeOut() {
        if (this.m_eState != update_state.NeedPassword) {
            this.m_nTimeOutCount--;
            if (this.m_nTimeOutCount == 0L) {
                SendPacket();
                this.m_nRetries++;
                if (this.m_nRetries > this.m_nMaxRetry) {
                    this.m_nMaxRetry = this.m_nRetries;
                }
                if (this.m_nRetries > 200 ) {
                    this.m_eState = update_state.TimedOut;
                    this.m_nbu.NotifyError("ERROR - Timed out", 7);
                    System.exit(1);
                }
            }
        }
    }

    protected boolean DoUpdate() {
        this.m_ConvertedS19 = S19Blob.S19FromFile(this.m_FileName);
        if (this.m_ConvertedS19 == null) {
            this.m_nbu.NotifyError("Failed to open and convert File:" + this.m_FileName, 10);
        }
        this.m_dwBaseaddress = this.m_ConvertedS19.BlobStartAddress;
        this.m_dwRecordLen = this.m_ConvertedS19.Blob.length;
        this.m_eState = update_state.Starting;
        this.m_dwCurrent_acked_addr = this.m_dwBaseaddress;
        this.m_dwCurrent_sent_addr = this.m_dwBaseaddress;
        this.m_dwRandomValue = (new Random()).nextLong();
        long lt = 4294967295L;
        this.m_dwRandomValue &= lt;
        this.m_nRetries = 0;
        this.m_nOutOfOrder = 0;
        this.m_nMaxRetry = 0;
        this.m_nLastInterval = 0L;
        this.m_dwExpectedNextAck = 0L;
        try {
            this.m_ds = new DatagramSocket(20034);
            this.m_nbu.SetPercentDone(0);
            this.m_ds.setSoTimeout(2);
            // This code initially sends the first packet and then waits for timeout ms before checking if a reply was received.
            // this.m_ds.receive(incommingPacket) is blocking until timeout or length has been received.
            // The initial timeout was 250 ms.
            // There are about 600 timeouts when transmitting a reasonably sized file.
            // This takes forever.
            // If the timeout is to small and the acknowledge is not received, then the code exits imediately.
            // So far 20 seemed ok, but will likely fail under certain circumstances.
            // TODO: Find a better solution then to always wait for the maximum timeout, so the timeout can be longer to accomodate delays but is not taken into account if the answer we are waiting for has already arrived.
            // FIX from A.G.: I trimmed the socket Timeout down to 10ms and increased the maximum number of retries from 10 to 500 and remove {|| (this.m_eState == update_state.Starting && this.m_nRetries >= 2)} as argument (line 483).
            // This allows a max delay of 5s, while running fast enough on socket.receive().
            SendPacket();
            do {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket incomingPacket = new DatagramPacket(buffer, buffer.length);
                    this.m_ds.receive(incomingPacket);
                    if (this.m_nbu.ShouldAbort()) {
                        this.m_ds.close();
                        this.m_nbu.NotifyError("Abort requested", 8);
                        return false;
                    }
                    NB_Update_DataGramRecord Rxp = new NB_Update_DataGramRecord(incomingPacket);
                    ProcessPacket(Rxp);
                    if (this.m_eState == update_state.AuthFailed) {
                        this.m_ds.close();
                        this.m_nbu.NotifyError("Abort requested", 8);
                        return false;
                    }
                } catch (SocketTimeoutException e) {
                    if (this.m_nbu.ShouldAbort()) {
                        this.m_ds.close();
                        this.m_nbu.NotifyError("Abort requested", 8);
                        return false;
                    }
                    OnTimeOut();
                }
            } while ((((this.m_eState != update_state.TimedOut) ? 1 : 0) & ((this.m_eState != update_state.Complete) ? 1 : 0) & ((this.m_eState != update_state.AuthFailed) ? 1 : 0)) != 0);
        } catch (Exception e) {
            if (this.m_ds != null) {
                this.m_ds.close();
            }
            System.out.println("General exception " + e);
            System.out.println("Connection Error - retry update");
            return false;
        }
        System.out.println("max retries: " + this.m_nMaxRetry);
        this.m_ds.close();
        return true;
    }

    public static boolean CoreUpdateAction(UpdateNotify nbu, String Device_Address, String FileName, boolean RebootWhenDone, String UserName, String PassWord) {
        NetBurnerCoreUpdate nbcu = new NetBurnerCoreUpdate(nbu, Device_Address, FileName, RebootWhenDone, UserName, PassWord);
        boolean rv = nbcu.DoUpdate();
        return rv;
    }
}
