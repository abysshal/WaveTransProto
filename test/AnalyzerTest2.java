import info.dreamingfish123.WaveTransProto.impl.DynamicAverageAnalyzer;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class AnalyzerTest2 {

	// public static final String path = "./test/data_pc_in.wav";
	// public static final String path = "./test/waveout.wav";

	public static final String path = "./test/AC3_S5570_r/wavein_AC3_S5570_r_4.wav";
	public static final int bufferSize = 6000;

	public static void main(String[] args) throws Exception {
		List<WTPPacket> results = new ArrayList<WTPPacket>();
		List<WTPPacket> fails = new ArrayList<WTPPacket>();
		DynamicAverageAnalyzer analyzer = new DynamicAverageAnalyzer();
		FileInputStream fis = new FileInputStream(path);
		byte[] header = new byte[44];
		byte[] tmp = new byte[bufferSize];
		int totalRead = 0;
		try {
			int size = fis.read(header, 0, 44);
			if (size < 44) {
				System.out.println("Wave file too small:" + size);
				return;
			}
			int format = header[34];
			System.out.println("AudioFormat:" + format);
			while (true) {
				size = fis.read(tmp);
				if (size <= 0) {
					System.out.println("File finished..:" + size);
					break;
				}
				System.out.println("New data read:" + size);
				totalRead += size;

				if (!analyzer.appendBuffer(tmp, 0, size, format, true)) {
					System.out.println("analyzer buffer full.");
					break;
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

	}
}
