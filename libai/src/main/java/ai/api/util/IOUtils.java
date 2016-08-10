package ai.api.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;

/**
 * Small subset of IO helping functions
 */
public class IOUtils {

	private static final int DEFAULT_BUFFER_SIZE = 4096;
	
	/**
	 * Write string to byte stream
	 * @param data Source string
	 * @param outputStream Output stream
	 * @param charset Convert string to bytes according to given {@link Charset}
	 * @throws IOException
	 */
	public static void writeAll(String data, OutputStream outputStream, Charset charset)
		throws IOException 
	{
		if ((data != null) && (data.length() > 0)) {
			outputStream.write(data.getBytes(charset));
		}
	}

	/**
	 * Read all stream byte data into {@link String}
	 * @param inputStream Source stream
	 * @param charset Convert bytes to chars according to given {@link Charset}
	 * @return Empty {@link String} if there was no data in stream
	 * @throws IOException
	 */
	public  static String readAll(InputStream inputStream, Charset charset) throws IOException {
		try (InputStreamReader streamReader = new InputStreamReader(inputStream, charset)) {
			return readAll(streamReader);
		}
	}
	
	/**
	 * Read all chars into String
	 * @param streamReader Input stream reader
	 * @return Empty {@link String} if there was no data in stream
	 * @throws IOException
	 */
	public static String readAll(InputStreamReader streamReader) throws IOException {
		StringWriter result = new StringWriter();
		copy(streamReader, result);
		return result.toString();
	}
	
	private static long copy(Reader reader, Writer writer) throws IOException {
		return copy(reader, writer, new char[DEFAULT_BUFFER_SIZE]);
	}

	private static long copy(Reader reader, Writer writer, char[] buffer)
		throws IOException
	{
		assert buffer != null;
		assert buffer.length > 0;
		
		long result = 0;
		
		int read = reader.read(buffer);
		
		while (read > 0) {
			writer.write(buffer, 0, read);
			result += read;
			read = reader.read(buffer);
		}
		
		return result;
	}

}
