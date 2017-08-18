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
