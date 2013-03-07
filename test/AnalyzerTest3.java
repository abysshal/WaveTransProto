import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.impl.StaticSequenceAnalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;

public class AnalyzerTest3 {

	public static final String PATH = "./test/AC3_S5570_pro";

	public static void main(String[] args) throws Exception {
		batchTest(PATH);
	}

	public static void batchTest(String path) throws Exception {
		File file = new File(path);
		if (!file.exists() || !file.isDirectory()) {
			System.out.println("Path invalid:" + path);
			return;
		}
		File[] files = file.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File arg0, String arg1) {
				if (arg1.endsWith(".wav")) {
					return true;
				}
				return false;
			}
		});
		StringBuilder sb = new StringBuilder();
		sb.append("Wave Files Got:" + files.length + "\n");

		byte[] tmp = new byte[6000];
		int found = 0;

		
		for (File currFile : files) {
			found = 0;
			StaticSequenceAnalyzer analyzer = new StaticSequenceAnalyzer();
			FileInputStream fis = new FileInputStream(currFile);
			int size = fis.read(tmp, 0, 44);
			if (size < 44) {
				continue;
			}
			int format = tmp[34];
			while (true) {
				size = fis.read(tmp);
				if (size <= 0) {
					break;
				}
				if (format == 16) {
					if (!analyzer.appendBuffer(Util.resample16To8bit(tmp, 0,
							size))) {
						break;
					}
				} else if (format == 8) {
					if (!analyzer.appendBuffer(tmp, 0, size)) {
						break;
					}
				} else {
					break;
				}

				if (analyzer.analyze()) {
					found++;
					analyzer.resetForNext();
				}
			}
			fis.close();
			sb.append(currFile.getName() + "\t found:" + found + "\n");
		}
		System.out.println(sb.toString());
	}
}
