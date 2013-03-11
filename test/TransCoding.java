import java.io.DataInputStream;
import java.io.FileInputStream;

public class TransCoding {

	public final static String FILENAME = "./test/AC3_S5570_pro/wavein_AC3_S5570_5_pro_clip1";
	public final static String INPUT_SUFFIX = ".wav";
	public final static String OUTPUT_SUFFIX = "_tc.wav";

	public static void main(String[] args) throws Exception {
		byte[] header = new byte[44];
		byte[] buffer = new byte[6000];
		FileInputStream fis = new FileInputStream(FILENAME + INPUT_SUFFIX);

		try {
			int read = fis.read(header);
			int format = header[34];
			int avaliable = fis.available();
			System.out.println("Bytes avaliable:" + avaliable);
			// read = fis.read(buffer);
			DataInputStream dis = new DataInputStream(fis);

			System.out.println("AudioFormat:" + format);

			if (format == 16) {
				// dis.skip(2600);
				char[] tmp = new char[100];
				for (int i = 0; i < 100; i++) {
					tmp[i] = (char) (dis.readUnsignedShort() / 256);
					System.out.println(i + ":" + tmp[i]);
				}
			} else if (format == 8) {
				dis.skip(160);
				for (int i = 0; i < 100; i++) {
					System.out.println(i + ":" + (dis.readUnsignedByte()));
				}
			} else {
				System.out.println("unsupport audio format.");
			}
		} finally {
			fis.close();
		}
	}

}
