package info.dreamingfish123.WaveTransProto.codec;

public class Constant {

	public static final int WAVE_RATE_INHZ = 44100;
	public static final int MAX_TRANSFER_DATA_LEN = 200;
	public static final int POINT_PER_BIT = 6;
	public static final int POINT_PER_BIT_HALF = 3;
	public static final int BIT_PER_BYTE = 10;
	public static final int POINT_PER_UART = POINT_PER_BIT * BIT_PER_BYTE;
	public static final int WAVE_HEAD_LEN = 0x2c;
	public static final int WAVEOUT_BUF_SIZE = MAX_TRANSFER_DATA_LEN
			* POINT_PER_BIT * BIT_PER_BYTE + WAVE_HEAD_LEN;
	public static final int WAVE_DATA_LEN_OFFSET = 0x28;
	public static final int WAVE_FILE_LEN_OFFSET = 0x04;
	public static final byte WAVE_HIGH_LEVEL = (byte) 0xF0;
	public static final byte WAVE_LOW_LEVEL = (byte) 0x10;
	public static final byte WAVE_MUTE_LEVEL = (byte) 0x80;
	public static final byte WAVE_DIFF_LEVEL = 0x40;
	public static final int WAVE_DIFF_SUM_LEVEL = POINT_PER_BIT_HALF
			* WAVE_DIFF_LEVEL;
	public static final byte WAVE_HEADER[] = { 0x52, 0x49, 0x46, 0x46,
			(byte) 0xF8, 0x00, 0x00, 0x00, 0x57, 0x41, 0x56, 0x45, 0x66, 0x6D,
			0x74, 0x20, 0x10, 0x00, 0x00, 0x00, 0x01, 0x00, 0x01, 0x00, 0x44,
			(byte) 0xAC, 0x00, 0x00, 0x44, (byte) 0xAC, 0x00, 0x00, 0x01, 0x00,
			0x08, 0x00, 0x64, 0x61, 0x74, 0x61, 0x00, 0x00, 0x00, 0x00 };
	public static final byte PACKET_START_FLAG = (byte) 0xFF;
}
