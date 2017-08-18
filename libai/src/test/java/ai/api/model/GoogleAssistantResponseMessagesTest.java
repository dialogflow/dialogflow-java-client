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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;

import ai.api.GsonFactory;
import ai.api.model.GoogleAssistantResponseMessages.CardImage;
import ai.api.model.GoogleAssistantResponseMessages.CardItem;
import ai.api.model.GoogleAssistantResponseMessages.OptionInfo;
import ai.api.model.GoogleAssistantResponseMessages.ResponseBasicCard;
import ai.api.model.GoogleAssistantResponseMessages.ResponseBasicCard.OpenUrlAction;
import ai.api.model.GoogleAssistantResponseMessages.ResponseCarouselCard;
import ai.api.model.GoogleAssistantResponseMessages.ResponseChatBubble;
import ai.api.model.GoogleAssistantResponseMessages.ResponseLinkOutChip;
import ai.api.model.GoogleAssistantResponseMessages.ResponseListCard;
import ai.api.model.GoogleAssistantResponseMessages.ResponseSuggestionChips;

public class GoogleAssistantResponseMessagesTest {

  final static Gson gson = GsonFactory.getDefaultFactory().getGson();

  private final String TEST_CHAT_BUBBLE_SEVERAL_ITEMS = "{"
      + "\"customizeAudio\":true,\"items\":["
      + "{\"textToSpeech\":\"hello1\",\"ssml\":\"ssmlText1\",\"displayText\":\"Hello1\"},"
      + "{\"textToSpeech\":\"hello2\",\"ssml\":\"ssmlText2\",\"displayText\":\"Hello2\"}"
      + "],\"type\":\"simple_response\",\"platform\":\"google\""
      + "}";

  private final String TEST_CHAT_BUBBLE_SINGLE_ITEM = "{"
      + "\"customizeAudio\":true,"
      + "\"type\":\"simple_response\",\"platform\":\"google\","
      + "\"textToSpeech\":\"hello\",\"ssml\":\"ssmlText\",\"displayText\":\"Hello\""
      + "}";

  private final String TEST_BASIC_CARD = "{"
      + "\"title\":\"titleVal\",\"subtitle\":\"subtitleVal\",\"formattedText\":\"formattedVal\","
      + "\"image\":{\"url\":\"urlVal\"},"
      + "\"buttons\":[{\"title\":\"buttonTitle\",\"openUrlAction\":{\"url\":\"openActionUrlVal\"}}],"
      + "\"type\":\"basic_card\",\"platform\":\"google\""
      + "}";

  private final String TEST_LIST_CARD = "{"
      + "\"title\":\"titleVal\","
      + "\"items\":[{"
      + "\"optionInfo\":{\"key\":\"keyVal\",\"synonyms\":[\"syn1\"]},"
      + "\"title\":\"itemTitle\",\"description\":\"itemDescription\","
      + "\"image\":{\"url\":\"urlVal\"}"
      + "}],"
      + "\"type\":\"list_card\",\"platform\":\"google\""
      + "}";

  private final String TEST_SUGGESTION_CHIP = "{"
      + "\"suggestions\":[{\"title\":\"titleVal\"}],"
      + "\"type\":\"suggestion_chips\",\"platform\":\"google\""
      + "}";

  private final String TEST_CAROUSEL_CARD = "{"
      + "\"items\":[{"
      + "\"optionInfo\":{\"key\":\"keyVal\",\"synonyms\":[\"syn1\"]},"
      + "\"title\":\"itemTitle\",\"description\":\"itemDescription\","
      + "\"image\":{\"url\":\"urlVal\"}"
      + "}],"
      + "\"type\":\"carousel_card\",\"platform\":\"google\""
      + "}";

  private final String TEST_LINK_OUT_CHIP = "{"
      + "\"destinationName\":\"destanationVal\",\"url\":\"urlVal\","
      + "\"type\":\"link_out_chip\",\"platform\":\"google\""
      + "}";

  @Test
  public void testResponseChatBubbleSerialization() {
    ResponseChatBubble chatBubble = new ResponseChatBubble();
    chatBubble.setCustomizeAudio(true);
    
    ResponseChatBubble.Item item = new ResponseChatBubble.Item();
    item.setTextToSpeech("hello");
    item.setSsml("ssmlText");
    item.setDisplayText("Hello");

    chatBubble.setItems(Arrays.asList(item));
    assertEquals(TEST_CHAT_BUBBLE_SINGLE_ITEM, gson.toJson(chatBubble));
  }

  @Test
  public void testResponseChatBubbleDeserialization() {
    ResponseChatBubble chatBubble =
        (ResponseChatBubble) gson.fromJson(TEST_CHAT_BUBBLE_SINGLE_ITEM, ResponseMessage.class);
    assertTrue(chatBubble.getCustomizeAudio());
    assertEquals(1, chatBubble.getItems().size());
    assertEquals("hello", chatBubble.getItems().get(0).getTextToSpeech());
    assertEquals("ssmlText", chatBubble.getItems().get(0).getSsml());
    assertEquals("Hello", chatBubble.getItems().get(0).getDisplayText());
  }

  @Test
  public void testResponseChatBubbleSeveralItemsDeserialization() {
    ResponseChatBubble chatBubble =
        (ResponseChatBubble) gson.fromJson(TEST_CHAT_BUBBLE_SEVERAL_ITEMS, ResponseMessage.class);
    assertTrue(chatBubble.getCustomizeAudio());
    assertEquals(2, chatBubble.getItems().size());
    assertEquals("hello1", chatBubble.getItems().get(0).getTextToSpeech());
    assertEquals("ssmlText1", chatBubble.getItems().get(0).getSsml());
    assertEquals("Hello1", chatBubble.getItems().get(0).getDisplayText());
    assertEquals("hello2", chatBubble.getItems().get(1).getTextToSpeech());
    assertEquals("ssmlText2", chatBubble.getItems().get(1).getSsml());
    assertEquals("Hello2", chatBubble.getItems().get(1).getDisplayText());
  }

  @Test
  public void testResponseChatBubbleSeveralItemsSerialization() {
    ResponseChatBubble chatBubble = new ResponseChatBubble();
    chatBubble.setCustomizeAudio(true);
    
    ResponseChatBubble.Item item1 = new ResponseChatBubble.Item();
    item1.setTextToSpeech("hello1");
    item1.setSsml("ssmlText1");
    item1.setDisplayText("Hello1");

    ResponseChatBubble.Item item2 = new ResponseChatBubble.Item();
    item2.setTextToSpeech("hello2");
    item2.setSsml("ssmlText2");
    item2.setDisplayText("Hello2");

    chatBubble.setItems(Arrays.asList(item1, item2));
    assertEquals(TEST_CHAT_BUBBLE_SEVERAL_ITEMS, gson.toJson(chatBubble));
  }
  @Test
  public void testResponseBasicCardDeserialization() {
    ResponseBasicCard basicCard =
        (ResponseBasicCard) gson.fromJson(TEST_BASIC_CARD, ResponseMessage.class);
    assertEquals("titleVal", basicCard.getTitle());
    assertEquals("subtitleVal", basicCard.getSubtitle());
    assertEquals("formattedVal", basicCard.getFormattedText());
    checkImage(basicCard.getImage());
    assertEquals(1, basicCard.getButtons().size());
    assertEquals("buttonTitle", basicCard.getButtons().get(0).getTitle());
    assertEquals("openActionUrlVal", basicCard.getButtons().get(0).getOpenUrlAction().getUrl());
  }

  @Test
  public void testResponseBasicCardSerialization() {
    ResponseBasicCard basicCard = new ResponseBasicCard();
    basicCard.setTitle("titleVal");
    basicCard.setSubtitle("subtitleVal");
    basicCard.setFormattedText("formattedVal");
    basicCard.setImage(createImage());
    
    ResponseBasicCard.Button button = new ResponseBasicCard.Button();
    button.setTitle("buttonTitle");
    OpenUrlAction openUrlAction = new OpenUrlAction();
    openUrlAction.setUrl("openActionUrlVal");
    button.setOpenUrlAction(openUrlAction);
    basicCard.setButtons(Arrays.asList(button));

    assertEquals(TEST_BASIC_CARD, gson.toJson(basicCard));
  }

  @Test
  public void testResponseListCardDeserialization() {
    ResponseListCard listCard =
        (ResponseListCard) gson.fromJson(TEST_LIST_CARD, ResponseMessage.class);
    assertEquals("titleVal", listCard.getTitle());
    checkItems(listCard.getItems());
  }

  @Test
  public void testResponseListCardSerialization() {
    ResponseListCard listCard = new ResponseListCard();

    listCard.setTitle("titleVal");
    listCard.setItems(Arrays.asList(createItem()));
    checkItems(listCard.getItems());

    assertEquals(TEST_LIST_CARD, gson.toJson(listCard));
  }


  @Test
  public void testResponseSuggestionChipsDeserialization() {
    ResponseSuggestionChips suggestionChips =
        (ResponseSuggestionChips) gson.fromJson(TEST_SUGGESTION_CHIP, ResponseMessage.class);

    assertEquals(1, suggestionChips.getSuggestions().size());
    assertEquals("titleVal", suggestionChips.getSuggestions().get(0).getTitle());
  }

  @Test
  public void testResponseSuggestionChipsSerialization() {
    ResponseSuggestionChips suggestionChips = new ResponseSuggestionChips();
    
    ResponseSuggestionChips.Suggestion suggestion = new ResponseSuggestionChips.Suggestion();
    suggestion.setTitle("titleVal");
    suggestionChips.setSuggestions(Arrays.asList(suggestion));

    assertEquals(TEST_SUGGESTION_CHIP, gson.toJson(suggestionChips));
  }

  @Test
  public void testResponseCarouselCardDeserialization() {
    ResponseCarouselCard carouselCard =
        (ResponseCarouselCard) gson.fromJson(TEST_CAROUSEL_CARD, ResponseMessage.class);
    checkItems(carouselCard.getItems());
  }

  @Test
  public void testResponseCarouselCardSerialization() {
    ResponseCarouselCard carouselCard = new ResponseCarouselCard();
    carouselCard.setItems(Arrays.asList(createItem()));

    assertEquals(TEST_CAROUSEL_CARD, gson.toJson(carouselCard));
  }

  @Test
  public void testResponseLinkOutChipDeserialization() {
    ResponseLinkOutChip linkOutChip =
        (ResponseLinkOutChip) gson.fromJson(TEST_LINK_OUT_CHIP, ResponseMessage.class);

    assertEquals("destanationVal", linkOutChip.getDestinationName());
    assertEquals("urlVal", linkOutChip.getUrl());
  }

  @Test
  public void testResponseLinkOutChipSerialization() {
    ResponseLinkOutChip linkOutChip = new ResponseLinkOutChip();
    linkOutChip.setDestinationName("destanationVal");
    linkOutChip.setUrl("urlVal");

    assertEquals(TEST_LINK_OUT_CHIP, gson.toJson(linkOutChip));
  }

  private void checkItems(List<CardItem> items) {
    assertEquals(1, items.size());
    checkItem(items.get(0));
  }

  private void checkItem(CardItem item) {
    assertEquals("itemTitle", item.getTitle());
    assertEquals("itemDescription", item.getDescription());
    checkImage(item.getImage());
    assertEquals("keyVal", item.getOptionInfo().getKey());
    assertEquals(1, item.getOptionInfo().getSynonyms().size());
    assertEquals("syn1", item.getOptionInfo().getSynonyms().get(0));
  }
  
  private CardItem createItem() {
    CardItem result = new CardItem();
    
    result.setTitle("itemTitle");
    result.setDescription("itemDescription");
    result.setImage(createImage());
    
    OptionInfo optionInfo = new OptionInfo();
    optionInfo.setKey("keyVal");
    optionInfo.setSynonyms(Arrays.asList("syn1"));
    result.setOptionInfo(optionInfo);
    
    return result;
  }
  
  private void checkImage(CardImage image) {
    assertEquals("urlVal", image.getUrl());
  }
  
  private CardImage createImage() {
    CardImage result = new CardImage();
    result.setUrl("urlVal");
    return result;
  }
}
