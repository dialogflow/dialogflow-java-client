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

import ai.api.GsonFactory;
import ai.api.model.ResponseMessage.ResponseImage;
import ai.api.model.ResponseMessage.ResponseQuickReply;
import ai.api.model.ResponseMessage.ResponseSpeech;


public class FulfillmentTest {

  final static Gson gson = GsonFactory.getDefaultFactory().getGson();

  private static final String TEST_FULFILLMENT =
      "{\"speech\":\"text\", " + "\"messages\":[{\"type\":0, \"speech\":[\"one\"]}]}";

  private static final String TEST_FULFILLMENT_NO_MESSAGES = "{\"speech\":\"text\"}";

  private static final String TEST_FULFILLMENT_WEBHOOK_RESPONSE =
      "{\"speech\":\"text\"," + "\"displayText\":\"DisplayText\", \"source\":\"webhook\", "
          + "\"contextOut\": [{\"name\":\"weather\", \"lifespan\":2, \"parameters\":{\"city\":\"Rome\"}}],"
          + "\"data\":{\"param\":\"value\"},"
          + "\"followupEvent\":{\"data\":{\"event-param\":\"event-value\"},\"name\":\"test-event\"}}";
  
  private static final String TEST_FULFILLMENT_WITH_MESSAGES = "{\"speech\":\"test speech\","+
      "\"messages\":[{\"imageUrl\":\"url image\",\"type\":3},{\"title\":\"Quick title\","+
      "\"type\":2},{\"speech\":[\"speech 1\",\"speech 2\"],\"type\":0}]}";
  
  @Test
  public void testDeserialization() {

    final Fulfillment fulfillment = gson.fromJson(TEST_FULFILLMENT, Fulfillment.class);

    assertEquals("text", fulfillment.getSpeech());


    assertEquals(1, fulfillment.getMessages().size());

    ResponseSpeech speech = (ResponseSpeech) fulfillment.getMessages().get(0);

    assertEquals(1, speech.getSpeech().size());
    assertEquals("one", speech.getSpeech().get(0));
  }

  @Test
  public void testDeserializationNoMessages() {

    final Fulfillment fulfillment = gson.fromJson(TEST_FULFILLMENT_NO_MESSAGES, Fulfillment.class);

    assertNull(fulfillment.getMessages());
  }

  @Test
  public void testDeserializationWebhookResponse() {

    final Fulfillment fulfillment =
        gson.fromJson(TEST_FULFILLMENT_WEBHOOK_RESPONSE, Fulfillment.class);

    assertEquals("DisplayText", fulfillment.getDisplayText());
    assertEquals("webhook", fulfillment.getSource());

    assertEquals(1, fulfillment.getContextOut().size());
    AIOutputContext context = fulfillment.getContext("weather");
    assertEquals("weather", context.getName());
    assertEquals(2, context.getLifespan());

    assertEquals(1, context.getParameters().size());
    assertEquals("Rome", context.getParameters().get("city").getAsString());

    assertEquals(1, fulfillment.getData().size());
    assertEquals("value", fulfillment.getData().get("param").getAsString());

    assertNotNull(fulfillment.getFollowupEvent());
    assertEquals("test-event", fulfillment.getFollowupEvent().getName());
    assertEquals(1, fulfillment.getFollowupEvent().getData().size());
    assertEquals("event-value", fulfillment.getFollowupEvent().getData().get("event-param"));
  }

  @Test
  public void testSerializationWithMessages() {
    ResponseSpeech speech = new ResponseMessage.ResponseSpeech();
    speech.setSpeech("speech 1", "speech 2");
        
    ResponseImage image = new ResponseMessage.ResponseImage();
    image.setImageUrl("url image");

    ResponseQuickReply quickReply = new ResponseMessage.ResponseQuickReply();
    quickReply.setTitle("Quick title");

    Fulfillment full = new Fulfillment();
    full.setSpeech("test speech");
    full.setMessages(image, quickReply, speech);
    assertEquals(TEST_FULFILLMENT_WITH_MESSAGES, gson.toJson(full));
  }
}
