package info.dreamingfish123.WaveTransProto.impl;

import info.dreamingfish123.WaveTransProto.WaveinAnalyzer;
import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.codec.WaveDecoder;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

public class StaticSequenceAnalyzer implements WaveinAnalyzer {

	/* wavein buffer */
	private int bufferSize = Constant.WAVEOUT_BUF_SIZE * 2;
	private byte[] buffer;
	private int start = 0;
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
	public StaticSequenceAnalyzer() {
		buffer = new byte[bufferSize];
		resetAll();
	}

	/**
	 * set a buffer size.
	 * 
	 * @param bufferSize
	 */
	public StaticSequenceAnalyzer(int bufferSize) {
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
				+ Util.getHex(buffer, start - Constant.POINT_PER_UART,
						Constant.POINT_PER_UART));
		System.out.println("Error:\n"
				+ Util.getHex(buffer, start, Constant.POINT_PER_UART));

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
		int i = start;
		while (true) {
			if (i > start + remainLen - Constant.POINT_PER_UART) {
				break;
			}
			int val = WaveDecoder.decodeUART(buffer, i, false);
			if (val == (Constant.PACKET_START_FLAG & 0xff)) { // found
				System.out.println("StartFlag found:" + i + "\t" + remainLen);
				remainLen -= (i - start + Constant.POINT_PER_UART);
				reallocBuffer(i, remainLen + Constant.POINT_PER_UART);
				// System.out.println("Head:\n"
				// + Util.getHex(buffer, 0, Constant.POINT_PER_UART));
				start = Constant.POINT_PER_UART;
				result[bytesDecoded++] = Constant.PACKET_START_FLAG;
				startPointFound = true;
				return true;
			}
			i++;
		}

		// not found
		remainLen -= (i - start);
		reallocBuffer(i, remainLen);
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
		int val = WaveDecoder.decodeUART(buffer, start, false);
		if (val >= 0) {
			System.out.println("PacketSize found:" + val);
			start += Constant.POINT_PER_UART;
			remainLen -= Constant.POINT_PER_UART;
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
		int val = WaveDecoder.decodeUART(buffer, start, true);
		if (val >= 0) {
			start += Constant.POINT_PER_UART;
			remainLen -= Constant.POINT_PER_UART;
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

}
