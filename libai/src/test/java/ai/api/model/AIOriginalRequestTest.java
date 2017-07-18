package ai.api.model;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;

import ai.api.GsonFactory;

public class AIOriginalRequestTest {
  
  final static Gson gson = GsonFactory.getDefaultFactory().getGson();

  @Test
  public void test() {
    AIOriginalRequest originalRequest = new AIOriginalRequest();
    assertEquals("{}", gson.toJson(originalRequest));

    originalRequest.setSource("sourceValue");
    Map<String, Object> data = new HashMap<>();
    data.put("key1", 1);
    data.put("key2", "value2");
    originalRequest.setData(data);
    assertEquals("{\"source\":\"sourceValue\",\"data\":{\"key1\":1,\"key2\":\"value2\"}}", gson.toJson(originalRequest));

    Map<String,String> data2 = new HashMap<>();
    originalRequest.setData(data2);
    assertEquals("{\"source\":\"sourceValue\",\"data\":{}}", gson.toJson(originalRequest));
  }
}
