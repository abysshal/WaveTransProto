package info.dreamingfish123.WaveTransProto.packet;

import info.dreamingfish123.WaveTransProto.codec.Constant;

public class WTPPacket {
	private byte startFlag = Constant.PACKET_START_FLAG;
	private byte[] payload = null;

	public WTPPacket() {
	}

	/**
	 * with a certain payload
	 */
	public WTPPacket(byte[] payload) {
		this.setPayload(payload);
	}

	/**
	 * decode to a WTPPacket from bytes
	 * 
	 * @param bytes
	 *            the raw data of a packet
	 * @return the decode result
	 */
	public static WTPPacket decodePacketBytes(byte[] bytes) {
		if (bytes == null) {
			return null;
		}
		if (bytes.length < 2) {
			return null;
		}
		if (bytes.length == 2) {
			return new WTPPacket();
		}
		int size = (bytes[1] & 0xff) - 2;
		byte[] payload = new byte[size];
		System.arraycopy(bytes, 2, payload, 0, size);
		WTPPacket ret = new WTPPacket(payload);
		ret.setStartFlag(bytes[0]);
		return ret;
	}

	/**
	 * get the raw data bytes of entire packet
	 * 
	 * @return the raw data bytes
	 */
	public byte[] getPacketBytes() {
		if (payload == null) {
			byte[] ret = new byte[2];
			ret[0] = startFlag;
			ret[1] = 2;
			return ret;
		} else {
			byte[] ret = new byte[payload.length + 2];
			ret[0] = startFlag;
			ret[1] = (byte) ((payload.length + 2) & 0xff);
			System.arraycopy(payload, 0, ret, 2, payload.length);
			return ret;
		}
	}

	public byte[] getPayload() {
		return payload;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public byte getStartFlag() {
		return startFlag;
	}

	public void setStartFlag(byte startFlag) {
		this.startFlag = startFlag;
	}

}
