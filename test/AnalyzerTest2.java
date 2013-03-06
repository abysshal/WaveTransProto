import info.dreamingfish123.WaveTransProto.Analyzer;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.FileInputStream;

public class AnalyzerTest2 {

	// public static final String path = "./test/waveout.wav";

	public static final String path = "./test/AC3_S5570/wavein_AC3_S5570_1_clip1.wav";

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new Analyzer();
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
//				System.out.println("Hex:\n"+ Util.getHex(Util.resample16To8bit(tmp)));
//				if(true)System.exit(1);
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
				System.out.println("Packet payload size:"
						+ packet.getPayload().length);
				System.out.println("Packet:\n"
						+ Util.getHex(packet.getPacketBytes()));
				System.out.println("CompareResult:"
						+ WaveEncodeTest.compareSData(packet.getPacketBytes()));
				analyzer.resetForNext();
			}
		}

		fis.close();
	}
}
