package pe.msbaek;

import org.junit.Test;

import java.io.*;
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

		new TyrantMap().put(new byte[]{'k', 'e', 'y'}, new byte[]{'v', 'a', 'l', 'u', 'e'});
	}

	private class TyrantMap {
		public static final int OPERATION_PREFIX = 0xC8;
		public static final int PUT_OPERATION = 0x10;

		public void put(byte[] key, byte[] value) throws IOException {
			Socket s = new Socket("localhost", 1978);
			DataOutputStream writer = new DataOutputStream(s.getOutputStream());
			writer.write(OPERATION_PREFIX);
			writer.write(PUT_OPERATION);
			writer.writeInt(key.length);
			writer.writeInt(value.length);
			writer.write(key);
			writer.write(value);
			DataInputStream reader = new DataInputStream(s.getInputStream());
			int status = reader.read();
			assertThat(status, is(0));
		}
	}
}
