package info.dreamingfish123.WaveTransProto.codec;

public class WaveDecoder {

	/*
	 * Decode self-defined wave format to data
	 */
	public static byte[] decode(byte[] wavein, int offset, int len) {
		byte[] ret = new byte[len / Constant.POINT_PER_SAMPLE
				/ Constant.BIT_PER_BYTE];
		int retOffset = offset;
		int bit = 0;

		for (int i = 0; i < ret.length; i++) {
			ret[i] = 0;
			
			bit = convertBit(wavein, retOffset);
			retOffset += Constant.POINT_PER_SAMPLE;

			for (int j = 0; j < 8; j++) {
				bit = convertBit(wavein, retOffset);
				retOffset += Constant.POINT_PER_SAMPLE;
				
				ret[i] = (byte) (ret[i] + (bit << (7-j)) & 0xFF);
			}

			bit = convertBit(wavein, retOffset);
			retOffset += Constant.POINT_PER_SAMPLE;

		}

		return ret;
	}

	/*
	 * Get bit from wave format
	 */
	private static int convertBit(byte[] wavein, int offset) {
		final int half = Constant.POINT_PER_SAMPLE / 2;
		int ave1 = 0;
		int ave2 = 0;

		for (int i = 0; i < half; i++) {
			ave1 += 0xff & wavein[offset + i];
			ave2 += 0xff & wavein[offset + i + half];
		}

		// ave1 /= half;
		// ave2 /= half;

		if (ave1 > ave2) {
			return 0;
		} else {
			return 1;
		}
	}

	/*
	 * Find the first byte of data:0xff
	 */
	private static int locateDataStartSignal(byte[] wavein) {
		final int half = Constant.POINT_PER_SAMPLE / 2;
		for (int i = 0; i < wavein.length - half; i++) {
			if (wavein[i] >= wavein[i + half] + Constant.WAVE_DIFF_LEVEL) {
				return i;
			}
		}
		return -1;
	}
}
