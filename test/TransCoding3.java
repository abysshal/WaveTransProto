import info.dreamingfish123.WaveTransProto.codec.Util;
import java.io.FileInputStream;

public class TransCoding3 {

	public static final String path = "./test/AC3_S5570_r/wavein_AC3_S5570_r_4.wav";
	public static final int bufferSize = 6000;

	public static void main(String[] args) throws Exception {
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

				for (int i = 0; i < 100; i++) {
					System.out.println(":"
							+ (Util.readShortLittleEndian(tmp, i * 2) >> 8));
				}
				break;
			}
		} finally {
			fis.close();
		}
	}
}
