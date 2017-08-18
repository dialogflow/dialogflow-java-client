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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class AIEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  @SerializedName("name")
  private String name;

  @SerializedName("data")
  private Map<String, String> data;

  public AIEvent() {

  }

  public AIEvent(final String name) {
    this.name = name;
  }

  /**
   * Event name
   */
  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  /**
   * String data map
   */
  public Map<String, String> getData() {
    return data;
  }

  public void setData(Map<String, String> data) {
    this.data = data;
  }

  public void addDataField(String key, String value) {
    if (data == null)
      setData(new HashMap<String, String>());
    data.put(key, value);
  }

  public void addDataField(Map<String, String> dataParams) {
    if (data == null)
      setData(new HashMap<String, String>());
    data.putAll(dataParams);
  }

  public String getDataField(final String name) {
    return getDataField(name, "");
  }

  public String getDataField(final String name, final String defaultValue) {
    if (data.containsKey(name)) {
      return data.get(name);
    }
    return defaultValue;
  }

}
