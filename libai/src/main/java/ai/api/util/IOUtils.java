/**
 * Copyright 2017 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
 
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
  private static final String DEFAULT_CHARSET = "UTF-8";

  /**
   * Write string to byte stream
   * 
   * @param data Source string
   * @param outputStream Output stream
   * @param charset Convert string to bytes according to given {@link Charset}
   * @throws IOException
   */
  public static void writeAll(String data, OutputStream outputStream, Charset charset)
      throws IOException {
    writeAll(data, outputStream, charset.name());
  }

  /**
   * Write string to byte stream
   * 
   * @param data Source string
   * @param outputStream Output stream
   * @param charset Convert string to bytes according to given
   * @throws IOException
   */
  public static void writeAll(String data, OutputStream outputStream, String charset)
      throws IOException {
    if ((data != null) && (data.length() > 0)) {
      outputStream.write(data.getBytes(charset));
    }
  }

  /**
   * Write string to byte stream
   * 
   * @param data Source string
   * @param outputStream Output stream
   * @throws IOException
   */
  public static void writeAll(String data, OutputStream outputStream) throws IOException {
    writeAll(data, outputStream, DEFAULT_CHARSET);
  }

  /**
   * Read all stream byte data into {@link String}
   * 
   * @param inputStream Source stream
   * @param charset Convert bytes to chars according to given {@link Charset}
   * @return Empty {@link String} if there was no data in stream
   * @throws IOException
   */
  public static String readAll(InputStream inputStream, Charset charset) throws IOException {
    return readAll(inputStream, charset.name());
  }

  /**
   * Read all stream byte data into {@link String}
   * 
   * @param inputStream Source stream
   * @param charset Convert bytes to chars according to given
   * @return Empty {@link String} if there was no data in stream
   * @throws IOException
   */
  public static String readAll(InputStream inputStream, String charset) throws IOException {
    try (InputStreamReader streamReader = new InputStreamReader(inputStream, charset)) {
      return readAll(streamReader);
    }
  }

  /**
   * Read all stream byte data into {@link String}
   * 
   * @param inputStream Source stream
   * @return Empty {@link String} if there was no data in stream
   * @throws IOException
   */
  public static String readAll(InputStream inputStream) throws IOException {
    return readAll(inputStream, DEFAULT_CHARSET);
  }

  /**
   * Read all chars into String
   * 
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

  private static long copy(Reader reader, Writer writer, char[] buffer) throws IOException {
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
