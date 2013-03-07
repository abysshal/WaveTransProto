package info.dreamingfish123.WaveTransProto.impl;

import info.dreamingfish123.WaveTransProto.WaveinAnalyzer;
import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

public class DynamicSequenceAnalyzer implements WaveinAnalyzer {

	/* wavein buffer */
	private int bufferSize = Constant.WAVEOUT_BUF_SIZE * 2;
	private byte[] buffer;
	private int start = 0;
	private int lastStart = 0;
	private int lastRemainLen = 0;
	private int remainLen = 0;
	private byte[] result = new byte[Constant.MAX_TRANSFER_DATA_LEN];

	/* result data packet */
	private WTPPacket packet = null;

	/* analyze process control */
	private boolean startPointFound = false;
	private boolean packetSizeFound = false;
	private boolean packetFound = false;
	private int packetSize = 0;
	private int bytesDecoded = 0;

	/**
	 * with default buffer size.
	 */
	public DynamicSequenceAnalyzer() {
		buffer = new byte[bufferSize];
		resetAll();
	}

	/**
	 * set a buffer size.
	 * 
	 * @param bufferSize
	 */
	public DynamicSequenceAnalyzer(int bufferSize) {
		this.bufferSize = bufferSize;
		buffer = new byte[bufferSize];
		resetAll();
	}

	/**
	 * should be called after one packet is found and continue to find more
	 */
	public void resetForNext() {
		System.out.println("ResetForNext remain:" + remainLen);
		int len = remainLen;
		reallocBuffer(start, remainLen);
		resetAll();
		remainLen = len;
	}

	/**
	 * add wavein bytes to this analyzer's buffer
	 * 
	 * @param data
	 *            the wavein bytes
	 * @return true if there is enough space of the buffer to append these bytes
	 */
	public boolean appendBuffer(byte[] data) {
		return appendBuffer(data, 0, data.length);
	}

	/**
	 * add wavein bytes to this analyzer's buffer
	 * 
	 * @param data
	 *            the wavein bytes
	 * @param offset
	 *            copy from
	 * @param len
	 *            how much to copy
	 * @return true if there is enough space of the buffer to append these bytes
	 */
	public boolean appendBuffer(byte[] data, int offset, int len) {
		if (start + remainLen + len > bufferSize) {
			return false;
		}
		System.arraycopy(data, offset, buffer, start + remainLen, len);
		remainLen += len;
		return true;
	}

	/**
	 * analyze from the buffer
	 * 
	 * @return true if one packet is found
	 */
	public boolean analyze() {
		while (isBufferAnalysis() && !packetFound) {
			if (!startPointFound) { // 1. to find the start flag
				if (!locateDataHead()) {
					continue;
				}
			}
			if (!packetSizeFound) {// 2. to find the packet size
				if (!getPacketSize()) {
					continue;
				}
			}
			while (!packetFound) { // 3. decode UART
				if (!getPacketData()) {
					break;
				}
			}
		}
		return packetFound;
	}

	/**
	 * get the decoded Wave Trans Proto packet
	 * 
	 * @return the result packet
	 */
	public WTPPacket getPacket() {
		return this.packet;
	}

	/**
	 * check if there is enough bytes in the buffer to be analyzed
	 * 
	 * @return true if there is enough bytes
	 */
	private boolean isBufferAnalysis() {
		return (remainLen >= Constant.POINT_PER_UART);
	}

	/**
	 * move the remain bytes in the buffer to the offset 0
	 * 
	 * @param offset
	 *            move from
	 * @param len
	 *            how much to move
	 */
	private void reallocBuffer(int offset, int len) {
		int j = 0;
		for (int i = offset; i < offset + len; i++) {
			buffer[j++] = buffer[i];
		}
	}

	/**
	 * reset all params to its defaults
	 */
	private void resetAll() {
		start = 0;
		remainLen = 0;
		startPointFound = false;
		packetSizeFound = false;
		packetFound = false;
		bytesDecoded = 0;
		packetSize = 0;
		packet = null;
	}

	/**
	 * called when error occurred while decoding, to reset params for a new
	 * analysis
	 * 
	 * @param remainLen
	 *            how much bytes remained to be moved
	 */
	private void resetOnDecodeError() {
		System.out.println("Error occurred, decoded bytes:" + bytesDecoded);
		// if (packetSize == 200)
		// System.out.println("ErrorAll:\n"
		// + Util.getHex(buffer, 0, start + remainLen));
		System.out.println("ErrorBefor:\n"
				+ Util.getHex(buffer,
						lastStart - Constant.POINT_PER_UART > 0 ? lastStart
								- Constant.POINT_PER_UART : 0,
						Constant.POINT_PER_UART * 2));
		System.out.println("Error:\n"
				+ Util.getHex(buffer, lastStart, Constant.POINT_PER_UART));

		int len = start + remainLen;
		resetAll();
		remainLen = len > 0 ? len - 1 : 0;
		start = remainLen > 0 ? 1 : 0;
	}

	/**
	 * check if a packet has been found
	 */
	private void finishPacket() {
		if (bytesDecoded == packetSize) { // finished
			System.out.println("EntirePacket found:" + bytesDecoded);
			packetFound = true;
			packet = WTPPacket.decodePacketBytes(result);
		}
	}

	/**
	 * to find out the start flag from the buffer
	 * 
	 * @return true if found
	 */
	private boolean locateDataHead() {
		while (true) {
			if (remainLen < Constant.POINT_PER_UART) {
				break;
			}
			int val = decodeUART();
			if (val == (Constant.PACKET_START_FLAG & 0xff)) { // found
				System.out.println("StartFlag found:" + lastStart + "\t"
						+ remainLen);
				System.out.println("start&last start:" + start + "\t"
						+ lastStart);
				reallocBuffer(lastStart, lastRemainLen);
				// System.out.println("Head:\n"
				// + Util.getHex(buffer, 0, Constant.POINT_PER_UART));
				start = (start - lastStart);
				result[bytesDecoded++] = Constant.PACKET_START_FLAG;
				startPointFound = true;
				return true;
			} else {
				start = lastStart + 1;
				remainLen = lastRemainLen - 1;
			}
		}

		// not found
		reallocBuffer(start, remainLen);
		start = 0;
		return false;
	}

	/**
	 * to found out the packet size from the buffer
	 * 
	 * @return true if found
	 */
	private boolean getPacketSize() {
		if (remainLen < Constant.POINT_PER_UART) {
			return false;
		}
		int val = decodeUART();
		if (val >= 0) {
			System.out.println("PacketSize found:" + val);
			packetSize = val;
			result[bytesDecoded++] = (byte) (val & 0xff);
			packetSizeFound = true;
			finishPacket();
			return true;
		} else {
			resetOnDecodeError();
		}
		return false;
	}

	/**
	 * to find out the packet data from the buffer
	 * 
	 * @return true if found
	 */
	private boolean getPacketData() {
		if (remainLen < Constant.POINT_PER_UART) {
			return false;
		}
		int val = decodeUART();
		if (val >= 0) {
			result[bytesDecoded++] = (byte) (val & 0xff);
			finishPacket();
			return true;
		} else {
			if (val == -101 || val == -102) {
				System.out.println("UART start error:" + val);
			}
			resetOnDecodeError();
		}
		return false;
	}

	/**
	 * convert some sample point bytes to a bit data.<br/>
	 * Use abs level
	 * 
	 * @return 1 - bit 1;<br/>
	 *         0 - bit 0;<br/>
	 *         other - error<br/>
	 */
	private int convertBit2() {
		boolean is10 = buffer[start] > 0;
		boolean isTurned = false;
		int cnt1 = 1;
		int cnt2 = 0;

		for (int i = start + 1; i < Constant.POINT_PER_BIT + start; i++) {
			if (is10 && !isTurned) {
				if (buffer[i] > 0) {
					cnt1++;
				} else {
					isTurned = true;
					cnt2++;
				}
			} else if (!is10 && !isTurned) {
				if (buffer[i] > 0) {
					isTurned = true;
					cnt2++;
				} else {
					cnt1++;
				}
			} else if (is10 && isTurned) {
				if (buffer[i] > 0) {
					break;
				} else {
					cnt2++;
				}
			} else if (!is10 && isTurned) {
				if (buffer[i] > 0) {
					cnt2++;
				} else {
					break;
				}
			}
		}
		if (!isTurned) {
			return -201;
		}

		if (cnt1 == Constant.POINT_PER_BIT_HALF
				&& cnt2 == Constant.POINT_PER_BIT_HALF) {
			start += Constant.POINT_PER_BIT;
			remainLen -= Constant.POINT_PER_BIT;
			return (is10 ? Constant.MANCHESTER_HIGH : Constant.MANCHESTER_LOW);
		}

		if (cnt1 == Constant.POINT_PER_BIT_HALF - 1
				&& cnt2 >= Constant.POINT_PER_BIT_HALF) {
			start += (cnt1 + cnt2);
			remainLen -= (cnt1 + cnt2);
			return (is10 ? Constant.MANCHESTER_HIGH : Constant.MANCHESTER_LOW);
		}

		if (cnt1 >= Constant.POINT_PER_BIT_HALF
				&& cnt2 == Constant.POINT_PER_BIT_HALF - 1) {
			start += (cnt1 + cnt2);
			remainLen -= (cnt1 + cnt2);
			return (is10 ? Constant.MANCHESTER_HIGH : Constant.MANCHESTER_LOW);

		}

		return -1;
	}

	/**
	 * convert some sample point bytes to a bit data.<br/>
	 * Use abs level
	 * 
	 * @return 1 - bit 1;<br/>
	 *         0 - bit 0;<br/>
	 *         other - error<br/>
	 */
	private int convertBit() {
		boolean is10 = buffer[start] > 0;
		boolean isTurned = false;
		int ave1 = 0;
		int ave2 = 0;

		for (int i = 0; i < Constant.POINT_PER_BIT_HALF; i++) {
			ave1 += buffer[start + i];
			ave2 += buffer[start + i + Constant.POINT_PER_BIT_HALF];
		}
		
		if(ave1 > 0 && ave2 <0) {
			if(buffer[start+2] < 0 && buffer[start+3] <0) {
				start+=5;
				remainLen -=5;
			}else if(buffer[start+2] > 0 && buffer[start+3] >0) {
				start += 7;
				remainLen -= 7;
			} else {
				start+=6;
				remainLen-=6;
			}
		}else if(ave1 < 0 && ave2 > 0) {
			
		}else {
			return -201;
		}

		for (int i = start + 1; i < Constant.POINT_PER_BIT + start; i++) {
			if (is10 && !isTurned) {
				if (buffer[i] > 0) {
					cnt1++;
				} else {
					isTurned = true;
					cnt2++;
				}
			} else if (!is10 && !isTurned) {
				if (buffer[i] > 0) {
					isTurned = true;
					cnt2++;
				} else {
					cnt1++;
				}
			} else if (is10 && isTurned) {
				if (buffer[i] > 0) {
					break;
				} else {
					cnt2++;
				}
			} else if (!is10 && isTurned) {
				if (buffer[i] > 0) {
					cnt2++;
				} else {
					break;
				}
			}
		}
		if (!isTurned) {
			return -201;
		}

		if (cnt1 == Constant.POINT_PER_BIT_HALF
				&& cnt2 == Constant.POINT_PER_BIT_HALF) {
			start += Constant.POINT_PER_BIT;
			remainLen -= Constant.POINT_PER_BIT;
			return (is10 ? Constant.MANCHESTER_HIGH : Constant.MANCHESTER_LOW);
		}

		if (cnt1 == Constant.POINT_PER_BIT_HALF - 1
				&& cnt2 >= Constant.POINT_PER_BIT_HALF) {
			start += (cnt1 + cnt2);
			remainLen -= (cnt1 + cnt2);
			return (is10 ? Constant.MANCHESTER_HIGH : Constant.MANCHESTER_LOW);
		}

		if (cnt1 >= Constant.POINT_PER_BIT_HALF
				&& cnt2 == Constant.POINT_PER_BIT_HALF - 1) {
			start += (cnt1 + cnt2);
			remainLen -= (cnt1 + cnt2);
			return (is10 ? Constant.MANCHESTER_HIGH : Constant.MANCHESTER_LOW);

		}

		return -1;
	}

	/**
	 * decode an entire UART data
	 * 
	 * @return >= 0 if decode succeed and the result should be returned;<br/>
	 *         < 0 if error occurred
	 */
	private int decodeUART() {
		int retTmp = 0;
		int ret = 0;

		lastStart = start;
		lastRemainLen = remainLen;

		retTmp = convertBit();
		if (retTmp < 0 || retTmp == 1) {
			return -101;
		}

		for (int i = 0; i < 8; i++) {
			retTmp = convertBit();
			if (retTmp < 0) {
				return -103;
			}

			ret += (retTmp << (7 - i));
		}

		retTmp = convertBit();
		if (retTmp <= 0) {
			return -102;
		}

		// System.out.println("Decode succ:" + ret);
		return ret;
	}

}
