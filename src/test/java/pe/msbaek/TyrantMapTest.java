package pe.msbaek;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;


public class TyrantMapTest {

	private TyrantMap map;

	@Test
	public void get_retrives_what_was_put() throws IOException {
//		byte[] key = "key".getBytes(); // step 2.2
//		byte[] value = "value".getBytes(); // step 2.3
//
//		TyrantMap map = new TyrantMap(); // step 2.1
//		map.put(key, value);
//		assertThat(map.get(key), is(value)); // step 1

		byte[] key = {'k', 'e', 'y'};
		byte[] value = {'v', 'a', 'l', 'u', 'e'};

		map.put(key, value);
		assertThat(map.get(key), is(value));
	}

	@After
	public void tearDown() throws IOException {
		map.close();
	}

	@Before
	public void setUp() throws IOException {
		map = new TyrantMap();
		map.open();
	}
}
