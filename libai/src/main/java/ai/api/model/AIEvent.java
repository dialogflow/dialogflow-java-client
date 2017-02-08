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
