package info.dreamingfish123.WaveTransProto.codec;

public class Util {
	
	public static void int2byte(int val, byte[] a, int offset) {
		a[offset] = (byte) ((val >> 0) & 0xFF);
		a[1 + offset] = (byte) ((val >> 8) & 0xFF);
		a[2 + offset] = (byte) ((val >> 16) & 0xFF);
		a[3 + offset] = (byte) ((val >> 24) & 0xFF);
	}
}
