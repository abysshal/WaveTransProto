import info.dreamingfish123.WaveTransProto.Analyzer;
import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.FileInputStream;
import java.io.IOException;

public class AnalyzerTest2 {

	// public static final String path = "./test/waveout.wav";

	public static final String path = "./test/wavein_defy_s5570.wav";

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new Analyzer();
		FileInputStream fis = new FileInputStream(path);
		fis.skip(44);
		byte[] tmp = new byte[6000];
		while (analyzer.getPacket() == null) {
			int size = fis.read(tmp);
			if (size <= 0) {
				System.out.println("File finished..");
				break;
			}
			System.out.println("New data read:" + size);
			if (size % 2 == 1) {
				throw new Exception("read size even..");
			}
			if (!analyzer.appendBuffer(Util.resample16To8bit(tmp, 0, size))) {
				System.out.println("analyzer buffer full.");
				break;
			}
			if (analyzer.analyze()) {
				System.out.println("Analyze SUCC!");
				WTPPacket packet = analyzer.getPacket();
				System.out.println("Packet payload size:"
						+ packet.getPayload().length);
				System.out.println("Packet:\n"
						+ Util.getHex(packet.getPacketBytes()));
				analyzer.resetForNext();
				break;
			}
		}

		fis.close();
		// System.out.println("Hex:\n" + Util.getHex(tmp, 44, 120));
		// if (analyzer.appendBuffer(tmp, 44, size - 44)) {
		// System.out.println("Add Buffer SUCC!");
		// if (analyzer.analyze()) {
		// System.out.println("Analyze SUCC!");
		// WTPPacket packet = analyzer.getPacket();
		// System.out.println("Packet payload size:"
		// + packet.getPayload().length);
		// System.out.println("Packet:\n"
		// + Util.getHex(packet.getPacketBytes()));
		// analyzer.resetForNext();
		// return;
		// } else {
		// System.out.println("Analyze FAIL!");
		// }
		// } else {
		// System.out.println("Add Buffer FAIL!");
		// }
	}
}
