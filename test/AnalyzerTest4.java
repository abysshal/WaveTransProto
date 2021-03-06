import info.dreamingfish123.WaveTransProto.impl.DynamicAverageAnalyzer;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AnalyzerTest4 {

	// public static final String path = "./test/data_pc_in.wav";
	// public static final String path = "./test/waveout.wav";

//	public static final String path = "./test/AC3_S5570_pro/wavein_AC3_S5570_6_pro_a_r.wav";
	public static final String path= "./test/AC3_S5570_r/wavein_AC3_S5570_r_4_g_8bit.wav";

	public static void main(String[] args) throws Exception {
		List<WTPPacket> results = new ArrayList<WTPPacket>();
		List<WTPPacket> fails = new ArrayList<WTPPacket>();
		DynamicAverageAnalyzer analyzer = new DynamicAverageAnalyzer();
		FileInputStream fis = new FileInputStream(path);
		DataInputStream dis = new DataInputStream(fis);
		byte[] header = new byte[44];
		int totalRead = 0;
		int tmpRead = 0;
		try {
			int size = fis.read(header);
			if (size < 44) {
				System.out.println("Wave file too small:" + size);
				return;
			}
			int format = header[34];
			System.out.println("AudioFormat:" + format);
			while (true) {
				if (dis.available() <= 0) {
					System.out.println("File finished..");
					break;
				}

				try {
					for (int i = 0; i < 6000; i++) {
						if (format == 16) {
							short a = dis.readShort();
							tmpRead = invertPhaseAnd16To8(a);
							totalRead++;
							if (totalRead > 0 && totalRead < 100) {
								System.out.println("read" + totalRead + "\t:"
										+ tmpRead);
							}
							analyzer.appendBuffer(tmpRead);
						} else if (format == 8) {
							tmpRead = (dis.readByte() & 0xff);
							totalRead++;
							if (totalRead > 0 && totalRead < 100) {
								System.out.println("read" + totalRead + "\t:"
										+ tmpRead);
							}
							analyzer.appendBuffer(tmpRead);
						}
					}
					System.out.println("New data added:6000");
				} catch (EOFException e) {
					System.out.println("File EOF read.");
				}

				if (analyzer.analyze()) {
					System.out.println("Analyze SUCC!");
					WTPPacket packet = analyzer.getPacket();
					if (WaveEncodeTest.compareSData(packet.getPacketBytes())) {
						results.add(packet);
					} else {
						fails.add(packet);
					}
					analyzer.resetForNext();
				}
				// break;
			}
		} finally {
			fis.close();
		}

		System.out.println("Total packet found:"
				+ (results.size() + fails.size()));
		System.out.println("Right packet found:" + results.size() + "\tr:"
				+ (results.size() * 1.0 / (results.size() + fails.size())));

		// for (WTPPacket packet : results) {
		// System.out.println("Packet payload size:"
		// + packet.getPayload().length);
		// System.out.println("Packet:\n"
		// + Util.getHex(packet.getPacketBytes()));
		// System.out.println("CompareResult:"
		// + WaveEncodeTest.compareSData(packet.getPacketBytes()));
		// }
	}

	/**
	 * invert phase & convert signed 16bit to unsigned 8bit
	 * 
	 * @param tmpRead
	 * @return
	 */
	public static int invertPhaseAnd16To8(short tmpRead) {
////		tmpRead *= (-1);
//		if (tmpRead > 0x8000) {
//			return (0x80 - (0xff - (tmpRead >> 8)));
//		} else {
//			return (0x80 + (tmpRead >> 8));
//		}
////		tmpRead *= (-1);
////		if (tmpRead > 0x8000) {
////			tmpRead = 0x80 - (0xff - (tmpRead >> 8));
////		} else {
////			tmpRead = 0x80 +  (tmpRead >> 8);
////		}
		return ((tmpRead >> 8) & 0xff);
		//		return tmpRead;
	}
}
