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

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import ai.api.util.StringUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Model class for <a href="https://docs.api.ai/docs/webhook#section-format-of-response-from-the-service"
 * >webhook response</a>. 
 */
public class Fulfillment implements Serializable {
  private static final long serialVersionUID = 1L;

  @SerializedName("speech")
  private String speech;

  @SerializedName("messages")
  private List<ResponseMessage> messages;

  @SerializedName("displayText")
  private String displayText;

  @SerializedName("data")
  private Map<String, JsonElement> data;

  @SerializedName("source")
  private String source;

  @SerializedName("contextOut")
  private List<AIOutputContext> contextOut;

  @SerializedName("followupEvent")
  private AIEvent followupEvent;

  /** Get voice response to the request */
  public String getSpeech() {
    return speech;
  }

  /** Set voice response to the request */
  public void setSpeech(final String speech) {
    this.speech = speech;
  }

  /** Get list of {@link ResponseMessage} objects */
  public List<ResponseMessage> getMessages() {
    return messages;
  }

  /** Set list of {@link ResponseMessage} objects */
  public void setMessages(List<ResponseMessage> messages) {
    this.messages = messages;
  }
  
  /** Set sequence of {@link ResponseMessage} objects */
  public void setMessages(ResponseMessage... messages) {
    setMessages(Arrays.asList(messages));
  }

  /**
   * @deprecated this method name is a typo, use <code>setMessages</code> method instead
   */
  @Deprecated
  public void getMessages(List<ResponseMessage> messages) {
    // TODO remove this method after major version change
    setMessages(messages);
  }

  /** Get additional data required for performing the action on the client side. */
  public Map<String, JsonElement> getData() {
    return data;
  }

  /** Set additional data required for performing the action on the client side. */
  public void setData(final Map<String, JsonElement> data) {
    this.data = data;
  }

  /** Get text displayed on the user device screen. */
  public String getDisplayText() {
    return displayText;
  }

  /** Set text displayed on the user device screen. */
  public void setDisplayText(final String displayText) {
    this.displayText = displayText;
  }

  /** Get a data source */
  public String getSource() {
    return source;
  }

  /** Set a data source */
  public void setSource(final String source) {
    this.source = source;
  }

  /** Get list of context objects set after intent completion. */
  public List<AIOutputContext> getContextOut() {
    return contextOut;
  }

  /** Get context object by its name. */
  public AIOutputContext getContext(final String name) {
    if (StringUtils.isEmpty(name)) {
      throw new IllegalArgumentException("name argument must be not empty");
    }

    if (contextOut == null) {
      return null;
    }

    for (final AIOutputContext c : contextOut) {
      if (name.equalsIgnoreCase(c.getName())) {
        return c;
      }
    }

    return null;
  }

  /** Set list of context objects set after intent completion. */
  public void setContextOut(final List<AIOutputContext> contextOut) {
    this.contextOut = contextOut;
  }
  
  /** Set sequence of context objects set after intent completion. */
  public void setContextOut(final AIOutputContext... contextOut) {
    setContextOut(Arrays.asList(contextOut));
  }

  /** Get follow up event to be triggered*/
  public AIEvent getFollowupEvent() {
    return followupEvent;
  }

  /** Set follow up event to be triggered */
  public void setFollowupEvent(AIEvent followupEvent) {
    this.followupEvent = followupEvent;
  }
}
