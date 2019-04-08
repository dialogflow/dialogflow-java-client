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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

public class AIEvent implements Serializable {
  private static final long serialVersionUID = 1L;

  @SerializedName("name")
  private String name;

  @SerializedName("data")
  private Map<String, JsonElement> data;

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
  public Map<String, JsonElement> getData() {
    return data;
  }

  public void setData(Map<String, JsonElement> data) {
    this.data = data;
  }

  public void addDataField(String key, String value) {
    if (data == null)
      setData(new HashMap<String, JsonElement>());
    data.put(key, new Gson().toJsonTree(value));
  }

  public void addDataField(Map<String, String> dataParams) {
    if (data == null)
      setData(new HashMap<String, JsonElement>());
    
    if (dataParams != null) {
        for (Map.Entry<String, String> entry : dataParams.entrySet()) {
          this.data.put(entry.getKey(), new Gson().toJsonTree(entry.getValue()));
        }
    }
  }
  
  public void addDataCollection(String key, Collection<String> collection) {
	  if (data == null) 
	      setData(new HashMap<String, JsonElement>());
	  
	  data.put(key, new Gson().toJsonTree(collection));
  }

  public String getDataField(final String name) {
    return getDataField(name, "");
  }

  public String getDataField(final String name, final String defaultValue) {
    if (data.containsKey(name) && data.get(name) != null) {
    		if(data.get(name).isJsonArray()) {
    			return data.get(name).toString();
    		} else {
    			return data.get(name).getAsString();
    		} 
      
    }
    return defaultValue;
  }

}
