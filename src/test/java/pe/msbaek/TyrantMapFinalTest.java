package pe.msbaek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Iterator;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class TyrantMapFinalTest {
private TyrantMapFinal tyrantMap;
	private final byte[] key = new byte[]{'k', 'e', 'y'};
	private final byte[] value = new byte[]{'v', 'a', 'l', 'u', 'e'};

	@Test
	public void get_retrives_what_was_put() throws IOException {
		tyrantMap.put(key, value);
		assertThat(tyrantMap.get(key), is(value));
	}

	@Test
	public void get_returns_null_if_key_not_found() throws IOException {
		assertThat(tyrantMap.get(key), is(nullValue()));
	}

	@Test
	public void clear_deletes_all_values() throws IOException {
		tyrantMap.put(key, value);
		tyrantMap.clear();
		assertThat(tyrantMap.get(key), is(nullValue()));
	}

	@Test
	public void remove_removes_specified_element() throws IOException {
		tyrantMap.put(key, value);
		tyrantMap.remove(key);
		assertThat(tyrantMap.get(key), is(nullValue()));
	}

	@Test
	public void remove_missing_key_does_nothing() throws IOException {
		tyrantMap.remove(key);
	}

	@Test
	public void empty_map_size_should_be_zero() throws IOException {
		assertThat(tyrantMap.size(), is(0l));
	}

	@Test
	public void one_element_map_size_should_be_one() throws IOException {
		tyrantMap.put(key, value);
		assertThat(tyrantMap.size(), is(1l));
	}

	@Test
	public void iterate_over_an_empty_map() {
		for(byte [] ignored : tyrantMap)
			fail();
	}

	@Test
	public void iterate_over_an_two_element_map() throws IOException {
		int counter = 0;
		tyrantMap.put(key, value);
		tyrantMap.put("key2".getBytes(), value);
		for(byte [] ignored : tyrantMap) {
			assertThat(ignored, is(value));
			counter++;
		}

		assertThat(counter , is(2));
	}

	@Test
	public void remove_one_element() throws IOException {
		tyrantMap.put(key, value);
		Iterator<byte[]> all = tyrantMap.iterator();
		all.next();
		all.remove();
		assertThat(tyrantMap.get(key), is(nullValue()));
	}

	@Test(expected = IllegalStateException.class)
	public void remove_before_next() throws IOException {
		tyrantMap.put(key, value);
		Iterator<byte[]> all = tyrantMap.iterator();
		all.remove();
		assertThat(tyrantMap.get(key), is(nullValue()));
	}

	@After
	public void tearDown() throws IOException {
		tyrantMap.clear();
		tyrantMap.close();
	}

	@Before
	public void setUp() throws IOException {
		tyrantMap = new TyrantMapFinal();
		tyrantMap.open();
	}

	private class TyrantMapFinal implements Iterable<byte []> {
		private static final int OPERATION_PREFIX = 0xC8;
		private static final int PUT_OPERATION = 0x10;
		private static final int GET_OPERATION = 0x30;
		private static final int VANISH_OPERATION = 0x72;
		private static final int REMOVE_OPERATION = 0x20;
		private static final int SIZE_OPERATION = 0x80;
		private static final int RESET_OPERATION = 0x50;
		private static final int GET_NEXT_KEY_OPERATION = 0x51;

		private static final int NOT_FOUND = 1;
		private static final int SUCCESS = 0;
		private Socket socket;
		private DataOutputStream writer;
		private DataInputStream reader;

		public void put(byte[] key, byte[] value) throws IOException {
			writeOperation(PUT_OPERATION);
			writer.writeInt(key.length);
			writer.writeInt(value.length);
			writer.write(key);
			writer.write(value);
			int status = reader.read();
			if (status != 0)
				throw new IllegalStateException("status[" + status + "] is not 0");
		}

		public void open() throws IOException {
			socket = new Socket("localhost", 1978);
			writer = new DataOutputStream(socket.getOutputStream());
			reader = new DataInputStream(socket.getInputStream());
		}

		public void close() throws IOException {
			socket.close();
		}

		public byte[] get(byte[] key) {
			try {
				writeOperation(GET_OPERATION);
				writer.writeInt(key.length);
				writer.write(key);
				return readBytes();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		public void clear() throws IOException {
			writeOperation(VANISH_OPERATION);
			int status = reader.read();
			if (status != 0)
				throw new IllegalStateException("status[" + status + "] is not 0");
		}

		public void remove(byte[] key) throws IOException {
			if(key == null)
				throw new IllegalArgumentException();
			writeOperation(REMOVE_OPERATION);
			writer.writeInt(key.length);
			writer.write(key);
			int status = reader.read();
			if(status == NOT_FOUND)
				return;
			if(status != SUCCESS)
				throw new IllegalStateException("status[" + status + "] is not 0");
		}

		public long size() throws IOException {
			writeOperation(SIZE_OPERATION);
			int status = reader.read();
			if(status != SUCCESS)
				throw new IllegalStateException("status[" + status + "] is not 0");
			return reader.readLong();
		}

		public Iterator<byte[]> iterator() {
			try {
				reset();
				final byte[] firstKey = getNextKey();
				return new Iterator<byte[]>() {
					public byte[] previousKey;
					byte [] nextKey = firstKey;
					public boolean hasNext() {
						return nextKey != null;
					}

					public byte[] next() {
						byte[] results = get(nextKey);
						previousKey = nextKey;
						nextKey = getNextKey();
						return results;
					}

					public void remove() {
						try {
							TyrantMapFinal.this.remove(previousKey);
						} catch (IllegalArgumentException e) {
							throw new IllegalStateException(e);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private void reset() throws IOException {
			writeOperation(RESET_OPERATION);
			int status = reader.read();
			if (status != 0)
				throw new IllegalStateException("status[" + status + "] is not 0");
		}

		private void writeOperation(int operationCode) throws IOException {
			writer.write(OPERATION_PREFIX);
			writer.write(operationCode);
		}

		private byte[] getNextKey() {
			try {
				writeOperation(GET_NEXT_KEY_OPERATION);
				return readBytes();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		private byte[] readBytes() throws IOException {
			int status = reader.read();
			if (status == NOT_FOUND)
				return null;
			else if(status != SUCCESS)
				throw new IllegalStateException("status[" + status + "] is not 0");
			int length = reader.readInt();
			byte [] results = new byte [length];
			reader.read(results);
			return results;
		}
	}
}
