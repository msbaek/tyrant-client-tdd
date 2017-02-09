package pe.msbaek;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class TyrantMap {
	public static final int OPERATION_PREFIX = 0xC8;
	public static final int PUT_OPERATION = 0x10;
	public static final int GET_OPERATION = 0x30;
	private Socket socket;
	private DataOutputStream writer;
	private DataInputStream reader;

	public void put(byte[] key, byte[] value) throws IOException {
		writeOperation(PUT_OPERATION);
		writer.writeInt(key.length);
		writer.writeInt(value.length);
		writer.write(key);
		writer.write(value);
		validateStatus();
	}

	public byte[] get(byte[] key) throws IOException {
		writeOperation(GET_OPERATION);
		writer.writeInt(key.length);
		writer.write(key);
		validateStatus();
		return readBytes();
	}

	public void open() throws IOException {
		socket = new Socket("localhost", 1978);
		writer = new DataOutputStream(socket.getOutputStream());
		reader = new DataInputStream(socket.getInputStream());
	}

	public void close() throws IOException {
		socket.close();
	}

	private byte[] readBytes() throws IOException {
		int size = reader.readInt();
		byte[] results = new byte[size];
		reader.read(results);
		return results;
	}

	private void validateStatus() throws IOException {
		int status = reader.read();
		if(status != 0)
			throw new IllegalStateException();
	}

	private void writeOperation(int putOperation) throws IOException {
		writer.write(OPERATION_PREFIX);
		writer.write(putOperation);
	}
}
