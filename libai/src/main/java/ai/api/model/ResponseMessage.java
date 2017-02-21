package ai.api.model;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com) https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 ***********************************************************************************************************************/

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

/**
 * Base model class for 
 * <a href="https://docs.api.ai/docs/query#section-message-objects">response message objects</a>.
 */
public abstract class ResponseMessage {

  @Expose
  private int type;
  
  /**
   * Default constructor initializing message type code 
   * @param type Message type. Cannot be <code>null</code>.
   */
  protected ResponseMessage(MessageType type) {
    assert type != null;
    this.type = type.code;
  }

  /**
   * Holds the message type integer code and related {@link Type}
   */
  public static enum MessageType {
    /** Text response message object */
    SPEECH(0, ResponseSpeech.class),
    /** Card message object */
    CARD(1, ResponseCard.class),
    /** Quick replies message object */
    QUICK_REPLY(2, ResponseQuickReply.class),
    /** Image message object */
    IMAGE(3, ResponseImage.class),
    /** Custom payload message object */
    PAYLOAD(4, ResponsePayload.class);

    private final int code;
    private final Type type;

    private MessageType(int code, Type curClass) {
      this.code = code;
      this.type = curClass;
    }

    /**
     * @return Message integer code value
     */
    public int getCode() {
      return this.code;
    }

    /**
     * @return Related class {@link Type}
     */
    public Type getType() {
      return type;
    }
  }

  /**
   * <a href="https://docs.api.ai/docs/query#section-text-response-message-object">Text response</a>
   * message object
   */
  public static class ResponseSpeech extends ResponseMessage {

    @Expose
    public List<String> speech;
    
    public ResponseSpeech() {
      super(MessageType.SPEECH);
    }

    /**
     * Get agent's text replies.
     */
    public List<String> getSpeech() {
      return this.speech;
    }

    /**
     * Set agent's text replies.
     */
    public void setSpeech(List<String> speech) {
      this.speech = speech;
    }
    
    /**
     * Set agent's text replies.
     */
    public void setSpeech(String... speech) {
      setSpeech(Arrays.asList(speech));
    }
  }

  /**
   * <a href="https://docs.api.ai/docs/query#section-card-message-object">Card message</a> object
   */
  public static class ResponseCard extends ResponseMessage {

    @Expose
    private String title;

    @Expose
    private String subtitle;

    @Expose
    private String imageUrl;

    @Expose
    private List<Button> buttons;
    
    public ResponseCard() {
      super(MessageType.CARD);
    }

    /** Get card title. */
    public String getTitle() {
      return this.title;
    }

    /** Set card title. */
    public void setTitle(String title) {
      this.title = title;
    }

    /** Get card subtitle. */
    public String getSubtitle() {
      return this.subtitle;
    }

    /** Set card subtitle. */
    public void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
    }

    /** Get image url */
    public String getImageUrl() {
      return this.imageUrl;
    }

    /** Set image url */
    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }

    /** Get list of objects corresponding to card buttons. */
    public List<Button> getButtons() {
      return this.buttons;
    }

    /** Set list of objects corresponding to card buttons. */
    public void setButtons(List<Button> buttons) {
      this.buttons = buttons;
    }

    /** Set sequence of objects corresponding to card buttons. */
    public void setButtons(Button... buttons) {
      setButtons(Arrays.asList(buttons));
    }

    /** Card button object */
    public static class Button {

      @Expose
      private String text;

      @Expose
      private String postback;

      public Button(String text, String postback) {
        this.text = text;
        this.postback = postback;
      }

      /** Set button text */
      public void setText(String text) {
        this.text = text;
      }

      /** Get button text */
      public String getText() {
        return this.text;
      }

      /** Set a text sent back to API.AI or a URL to open. */
      public void setPostback(String postback) {
        this.postback = postback;
      }

      /** Get a text sent back to API.AI or a URL to open. */
      public String getPostback() {
        return this.postback;
      }
    }
  }

  /** <a href="https://docs.api.ai/docs/query#section-quick-replies-message-object">Quick
   * replies</a> message object */
  public static class ResponseQuickReply extends ResponseMessage {

    @Expose
    private String title;

    @Expose
    private List<String> replies;
    
    public ResponseQuickReply() {
      super(MessageType.QUICK_REPLY);
    }

    /** Get list of text replies */
    public List<String> getReplies() {
      return this.replies;
    }

    /** Set list of text replies */
    public void setReplies(List<String> replies) {
      this.replies = replies;
    }
    
    /** Set sequence of text replies */
    public void setReplies(String... replies) {
      setReplies(Arrays.asList(replies));
    }

    /** Set quick replies title. Required for the Facebook Messenger, Kik, and Telegram one-click
     * integrations. */
    public void setTitle(String title) {
      this.title = title;
    }

    /** Get quick replies title. Required for the Facebook Messenger, Kik, and Telegram one-click
     * integrations. */
    public String getTitle() {
      return this.title;
    }
  }

  /** 
   * <a href="https://docs.api.ai/docs/query#section-image-message-object">Image</a>  message object
   */
  public static class ResponseImage extends ResponseMessage {

    @Expose
    private String imageUrl;
    
    public ResponseImage() {
      super(MessageType.IMAGE);
    }

    /** Get image url */
    public String getImageUrl() {
      return this.imageUrl;
    }

    /** Set image url */
    public void setImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
    }
  }

  /**
   * <a href="">Custom payload</a> message object that holds an object passed through without
   * modification  
   */
  public static class ResponsePayload extends ResponseMessage {

    @Expose
    private JsonObject payload;
    
    public ResponsePayload() {
      super(MessageType.PAYLOAD);
    }

    /** Get custom defined JSON. */
    public JsonObject getPayload() {
      return this.payload;
    }

    /** Set custom defined JSON. */
    public void setPayload(JsonObject payload) {
      this.payload = payload;
    }
  }
}
