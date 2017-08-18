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

import java.util.List;

public abstract class GoogleAssistantResponseMessages extends ResponseMessage {

  protected GoogleAssistantResponseMessages(MessageType type) {
    super(type, Platform.GOOGLE);
  }

  public static class CardImage {
    private String url;

    /**
     * @return the URL
     */
    public final String getUrl() {
      return url;
    }

    /**
     * @param url the URL to set
     */
    public final void setUrl(String url) {
      this.url = url;
    }
  }

  public static class OptionInfo {
    private String key;
    private List<String> synonyms;

    /**
     * @return the key
     */
    public final String getKey() {
      return key;
    }

    /**
     * @param key the key to set
     */
    public final void setKey(String key) {
      this.key = key;
    }

    /**
     * @return the synonyms
     */
    public final List<String> getSynonyms() {
      return synonyms;
    }

    /**
     * @param synonyms the synonyms to set
     */
    public final void setSynonyms(List<String> synonyms) {
      this.synonyms = synonyms;
    }
  }

  public static class CardItem {
    private OptionInfo optionInfo;
    private String title;
    private String description;
    private CardImage image;
    /**
     * @return the optionInfo
     */
    public final OptionInfo getOptionInfo() {
      return optionInfo;
    }
    /**
     * @param optionInfo the optionInfo to set
     */
    public final void setOptionInfo(OptionInfo optionInfo) {
      this.optionInfo = optionInfo;
    }
    /**
     * @return the title
     */
    public final String getTitle() {
      return title;
    }
    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
      this.title = title;
    }
    /**
     * @return the description
     */
    public final String getDescription() {
      return description;
    }
    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
      this.description = description;
    }
    /**
     * @return the image
     */
    public final CardImage getImage() {
      return image;
    }
    /**
     * @param image the image to set
     */
    public final void setImage(CardImage image) {
      this.image = image;
    }
  }

  public static class ResponseChatBubble extends GoogleAssistantResponseMessages {

    private Boolean customizeAudio;
    private List<Item> items;

    public ResponseChatBubble() {
      super(MessageType.CHAT_BUBBLE);
    }

    /**
     * @return the customizeAudio
     */
    public final Boolean getCustomizeAudio() {
      return customizeAudio;
    }

    /**
     * @param customizeAudio the customizeAudio to set
     */
    public final void setCustomizeAudio(Boolean customizeAudio) {
      this.customizeAudio = customizeAudio;
    }

    /**
     * @return the items
     */
    public final List<Item> getItems() {
      return items;
    }

    /**
     * @param items the items to set
     */
    public final void setItems(List<Item> items) {
      this.items = items;
    }

    public static class Item {
      private String textToSpeech;
      private String ssml;
      private String displayText;

      /**
       * @return the textToSpeech
       */
      public final String getTextToSpeech() {
        return textToSpeech;
      }

      /**
       * @param textToSpeech the textToSpeech to set
       */
      public final void setTextToSpeech(String textToSpeech) {
        this.textToSpeech = textToSpeech;
      }

      /**
       * @return the SSML
       */
      public final String getSsml() {
        return ssml;
      }

      /**
       * @param ssml the SSML to set
       */
      public final void setSsml(String ssml) {
        this.ssml = ssml;
      }

      /**
       * @return the displayText
       */
      public final String getDisplayText() {
        return displayText;
      }

      /**
       * @param displayText the displayText to set
       */
      public final void setDisplayText(String displayText) {
        this.displayText = displayText;
      }
    }
  }
  
  public static class ResponseBasicCard extends GoogleAssistantResponseMessages {
    private String title;
    private String subtitle;
    private String formattedText;
    private CardImage image;
    private List<Button> buttons;

    public ResponseBasicCard() {
      super(MessageType.BASIC_CARD);
    }

    /**
     * @return the title
     */
    public final String getTitle() {
      return title;
    }

    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
      this.title = title;
    }

    /**
     * @return the subtitle
     */
    public final String getSubtitle() {
      return subtitle;
    }

    /**
     * @param subtitle the subtitle to set
     */
    public final void setSubtitle(String subtitle) {
      this.subtitle = subtitle;
    }

    /**
     * @return the formattedText
     */
    public final String getFormattedText() {
      return formattedText;
    }

    /**
     * @param formattedText the formattedText to set
     */
    public final void setFormattedText(String formattedText) {
      this.formattedText = formattedText;
    }

    /**
     * @return the image
     */
    public final CardImage getImage() {
      return image;
    }

    /**
     * @param image the image to set
     */
    public final void setImage(CardImage image) {
      this.image = image;
    }

    /**
     * @return the buttons
     */
    public final List<Button> getButtons() {
      return buttons;
    }

    /**
     * @param buttons the buttons to set
     */
    public final void setButtons(List<Button> buttons) {
      this.buttons = buttons;
    }

    public static class Button {
      private String title;
      private OpenUrlAction openUrlAction;

      /**
       * @return the title
       */
      public final String getTitle() {
        return title;
      }

      /**
       * @param title the title to set
       */
      public final void setTitle(String title) {
        this.title = title;
      }

      /**
       * @return the openUrlAction
       */
      public final OpenUrlAction getOpenUrlAction() {
        return openUrlAction;
      }

      /**
       * @param openUrlAction the openUrlAction to set
       */
      public final void setOpenUrlAction(OpenUrlAction openUrlAction) {
        this.openUrlAction = openUrlAction;
      }
    }

    public static class OpenUrlAction {
      private String url;

      /**
       * @return the URL
       */
      public final String getUrl() {
        return url;
      }

      /**
       * @param url the URL to set
       */
      public final void setUrl(String url) {
        this.url = url;
      }
    }
  }

  public static class ResponseListCard extends GoogleAssistantResponseMessages {

    private String title;
    private List<CardItem> items;

    public ResponseListCard() {
      super(MessageType.LIST_CARD);
    }

    /**
     * @return the title
     */
    public final String getTitle() {
      return title;
    }

    /**
     * @param title the title to set
     */
    public final void setTitle(String title) {
      this.title = title;
    }

    /**
     * @return the items
     */
    public final List<CardItem> getItems() {
      return items;
    }

    /**
     * @param items the items to set
     */
    public final void setItems(List<CardItem> items) {
      this.items = items;
    }
  }

  public static class ResponseSuggestionChips extends GoogleAssistantResponseMessages {

    private List<Suggestion> suggestions;

    public ResponseSuggestionChips() {
      super(MessageType.SUGGESTION_CHIPS);
    }

    
    
    /**
     * @return the suggestions
     */
    public final List<Suggestion> getSuggestions() {
      return suggestions;
    }

    /**
     * @param suggestions the suggestions to set
     */
    public final void setSuggestions(List<Suggestion> suggestions) {
      this.suggestions = suggestions;
    }

    public static class Suggestion {
      private String title;

      /**
       * @return the title
       */
      public final String getTitle() {
        return title;
      }

      /**
       * @param title the title to set
       */
      public final void setTitle(String title) {
        this.title = title;
      }
    }
  }

  public static class ResponseCarouselCard extends GoogleAssistantResponseMessages {

    private List<CardItem> items;

    public ResponseCarouselCard() {
      super(MessageType.CAROUSEL_CARD);
    }

    /**
     * @return the items
     */
    public final List<CardItem> getItems() {
      return items;
    }

    /**
     * @param items the items to set
     */
    public final void setItems(List<CardItem> items) {
      this.items = items;
    }
  }

  public static class ResponseLinkOutChip extends GoogleAssistantResponseMessages {

    private String destinationName;
    private String url;

    public ResponseLinkOutChip() {
      super(MessageType.LINK_OUT_CHIP);
    }

    /**
     * @return the destinationName
     */
    public final String getDestinationName() {
      return destinationName;
    }

    /**
     * @param destinationName the destinationName to set
     */
    public final void setDestinationName(String destinationName) {
      this.destinationName = destinationName;
    }

    /**
     * @return the URL
     */
    public final String getUrl() {
      return url;
    }

    /**
     * @param url the URL to set
     */
    public final void setUrl(String url) {
      this.url = url;
    }
  }
}
