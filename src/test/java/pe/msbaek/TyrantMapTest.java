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

		byte[] key = {'k', 'e', 'y'};
		byte[] value = {'v', 'a', 'l', 'u', 'e'};

		TyrantMap map = new TyrantMap();
		map.open();
		map.put(key, value);
		assertThat(map.get(key), is(value));
		map.close();
	}

	private class TyrantMap {
		public static final int OPERATION_PREFIX = 0xC8;
		public static final int PUT_OPERATION = 0x10;
		public static final int GET_OPERATION = 0x30;
		private Socket socket;
		private DataOutputStream writer;
		private DataInputStream reader;

		public void put(byte[] key, byte[] value) throws IOException {
			writer.write(OPERATION_PREFIX);
			writer.write(PUT_OPERATION);
			writer.writeInt(key.length);
			writer.writeInt(value.length);
			writer.write(key);
			writer.write(value);
			int status = reader.read();
			assertThat(status, is(0));
		}

		public byte[] get(byte[] key) throws IOException {
			writer.write(OPERATION_PREFIX);
			writer.write(GET_OPERATION);
			writer.writeInt(key.length);
			writer.write(key);
			int status = reader.read();
			assertThat(status, is(0));
			int size = reader.readInt();
			byte[] results = new byte[size];
			reader.read(results);
			return results;
		}

		private void open() throws IOException {
			socket = new Socket("localhost", 1978);
			writer = new DataOutputStream(socket.getOutputStream());
			reader = new DataInputStream(socket.getInputStream());
		}

		private void close() throws IOException {
			socket.close();
		}
	}
}
