package info.dreamingfish123.WaveTransProto;

import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

public interface WaveinAnalyzer {

	/**
	 * should be called after one packet is found and continue to find more
	 */
	public void resetForNext();

	/**
	 * add wavein bytes to this analyzer's buffer
	 * 
	 * @param data
	 *            the wavein bytes
	 * @return true if there is enough space of the buffer to append these bytes
	 */
	public boolean appendBuffer(byte[] data);

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
	public boolean appendBuffer(byte[] data, int offset, int len);

	/**
	 * analyze from the buffer
	 * 
	 * @return true if one packet is found
	 */
	public boolean analyze();

	/**
	 * get the decoded Wave Trans Proto packet
	 * 
	 * @return the result packet
	 */
	public WTPPacket getPacket();
}
