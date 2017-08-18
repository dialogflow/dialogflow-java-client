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

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

import ai.api.model.GoogleAssistantResponseMessages.ResponseBasicCard;
import ai.api.model.GoogleAssistantResponseMessages.ResponseCarouselCard;
import ai.api.model.GoogleAssistantResponseMessages.ResponseChatBubble;
import ai.api.model.GoogleAssistantResponseMessages.ResponseLinkOutChip;
import ai.api.model.GoogleAssistantResponseMessages.ResponseListCard;
import ai.api.model.GoogleAssistantResponseMessages.ResponseSuggestionChips;

/**
 * Base model class for 
 * <a href="https://docs.api.ai/docs/query#section-message-objects">response message objects</a>.
 */
public abstract class ResponseMessage {

  @Expose
  private final MessageType type;

  @Expose
  private final Platform platform;

  /**
   * Constructor initializing message type code 
   * @param type Message type. Cannot be <code>null</code>.
   */
  protected ResponseMessage(MessageType type) {
    this(type, null);
  }
  
  /**
   * Constructor initializing message type code and platform
   * @param type Message type. Cannot be <code>null</code>.
   * @param platform Platform type. If <code>null</code> then
   *    default value will be used
   */
  protected ResponseMessage(MessageType type, Platform platform) {
    assert type != null;
    this.type = type;
    this.platform = platform != null ? platform : Platform.DEFAULT;
  }

  /**
   * Holds the message type integer code and related {@link Type}
   */
  public static enum MessageType {
    /** Text response message object */
    SPEECH(0, "message", ResponseSpeech.class),
    /** Card message object */
    CARD(1, "card", ResponseCard.class),
    /** Quick replies message object */
    QUICK_REPLY(2, "quick_reply", ResponseQuickReply.class),
    /** Image message object */
    IMAGE(3, "image", ResponseImage.class),
    /** Custom payload message object */
    PAYLOAD(4, "custom_payload", ResponsePayload.class),
    CHAT_BUBBLE(5, "simple_response", ResponseChatBubble.class),
    BASIC_CARD(6, "basic_card", ResponseBasicCard.class),
    LIST_CARD(7, "list_card", ResponseListCard.class),
    SUGGESTION_CHIPS(8, "suggestion_chips", ResponseSuggestionChips.class),
    CAROUSEL_CARD(9, "carousel_card", ResponseCarouselCard.class),
    LINK_OUT_CHIP(10, "link_out_chip", ResponseLinkOutChip.class);
    
    private final int code;
    private final String name;
    private final Type type;

    private MessageType(int code, String name, Type curClass) {
      assert name != null;
      assert curClass != null;
      this.code = code;
      this.name = name;
      this.type = curClass;
    }

    /**
     * @return Message integer code value
     */
    public int getCode() {
      return this.code;
    }

    /**
     * @return Type name presentation
     */
    public String getName() {
      return name;
    }

    /**
     * @return Related class {@link Type}
     */
    public Type getType() {
      return type;
    }
    

    
    private static Map<Integer,MessageType> typeByCode = new HashMap<>();
    private static Map<String,MessageType> typeByName = new HashMap<>();

    static {
      for (MessageType type : values()) {
        typeByCode.put(type.code, type);
        typeByName.put(type.name.toLowerCase(), type);
      }
    }
    
    public static MessageType fromCode(int code) {
      return typeByCode.get(code);
    }
    
    public static MessageType fromName(String name) {
      return typeByName.get(name != null ? name.toLowerCase() : null);
    }
  }

  public enum Platform {
    DEFAULT(null),
    GOOGLE("google"),
    FACEBOOK("facebook"),
    SLACK("slack"),
    TELEGRAM("telegram"),
    KIK("kik"),
    VIBER("viber"),
    SKYPE("skype"),
    LINE("line");

    private final String name;

    Platform(String name) {
      this.name = name;
    }
    
    public String getName() {
      return name;
    }
    
    private static Map<String,Platform> platformByName = new HashMap<>();
    
    static {
      for (Platform platform : values()) {
        String platformName = platform.getName();
        platformByName.put(platformName != null ? platformName.toLowerCase() : null, platform);
      }
    }
    
    public static Platform fromName(String name) {
      return platformByName.get(name != null ? name.toLowerCase() : null);
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
