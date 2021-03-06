package info.dreamingfish123.WaveTransProto.codec;

import java.nio.Buffer;

public class WaveDecoder {

	/**
	 * Decode self-defined wave format to data.<br/>
	 * For standard test
	 * 
	 * @param wavein
	 * @param offset
	 * @param len
	 * @return
	 */
	public static byte[] decode(byte[] wavein, int offset, int len) {
		byte[] ret = new byte[len / Constant.POINT_PER_BIT
				/ Constant.BIT_PER_BYTE];
		int retOffset = offset;
		int bit = 0;

		for (int i = 0; i < ret.length; i++) {
			ret[i] = 0;

			bit = convertBit(wavein, retOffset, false);
			retOffset += Constant.POINT_PER_BIT;

			for (int j = 0; j < 8; j++) {
				bit = convertBit(wavein, retOffset, false);
				retOffset += Constant.POINT_PER_BIT;

				ret[i] = (byte) (ret[i] + (bit << (7 - j)) & 0xFF);
			}

			bit = convertBit(wavein, retOffset, false);
			retOffset += Constant.POINT_PER_BIT;

		}

		return ret;
	}

	/**
	 * convert some sample point bytes to a bit data.<br/>
	 * Count average level.
	 * 
	 * @param wavein
	 *            sample buffer
	 * @param offset
	 *            start from
	 * @return 1 - bit 1;<br/>
	 *         0 - bit 0;<br/>
	 *         other - error<br/>
	 */
	public static int convertBit2(byte[] wavein, int offset) {
		int ave1 = 0;
		int ave2 = 0;

		for (int i = 0; i < Constant.POINT_PER_BIT_HALF; i++) {
			ave1 += (wavein[offset + i]);
			ave2 += (wavein[offset + i + Constant.POINT_PER_BIT_HALF]);
		}

		if (ave1 - ave2 > Constant.WAVE_DIFF_SUM_LEVEL) {
			return Constant.MANCHESTER_HIGH;
		} else if (ave2 - ave1 > Constant.WAVE_DIFF_SUM_LEVEL) {
			return Constant.MANCHESTER_LOW;
		} else {
			return -1;
		}
	}

	/**
	 * convert some sample point bytes to a bit data.<br/>
	 * Use abs level
	 * 
	 * @param wavein
	 *            sample buffer
	 * @param offset
	 *            start from
	 * @return 1 - bit 1;<br/>
	 *         0 - bit 0;<br/>
	 *         other - error<br/>
	 */
	public static int convertBit(byte[] wavein, int offset,
			boolean faultTolerance) {
		int cnt1 = 0;
		int cnt2 = 0;

		for (int i = 0; i < Constant.POINT_PER_BIT_HALF; i++) {
			cnt1 += (wavein[offset + i] > 0 ? 1 : 0);
			cnt2 += (wavein[offset + i + Constant.POINT_PER_BIT_HALF] > 0 ? 1
					: 0);
		}

		if (cnt1 == Constant.POINT_PER_BIT_HALF && cnt2 == 0) {
			return Constant.MANCHESTER_HIGH;
		} else if (cnt2 == Constant.POINT_PER_BIT_HALF && cnt1 == 0) {
			return Constant.MANCHESTER_LOW;
		} else {
			if (true) {
				if (wavein[offset] > 0 && wavein[offset + 1] > 0
						&& wavein[offset + 2] < 0 && wavein[offset + 3] < 0
						&& wavein[offset + 4] < 0 && wavein[offset + 5] < 0) {
					return Constant.MANCHESTER_HIGH;
				} else if (wavein[offset] > 0 && wavein[offset + 1] > 0
						&& wavein[offset + 2] > 0 && wavein[offset + 3] > 0
						&& wavein[offset + 4] < 0 && wavein[offset + 5] < 0) {
					return Constant.MANCHESTER_HIGH;
				} else if (wavein[offset] < 0 && wavein[offset + 1] < 0
						&& wavein[offset + 2] > 0 && wavein[offset + 3] > 0
						&& wavein[offset + 4] > 0 && wavein[offset + 5] > 0) {
					return Constant.MANCHESTER_LOW;
				} else if (wavein[offset] < 0 && wavein[offset + 1] < 0
						&& wavein[offset + 2] < 0 && wavein[offset + 3] < 0
						&& wavein[offset + 4] > 0 && wavein[offset + 5] > 0) {
					return Constant.MANCHESTER_LOW;
				} else {
					return convertBit2(wavein, offset);
				}
			} else {
				return -1;
			}
		}
	}

	/**
	 * decode an entire UART data
	 * 
	 * @param data
	 *            data buffer to be decoded
	 * @param offset
	 *            start from
	 * @return >= 0 if decode succeed and the result should be returned;<br/>
	 *         < 0 if error occurred
	 */
	public static int decodeUART(byte[] data, int offset, boolean faultTolerance) {
		int offsetTmp = offset;
		int retTmp = 0;
		int ret = 0;

		retTmp = WaveDecoder.convertBit(data, offsetTmp, faultTolerance);
		if (retTmp < 0 || retTmp == 1) {
			return -101;
		}
		offsetTmp += Constant.POINT_PER_BIT;

		for (int i = 0; i < 8; i++) {
			retTmp = WaveDecoder.convertBit(data, offsetTmp, faultTolerance);
			if (retTmp < 0) {
				return -103;
			}
			offsetTmp += Constant.POINT_PER_BIT;

			ret += (retTmp << (7 - i));
		}

		retTmp = WaveDecoder.convertBit(data, offsetTmp, faultTolerance);
		if (retTmp <= 0) {
			return -102;
		}
		// offsetTmp += Constant.POINT_PER_SAMPLE;

		// System.out.println("Decode succ:" + ret);
		return ret;
	}
}
