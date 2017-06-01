package org.ichanging.qpboc.util;

/**
 * Created by ChangingP on 16/6/8.
 */
public class HexUtil {

    /**
     * Utility method to convert a byte array to a hexadecimal string.
     *
     * @param bytes Bytes to convert
     * @return String, containing hexadecimal representation.
     */
    public static String ByteArrayToHexString(byte[] bytes) {

        if(bytes == null) return "";

        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[v >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[v & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    /**
     * Utility method to convert a hexadecimal string to a byte string.
     *
     * <p>Behavior with input strings containing non-hexadecimal characters is undefined.
     *
     * @param s String containing hexadecimal characters to convert
     * @return Byte array generated from input
     * @throws java.lang.IllegalArgumentException if input length is incorrect
     */
    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {

        if(s == null) return null;

        int len = s.length();
        if (len % 2 == 1) {
            throw new IllegalArgumentException("Hex string must have even number of characters");
        }
        byte[] data = new byte[len / 2]; // Allocate 1 byte per 2 hex characters
        for (int i = 0; i < len; i += 2) {
            // Convert each character into a integer (base-16), then bit-shift into place
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }


    /**
     * Utility method to convert 4 bytes array to a int (unsigned).
     *
     * @param c4
     * @return
     */
    public static int Byte4ToUnsignedInt(byte[] c4)
    {
        if(c4 == null)
        {
            return 0;
        }

        return (((int)(c4[0]) << 24) + ((int)((c4[1])) << 16) +((int)((c4[2])) << 8) + ((c4[3])));
    }

    /**
     * Utility method to convert a int(unsigned) to 4 bytes array.
     *
     * @param num
     * @return
     */
    public static byte[] UnsignedIntToByte4(int num)
    {
        byte[] c4 = new byte[4];

        c4[0] = (byte)(num >> 24);
        c4[1] = (byte)((num >> 16) & 0x00FF);
        c4[2] = (byte)((num >> 8) & 0x00FF);
        c4[3] = (byte)(num & 0x00FF);

        return c4;
    }

}
