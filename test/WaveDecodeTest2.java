import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.WaveDecoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WaveDecodeTest2 {

	public static void main(String[] args) {
		try {
			byte[] buffer = new byte[1024];
			FileInputStream fis = new FileInputStream("./test/res/wavein_mbp_S5570_4_8bit.wav");
			FileOutputStream fos = new FileOutputStream("./test/res/wavein_mbp_S5570_4_8bit.txt");
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

	public static String getHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			if (i != 0 && i % 16 == 0) {
				sb.append("\n");
			}
			sb.append(String.format("%02X ", bytes[i]));
		}
		return sb.toString();
	}

	public static short[] byteArray2ShortArray(byte[] data) {
		short[] retVal = new short[data.length / 2];
		for (int i = 0; i < retVal.length; i++)
			retVal[i] = (short) ((data[i * 2] & 0xff) | (data[i * 2 + 1] & 0xff) << 8);
		return retVal;
	}
}
