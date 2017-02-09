package pe.msbaek;

import org.junit.Test;

import java.io.IOException;
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

		new Socket("localhost", 1978);
	}
}
