package ai.api.model;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

import static org.junit.Assert.*;

import org.junit.Test;

import com.google.gson.Gson;

import ai.api.GsonFactory;
import ai.api.model.ResponseMessage.ResponseSpeech;


public class FulfillmentTest {
	
	final static Gson gson = GsonFactory.getDefaultFactory().getGson();

    private static final String TEST_FULFILLMENT = "{\"speech\":\"text\", "
    		+ "\"messages\":[{\"type\":0, \"speech\":[\"one\"]}]}";
    
    private static final String TEST_FULFILLMENT_NO_MESSAGES = "{\"speech\":\"text\"}";
    
    private static final String TEST_FULFILLMENT_WEBHOOK_RESPONSE = "{\"speech\":\"text\","
    		+ "\"displayText\":\"DisplayText\", \"source\":\"webhook\", "
    		+ "\"contextOut\": [{\"name\":\"weather\", \"lifespan\":2, \"parameters\":{\"city\":\"Rome\"}}],"
    		+ "\"data\":{\"param\":\"value\"}}";

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
		
		final Fulfillment fulfillment = gson.fromJson(TEST_FULFILLMENT_WEBHOOK_RESPONSE, Fulfillment.class);

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
	}
}
