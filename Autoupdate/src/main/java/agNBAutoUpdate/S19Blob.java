package agNBAutoUpdate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

public class S19Blob {

    protected byte[] Blob;

    protected long BlobStartAddress;

    protected long ExecutionAddress;

    protected String S0_Value;

    protected boolean bHadParseError;

    private long CurPosition;

    private long dwCsum;

    private byte ParseSignedByte(String s) {
        Integer iv = Integer.valueOf(Integer.parseInt(s, 16));
        if (iv.intValue() >= 128) {
            iv = Integer.valueOf(iv.intValue() - 256);
        }
        return (byte) iv.intValue();
    }

    private void ParseSRecord(String line) {
        Integer DataLen;
        Long Address;
        int i;
        byte cs;
        if (line.length() < 3) {
            return;
        }
        if (line.charAt(0) != 'S') {
            return;
        }
        switch (line.charAt(1)) {
            case '0':
                this.S0_Value = line.substring(2);
                break;
            case '3':
                DataLen = Integer.valueOf(Integer.parseInt(line.substring(2, 4), 16));
                Address = Long.valueOf(Long.parseLong(line.substring(4, 12), 16));
                line = line.substring(12);
                if (this.CurPosition < 0L) {
                    this.CurPosition = 0L;
                    this.BlobStartAddress = Address.longValue();
                }
                if (Address.longValue() < this.BlobStartAddress) {
                    this.bHadParseError = true;
                    return;
                }
                this.CurPosition = Address.longValue() - this.BlobStartAddress;
                DataLen = Integer.valueOf(DataLen.intValue() - 5);
                for (i = 0; i < DataLen.intValue(); i++) {
                    String sp = line.substring(i * 2, i * 2 + 2);
                    this.Blob[(int) this.CurPosition++] = ParseSignedByte(sp);
                }
                cs = ParseSignedByte(line.substring(line.length() - 2));
                break;
            case '7':
                this.ExecutionAddress = Long.parseLong(line.substring(4, 11), 16);
                break;
        }
    }

    private S19Blob() {
        this.BlobStartAddress = -1L;
        this.ExecutionAddress = -1L;
        this.S0_Value = null;
        this.Blob = null;
        this.bHadParseError = true;
    }

    public static S19Blob S19FromFile(String fileName) {
        S19Blob BlobToBuild = new S19Blob();
        File f = new File(fileName);
        if (f.isFile()) {
            int fLength = (int) f.length();
            BlobToBuild.Blob = new byte[fLength];
            BlobToBuild.CurPosition = -1L;
            BlobToBuild.bHadParseError = false;
            String InputLine = null;
            try {
                BufferedReader inputStream = new BufferedReader(new FileReader(fileName));
                while ((InputLine = inputStream.readLine()) != null) {
                    BlobToBuild.ParseSRecord(InputLine);
                }
            } catch (Exception e) {
                System.out.println("Got exception =" + InputLine.toString());
                BlobToBuild.bHadParseError = true;
                return null;
            }
            while ((BlobToBuild.CurPosition & 0x3L) != 0L) {
                BlobToBuild.Blob[(int) BlobToBuild.CurPosition++] = 0;
            }
            BlobToBuild.Blob = Arrays.copyOf(BlobToBuild.Blob, (int) BlobToBuild.CurPosition);
            BlobToBuild.dwCsum = 0L;
            int v = 0;
            int tCsum = 0;
            for (int i = 0; i <= BlobToBuild.Blob.length - 3; i += 4) {
                v = BlobToBuild.Blob[i] & 0xFF;
                v = (v << 8) + (BlobToBuild.Blob[i + 1] & 0xFF);
                v = (v << 8) + (BlobToBuild.Blob[i + 2] & 0xFF);
                v = (v << 8) + (BlobToBuild.Blob[i + 3] & 0xFF);
                tCsum += v;
            }
            BlobToBuild.dwCsum = tCsum;
        } else {
            return null;
        }
        return BlobToBuild;
    }

    long getBlobStartAddress() {
        return this.BlobStartAddress;
    }

    long getExecutionAddress() {
        return this.ExecutionAddress;
    }

    byte[] getBlob() {
        return this.Blob;
    }

    String getS0Value() {
        return this.S0_Value;
    }

    boolean IsValid() {
        return !this.bHadParseError;
    }

    long GetCsum() {
        return this.dwCsum;
    }
}
