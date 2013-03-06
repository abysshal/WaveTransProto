import info.dreamingfish123.WaveTransProto.WaveinAnalyzer;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.impl.StaticSequenceAnalyzer;
import info.dreamingfish123.WaveTransProto.impl.DynamicSequenceAnalyzer;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AnalyzerTest2 {

	// public static final String path = "./test/data_pc_in.wav";
//	public static final String path = "./test/waveout.wav";

	public static final String path = "./test/AC3_S5570/wavein_AC3_S5570_1_clip1.wav";

	public static void main(String[] args) throws Exception {
		List<WTPPacket> results = new ArrayList<WTPPacket>();
		WaveinAnalyzer analyzer = new DynamicSequenceAnalyzer();
//		WaveinAnalyzer analyzer = new StaticSequenceAnalyzer();
		FileInputStream fis = new FileInputStream(path);
		byte[] tmp = new byte[6000];
		int size = fis.read(tmp, 0, 44);
		if (size < 44) {
			System.out.println("Wave file too small:" + size);
			fis.close();
			return;
		}
		int format = tmp[34];
		System.out.println("AudioFormat:" + format);
		while (true) {
			size = fis.read(tmp);
			if (size <= 0) {
				System.out.println("File finished..:" + size);
				break;
			}
			System.out.println("New data read:" + size);

			if (format == 16) {
//				System.out.println("Hex:\n"
//						+ Util.getHex(Util.resample16To8bit(tmp)));
				if (!analyzer.appendBuffer(Util.resample16To8bit(tmp, 0, size))) {
					System.out.println("analyzer buffer full.");
					break;
				}
			} else if (format == 8) {
				if (!analyzer.appendBuffer(tmp, 0, size)) {
					System.out.println("analyzer buffer full.");
					break;
				}
			} else {
				System.out.println("Unsupported Audio Format:" + format);
				break;
			}
			if (analyzer.analyze()) {
				System.out.println("Analyze SUCC!");
				WTPPacket packet = analyzer.getPacket();
				results.add(packet);
				analyzer.resetForNext();
			}
		}

		fis.close();

		System.out.println("Total packet found:" + results.size());

		for (WTPPacket packet : results) {
			System.out.println("Packet payload size:"
					+ packet.getPayload().length);
			System.out.println("Packet:\n"
					+ Util.getHex(packet.getPacketBytes()));
			System.out.println("CompareResult:"
					+ WaveEncodeTest.compareSData(packet.getPacketBytes()));
		}
	}
}
