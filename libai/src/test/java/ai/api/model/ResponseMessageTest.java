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

import static org.junit.Assert.*;
import org.junit.Test;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import ai.api.GsonFactory;
import ai.api.model.ResponseMessage.ResponseCard;
import ai.api.model.ResponseMessage.ResponsePayload;
import ai.api.model.ResponseMessage.ResponseImage;
import ai.api.model.ResponseMessage.ResponseQuickReply;
import ai.api.model.ResponseMessage.ResponseSpeech;

public class ResponseMessageTest {

  final static Gson gson = GsonFactory.getDefaultFactory().getGson();

  private static final String TEST_SPEECH = "{\"speech\":[\"one\",\"two\"],\"type\":0}";

  private static final String TEST_SPEECH_NOT_ARRAY = "{\"speech\":\"one\",\"type\":0}";
  private static final String TEST_SPEECH_SINGLE_SPEECH_VALUE = "{\"speech\":[\"one\"],\"type\":0}";

  private static final String TEST_CARD =
      "{\"title\":\"Title\",\"subtitle\":\"Subtitle\",\"imageUrl\":\"http://image\","
          + "\"buttons\":[{\"text\":\"ButtonText\",\"postback\":\"ButtonPostback\"}],\"type\":1}";

  private static final String TEST_QUICK_REPLY =
      "{\"title\":\"Title\",\"replies\":[\"one\",\"two\"],\"type\":2}";

  private static final String TEST_IMAGE = "{\"imageUrl\":\"http://image\",\"type\":3}";

  private static final String TEST_PAYLOAD = "{\"payload\":{\"field1\":11},\"type\":4}";

  private static final String TEST_BAD_TYPE = "{\"field1\":1,\"type\":100}";

  @Test
  public void testResponseSpeechDeserialization() {

    ResponseSpeech speech = (ResponseSpeech) gson.fromJson(TEST_SPEECH, ResponseMessage.class);
    assertEquals(2, speech.getSpeech().size());
    assertEquals("one", speech.getSpeech().get(0));
    assertEquals("two", speech.getSpeech().get(1));
  }

  @Test
  public void testResponseSpeechSerialization() {
    ResponseSpeech speech = new ResponseSpeech();
    speech.setSpeech("one", "two");
    assertEquals(TEST_SPEECH, gson.toJson(speech));
  }

  @Test
  public void testResponseSpeechNotArrayDeserialization() {
    ResponseSpeech speech =
        (ResponseSpeech) gson.fromJson(TEST_SPEECH_NOT_ARRAY, ResponseMessage.class);
    assertEquals(1, speech.getSpeech().size());
    assertEquals("one", speech.getSpeech().get(0));
  }

  @Test
  public void testResponseSpeechNotArraySerialization() {
    ResponseSpeech speech = new ResponseSpeech();
    speech.setSpeech("one");
    assertEquals(TEST_SPEECH_SINGLE_SPEECH_VALUE, gson.toJson(speech));
  }

  @Test
  public void testResponseCardDeserialization() {
    ResponseCard card = (ResponseCard) gson.fromJson(TEST_CARD, ResponseMessage.class);
    assertEquals("Title", card.getTitle());
    assertEquals("Subtitle", card.getSubtitle());
    assertEquals("http://image", card.getImageUrl());
    assertEquals(1, card.getButtons().size());
    assertEquals("ButtonText", card.getButtons().get(0).getText());
    assertEquals("ButtonPostback", card.getButtons().get(0).getPostback());
  }

  @Test
  public void testResponseCardSerialization() {
    ResponseCard card = new ResponseCard();
    card.setTitle("Title");
    card.setSubtitle("Subtitle");
    card.setImageUrl("http://image");
    card.setButtons(new ResponseCard.Button("ButtonText", "ButtonPostback"));
    assertEquals(TEST_CARD, gson.toJson(card));
  }

  @Test
  public void testResponseQuickReplyDeserialization() {
    ResponseQuickReply quickReply =
        (ResponseQuickReply) gson.fromJson(TEST_QUICK_REPLY, ResponseMessage.class);
    assertEquals("Title", quickReply.getTitle());
    assertEquals(2, quickReply.getReplies().size());
    assertEquals("one", quickReply.getReplies().get(0));
    assertEquals("two", quickReply.getReplies().get(1));
  }
  
  @Test
  public void testResponseQuickReplySerialization() {
    ResponseQuickReply quickReply = new ResponseQuickReply();
    quickReply.setTitle("Title");
    quickReply.setReplies("one", "two");
    assertEquals(TEST_QUICK_REPLY, gson.toJson(quickReply));
  }

  @Test
  public void testResponseImageDeserialization() {
    ResponseImage image = (ResponseImage) gson.fromJson(TEST_IMAGE, ResponseMessage.class);
    assertEquals("http://image", image.getImageUrl());
  }
  
  @Test
  public void testResponseImageSerialization() {
    ResponseImage image = new ResponseImage();
    image.setImageUrl("http://image");
    assertEquals(TEST_IMAGE, gson.toJson(image));
  }

  @Test
  public void testResponsePayloadDeserialization() {
    ResponsePayload payload = (ResponsePayload) gson.fromJson(TEST_PAYLOAD, ResponseMessage.class);
    assertEquals(11, payload.getPayload().get("field1").getAsInt());
  }
  
  @Test
  public void testResponsePayloadSerialization() {
    ResponsePayload payload = new ResponsePayload();
    payload.setPayload(gson.toJsonTree(new PayloadBody(11)).getAsJsonObject());
    assertEquals(TEST_PAYLOAD, gson.toJson(payload));
  }

  @Test(expected = JsonParseException.class)
  public void testBadType() {
    gson.fromJson(TEST_BAD_TYPE, ResponseMessage.class);
  }

  @Test
  public void testResponseSpeechDeserializationSerialization() {
    final String json = "{\"speech\":[\"text\"],\"type\":0}";
    assertEquals(json, gson.toJson(gson.fromJson(json, ResponseMessage.class)));
  }

  private static class PayloadBody {
    @SuppressWarnings("unused") /* this field used by serialization class */ 
    private final int field1;
    
    public PayloadBody(int value) {
      this.field1 = value;
    }
  }
}
