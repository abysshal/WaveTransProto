package info.dreamingfish123.WaveTransProto.codec;

public class WaveEncoder {

	/*
	 * Encode the data to self-defined wave format
	 */
	public static byte[] encode(byte[] data) {
		byte[] ret = new byte[data.length * Constant.BIT_PER_BYTE
				* Constant.POINT_PER_BIT + Constant.WAVE_HEAD_LEN];
		// Get wave header
		System.arraycopy(Constant.WAVE_HEADER, 0, ret, 0,
				Constant.WAVE_HEADER.length);
		int retOffset = Constant.WAVE_HEADER.length;

		for (int i = 0; i < data.length; i++) {
			// UART start
			convertBit(ret, retOffset, 0);
			retOffset += Constant.POINT_PER_BIT;

			// UART data
			for (int j = 0; j < 8; j++) {
				convertBit(ret, retOffset, (data[i] & (0x01 << (7 - j))));
				retOffset += Constant.POINT_PER_BIT;
			}

			// UART stop
			convertBit(ret, retOffset, 1);
			retOffset += Constant.POINT_PER_BIT;
		}

		// set data length field in header
		Util.int2byte(data.length * Constant.BIT_PER_BYTE
				* Constant.POINT_PER_BIT, ret, Constant.WAVE_DATA_LEN_OFFSET);
		// set file length field in header
		Util.int2byte(data.length * Constant.BIT_PER_BYTE
				* Constant.POINT_PER_BIT + Constant.WAVE_HEAD_LEN - 8, ret,
				Constant.WAVE_FILE_LEN_OFFSET);

		return ret;

	}

	/*
	 * Impl the Manchester encoding, and fit the POINT_PER_SAMPLE
	 */
	private static void convertBit(byte[] ret, int offset, int bit) {
		int index = offset;
		for (int i = 0; i < Constant.POINT_PER_BIT / 2; i++) {
			ret[index++] = (bit > 0 ? Constant.WAVE_LOW_LEVEL
					: Constant.WAVE_HIGH_LEVEL);
		}
		for (int i = 0; i < Constant.POINT_PER_BIT / 2; i++) {
			ret[index++] = (bit > 0 ? Constant.WAVE_HIGH_LEVEL
					: Constant.WAVE_LOW_LEVEL);
		}
	}
}
