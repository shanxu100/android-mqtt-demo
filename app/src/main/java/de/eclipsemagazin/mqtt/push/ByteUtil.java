package de.eclipsemagazin.mqtt.push;

/**
 * @author Guan
 * @date 2017/9/18
 */
public class ByteUtil {

    private ByteUtil() {
        throw new AssertionError("工具类不可实例化");
    }

    //将byte型数据转换为0~255 (0xFF 即BYTE)
    public static int getUnsignedByte(byte data) {
        return data & 0x0FF;
    }

    public static void main(String[] args) {
        //999999 99
//        byte byte0 = 0x01;
//        byte byte1 = (byte) 0x02;
//        byte byte2 = (byte) 0x03;
//        byte byte3 = (byte) 0x04;
//        byte[] bytes=new byte[]{0x00,0x01 ,0x01 ,0x01 ,0x00 ,0x57};
//        String result = bcdToStr(bytes);
//
//        System.out.println(result);

        int waterValue=9999;
        String value = String.format( "%.1f",9999*0.01);
        System.out.println(value);



    }

    /**
     * 将2字节byte[]数组型数据转换为0~65535
     * byte[]高位在前，低位在后
     *
     * @param byte0
     * @param byte1
     * @return
     */
    public static int toInteger(byte byte0, byte byte1) {
        int result = 0;
        result += (byte0 & 0x0FF) << 8;
        result += byte1 & 0x0FF;
        return result;
    }

    /**
     * 将4字节byte[]转换为 0 ~ 2^31 - 1
     * 高位在前，低位在后
     *
     * @param byte0
     * @param byte1
     * @param byte2
     * @param byte3
     * @return
     */
    public static int toInteger(byte byte0, byte byte1, byte byte2, byte byte3) {
        int result = 0;
        result += (byte0 & 0x0FF) << 24;
        result += (byte1 & 0x0FF) << 16;
        result += (byte2 & 0x0FF) << 8;
        result += byte3 & 0x0FF;
        return result;
    }

    /**
     * 将0~65535转换为2字节byte[]数组型数据
     *
     * @param i
     * @return
     */
    public static byte[] toByteArray(int i) {
        if (i <= 65535) {
            byte[] result = new byte[2];
            result[0] = (byte) ((i >> 8) & 0xFF);
            result[1] = (byte) (i & 0xFF);
            return result;
        }
        System.err.println(" int-->byte[2], 超出65535");
        return null;
    }

    /**
     * int 转 4字节的byte[]
     *
     * @param i
     * @return
     */
    public static byte[] toByteArray4(int i) {
        byte[] result = new byte[4];
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }

    /**
     * String --> byte[]
     *
     * @param s
     * @return
     */
    public static byte[] string2bytes(String s) {
        char[] chars = s.toCharArray();
        byte[] bytes = new byte[chars.length];

        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }

        return bytes;
    }

    /**
     * byte[] --> String
     *
     * @param bytes
     * @return
     */
    public static String byte2string(byte[] bytes) {
        int count = bytes.length;
        char[] chars = new char[count];
        for (int i = 0; i < count; i++) {
            if (bytes[i] < 0) {
                chars[i] = (char) (bytes[i] + 256);
            } else {
                chars[i] = (char) (bytes[i]);
            }
        }
        return String.valueOf(chars);
    }


    /**
     * @功能: BCD码转为10进制串(阿拉伯数据)
     * @参数: BCD码
     * @结果: 10进制串
     */
    public static String bcdToStr(byte[] bytes) {
        StringBuffer temp = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            temp.append((byte) ((bytes[i] & 0xf0) >>> 4));
            temp.append((byte) (bytes[i] & 0x0f));
        }
//        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp
//                .toString().substring(1) : temp.toString();
        return temp.toString();
    }

    /**
     * 10进制串 --> BCD码
     *
     * @param asc
     * @return
     */
    public static byte[] strToBcd(String asc) {
        int len = asc.length();
        int mod = len % 2;
        if (mod != 0) {
            asc = "0" + asc;
            len = asc.length();
        }
        byte abt[] = new byte[len];
        if (len >= 2) {
            len = len / 2;
        }
        byte bbt[] = new byte[len];
        abt = asc.getBytes();
        int j, k;
        for (int p = 0; p < asc.length() / 2; p++) {
            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9')) {
                j = abt[2 * p] - '0';
            } else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z')) {
                j = abt[2 * p] - 'a' + 0x0a;
            } else {
                j = abt[2 * p] - 'A' + 0x0a;
            }
            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9')) {
                k = abt[2 * p + 1] - '0';
            } else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z')) {
                k = abt[2 * p + 1] - 'a' + 0x0a;
            } else {
                k = abt[2 * p + 1] - 'A' + 0x0a;
            }
            int a = (j << 4) + k;
            byte b = (byte) a;
            bbt[p] = b;
        }
        return bbt;
    }


    /**
     * 将byte以16进制的形式打印出来
     *
     * @param buffer
     * @return
     */
    public static String byte2hex(byte[] buffer) {
        String h = "";

        for (int i = 0; i < buffer.length; i++) {
            String temp = Integer.toHexString(buffer[i] & 0xFF);
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            h = h + " " + temp;
        }

        return h;
    }

    /**
     * 将byte以二进制的形式打印出来
     *
     * @param b
     */
    public static void byte2binary(byte b) {
        //Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
    }

}