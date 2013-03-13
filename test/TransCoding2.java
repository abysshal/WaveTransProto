import java.io.DataInputStream;
import java.io.FileInputStream;

public class TransCoding2 {

	public final static String FILENAME = "./test/AC3_S5570_pro/wavein_AC3_S5570_5_pro_clip1";
	public final static String INPUT_SUFFIX = ".wav";
	public final static String OUTPUT_SUFFIX = "_tc.wav";

	public static void main(String[] args) throws Exception {
		byte[] src = new byte[] { (byte) 0xff, 0x7f, 0x52, 0x6e, 0x00,
				(byte) 0x80, (byte) 0xa9, (byte) 0xcf };
		int ret = 0;
		for (int i = 0; i < 4; i++) {
			ret = getShort(src, i * 2);
			System.out.println(i + ":\t" + ret);
//			ret = ret + Short.MAX_VALUE + 1;
//			ret /= 256;
			System.out.println("ret:" + ret);
		}
	}

	public static int getShort(byte[] bytes, int offset) {
		short s0 = (short) (bytes[offset] & 0xff);
		short s1 = (short) (bytes[offset + 1] & 0xff);
		return (((short) (s0 | s1 << 8)) + Short.MAX_VALUE + 1) >> 8;
	}

}
