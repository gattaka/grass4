package cz.gattserver.common.util;

import java.io.*;
import java.util.Base64;

public class StringSerializer {

	private StringSerializer() {
	}

	public static <T> T deserialize(String s) throws IOException, ClassNotFoundException {
		byte[] data = Base64.getDecoder().decode(s);
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ObjectInputStream in = new ObjectInputStream(bais);
		@SuppressWarnings("unchecked")
		T result = (T) in.readObject();
		in.close();
		return result;
	}

	public static String serialize(Serializable o) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(o); // Serialize Object
		out.close();
		return Base64.getEncoder().encodeToString(baos.toByteArray());
	}
}
