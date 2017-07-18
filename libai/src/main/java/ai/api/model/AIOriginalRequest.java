package ai.api.model;

import java.util.Map;

public class AIOriginalRequest {

  private String source;
  private Map<String, Object> data;

  /**
   * @return the request source name (Facebook Messenger, Slack, etc.)
   */
  public final String getSource() {
    return source;
  }

  /**
   * Set the request source name (Facebook Messenger, Slack, etc.)
   * @param source 
   */
  public final void setSource(String source) {
    this.source = source;
  }

  /**
   * Get map of additional data values
   */
  public final Map<String,? extends Object> getData() {
    return data;
  }

  /**
   * Set map of additional data values
   * @param data
   */
  public final void setData(Map<String, ? extends Object> data) {
    this.data = (Map<String,Object>)data;
  }
}
