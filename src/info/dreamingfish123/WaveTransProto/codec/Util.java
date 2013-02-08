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
}
