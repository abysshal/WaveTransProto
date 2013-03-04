package info.dreamingfish123.WaveTransProto.codec;

public class Util {

	public static void int2byte(int val, byte[] a, int offset) {
		a[offset] = (byte) ((val >> 0) & 0xFF);
		a[1 + offset] = (byte) ((val >> 8) & 0xFF);
		a[2 + offset] = (byte) ((val >> 16) & 0xFF);
		a[3 + offset] = (byte) ((val >> 24) & 0xFF);
	}

	public static int byte2int(byte[] a, int offset) {
		int val;
		val = a[3 + offset] & 0xFF;
		val = (val << 8) + (a[2 + offset] & 0xFF);
		val = (val << 8) + (a[1 + offset] & 0xFF);
		val = (val << 8) + (a[offset] & 0xFF);
		return val;
	}

	public static String getHex(byte[] bytes) {
		return getHex(bytes, 0, bytes.length);
	}

	public static String getHex(byte[] bytes, int offset, int len) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			if (i != 0 && i % 16 == 0) {
				sb.append("\n");
			}
			sb.append(String.format("%02X ", bytes[i + offset]));
		}
		return sb.toString();
	}

	public static byte[] resample16To8bit(byte[] bytes) {
		return resample16To8bit(bytes, 0, bytes.length);
	}

	public static byte[] resample16To8bit(byte[] bytes, int offset, int len) {
		byte[] ret = new byte[len / 2];
		for (int i = 0; i < ret.length; i++) {
			short s0 = (short) (bytes[i * 2 + offset] & 0xff);
			short s1 = (short) (bytes[i * 2 + offset + 1] & 0xff);
			ret[i] = (byte) (((short) (s0 | s1 << 8)) / 256);
		}
		return ret;
	}

	public static short byteToShort(byte[] b, int offset) {
		short s0 = (short) (b[offset] & 0xff);
		short s1 = (short) (b[offset + 1] & 0xff);
		return (short) (s0 | s1 << 8);
	}
}
