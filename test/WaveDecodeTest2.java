import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.Util;
import info.dreamingfish123.WaveTransProto.codec.WaveDecoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WaveDecodeTest2 {

	public static final String PATH_NAME = "./test/AC3_S5570/wavein_AC3_S5570_1_clip1";

	public static void main(String[] args) {
		resampleAndToHex();
	}

	public static void toBin() {
		try {
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(PATH_NAME + ".wav");
			FileOutputStream fos = new FileOutputStream(PATH_NAME + ".txt");
			int read = 0;
			while ((read = fis.read(buffer)) > 0) {
				// short[] data = byteArray2ShortArray(buffer);
				// for (int i = 0; i < data.length; i++) {
				// fos.write(data[i] > 0 ? 0x31 : 0x30);
				// }
				for (int i = 0; i < buffer.length; i++) {
					fos.write(buffer[i] > 0 ? 0x31 : 0x30);
				}
			}
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void resampleAndToHex() {
		try {
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream(PATH_NAME + ".wav");
			FileOutputStream fos = new FileOutputStream(PATH_NAME + "_8bit.wav");
			int read = fis.read(buffer, 0, 44);
			buffer[34] = 8;
			Util.int2byte(((int) fis.getChannel().size() - 44) / 2, buffer,
					Constant.WAVE_DATA_LEN_OFFSET);
			Util.int2byte(((int) fis.getChannel().size() - 44) / 2
					+ Constant.WAVE_HEAD_LEN - 8, buffer,
					Constant.WAVE_FILE_LEN_OFFSET);
			fos.write(buffer, 0, 44);
			while ((read = fis.read(buffer)) > 0) {
				fos.write(Util.resample16To8bit(buffer, 0, read));
			}
			fis.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
