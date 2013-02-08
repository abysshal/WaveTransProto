import info.dreamingfish123.WaveTransProto.codec.Constant;
import info.dreamingfish123.WaveTransProto.codec.WaveDecoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class WaveDecodeTest {

	public static void main(String[] args) {
		try {
			byte[] wavein = new byte[Constant.MAX_TRANSFER_DATA_LEN
					* Constant.BIT_PER_BYTE * Constant.POINT_PER_SAMPLE
					+ Constant.WAVE_HEAD_LEN];
			FileInputStream fis = new FileInputStream("./test/waveout.wav");
			int len = fis.read(wavein);
			fis.close();
			len -= Constant.WAVE_HEAD_LEN;
			byte[] ret = WaveDecoder
					.decode(wavein, Constant.WAVE_HEAD_LEN, len);
			FileOutputStream fos = new FileOutputStream("./test/wavedata.txt");
			fos.write(getHex(ret).getBytes());
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
}
