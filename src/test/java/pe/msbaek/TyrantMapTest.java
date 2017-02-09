package pe.msbaek;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class TyrantMapTest {
	@Test
	public void get_retrives_what_was_put() throws IOException {
//		TyrantMap map = new TyrantMap(); // step 2.1
//		byte[] key = "key".getBytes(); // step 2.2
//		byte[] value = "value".getBytes(); // step 2.3
//		assertThat(map.get(key), is(value)); // step 1

		new TyrantMap().invoke();
	}

	private class TyrantMap {
		public void invoke() throws IOException {
			Socket s = new Socket("localhost", 1978);
			OutputStream writer = s.getOutputStream();
			writer.write(0xC8); // operation prefix
			writer.write(0x10); // put operation
			writer.write(0);
			writer.write(0);
			writer.write(0);
			writer.write(3); // 4 byte
			writer.write(0);
			writer.write(0);
			writer.write(0);
			writer.write(5); // 4 byte
			writer.write(new byte [] {'k', 'e', 'y'}); // key
			writer.write(new byte [] {'v', 'a', 'l', 'u', 'e'}); // value
			InputStream reader = s.getInputStream();
			int status = reader.read();
			assertThat(status, is(0));
		}
	}
}
