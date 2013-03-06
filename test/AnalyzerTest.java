import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.impl.StaticSequenceAnalyzer;
import info.dreamingfish123.WaveTransProto.packet.WTPPacket;

import java.io.FileInputStream;
import java.io.IOException;

public class AnalyzerTest {

	public static final String path = "./test/waveout.wav";

	// public static final String path = "./test/wavein_defy_s5570.wav";

	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		byte[] tmp = new byte[Constant.WAVEOUT_BUF_SIZE];
		int size = fis.read(tmp);
		fis.close();
		StaticSequenceAnalyzer analyzer = new StaticSequenceAnalyzer();
		System.out.println("Hex:\n" + Util.getHex(tmp, 44, 120));
		if (analyzer.appendBuffer(tmp, 44, size - 44)) {
			System.out.println("Add Buffer SUCC!");
			if (analyzer.analyze()) {
				System.out.println("Analyze SUCC!");
				WTPPacket packet = analyzer.getPacket();
				System.out.println("Packet payload size:"
						+ packet.getPayload().length);
				System.out.println("Packet:\n"
						+ Util.getHex(packet.getPacketBytes()));
				analyzer.resetForNext();
				return;
			} else {
				System.out.println("Analyze FAIL!");
			}
		} else {
			System.out.println("Add Buffer FAIL!");
		}
	}
}
