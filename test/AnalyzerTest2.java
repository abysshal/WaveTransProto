import info.dreamingfish123.WaveTransProto.Analyzer;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.FileInputStream;

public class AnalyzerTest2 {

	// public static final String path = "./test/waveout.wav";

	public static final String path = "./test/res/wavein_mbp_S5570_4.wav";

	public static void main(String[] args) throws Exception {
		Analyzer analyzer = new Analyzer();
		FileInputStream fis = new FileInputStream(path);
		fis.skip(44);
		byte[] tmp = new byte[6000];
		while (true) {
			int size = fis.read(tmp);
			if (size <= 0) {
				System.out.println("File finished..:" + size);
				break;
			}
			// System.out.println("New data read:" + size);
			if (size % 2 == 1) {
				throw new Exception("read size even..");
			}
			byte[] ret = Util.resample16To8bit(tmp, 0, size);
			// System.out.println("Ori:\n" + Util.getHex(tmp) );
			// System.out.println("8bit:\n" + Util.getHex(ret));
			if (!analyzer.appendBuffer(ret)) {
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
				// break;
			}
		}

		fis.close();
	}
}
