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
