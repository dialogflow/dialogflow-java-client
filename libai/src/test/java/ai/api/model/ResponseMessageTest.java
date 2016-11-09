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
import com.google.gson.JsonParseException;

import ai.api.GsonFactory;
import ai.api.model.ResponseMessage.ResponseCard;
import ai.api.model.ResponseMessage.ResponsePayload;
import ai.api.model.ResponseMessage.ResponseImage;
import ai.api.model.ResponseMessage.ResponseQuickReply;
import ai.api.model.ResponseMessage.ResponseSpeech;

public class ResponseMessageTest {
	
	final static Gson gson = GsonFactory.getDefaultFactory().getGson();

    private static final String TEST_SPEECH = "{\"type\":0, \"speech\":[\"one\",\"two\"]}";
    
    private static final String TEST_SPEECH_NOT_ARRAY = "{\"type\":0, \"speech\":\"one\"}";
    
    private static final String TEST_CARD = "{\"type\":1, \"title\":\"Title\", \"subtitle\":\"Subtitle\", \"imageUrl\":\"http://image\", "
    		+ "\"buttons\": [ {\"text\":\"ButtonText\",\"postback\":\"ButtonPostback\"} ]"
    		+ "}";
    
    private static final String TEST_QUICK_REPLY = "{\"type\":2, \"title\":\"Title\", \"replies\":[\"one\",\"two\"]}";
    
    private static final String TEST_IMAGE = "{\"type\":3, \"imageUrl\":\"http://image\"}";
    
    private static final String TEST_PAYLOAD = "{\"type\":4, \"payload\":{\"field1\":11}}";
    
    private static final String TEST_BAD_TYPE = "{\"type\":5, \"field1\":1}";

	@Test
	public void testResponseSpeech() {
		
		ResponseSpeech speech = (ResponseSpeech) gson.fromJson(TEST_SPEECH, ResponseMessage.class);
		assertEquals(2, speech.getSpeech().size());
		assertEquals("one", speech.getSpeech().get(0));
		assertEquals("two", speech.getSpeech().get(1));
	}
	
	@Test
	public void testResponseSpeechNotArray() {
		
		ResponseSpeech speech = (ResponseSpeech) gson.fromJson(TEST_SPEECH_NOT_ARRAY, ResponseMessage.class);;
		assertEquals(1, speech.getSpeech().size());
		assertEquals("one", speech.getSpeech().get(0));
	}
	
	@Test
	public void testResponseCard() {
		
		ResponseCard card = (ResponseCard)gson.fromJson(TEST_CARD, ResponseMessage.class);
		assertEquals("Title", card.getTitle());
		assertEquals("Subtitle", card.getSubtitle());
		assertEquals("http://image", card.getImageUrl());
		assertEquals(1, card.getButtons().size());
		assertEquals("ButtonText", card.getButtons().get(0).getText());
		assertEquals("ButtonPostback", card.getButtons().get(0).getPostback());
	}
	
	@Test
	public void testResponseQuickReply() {
		
		ResponseQuickReply quickReply = (ResponseQuickReply)gson.fromJson(TEST_QUICK_REPLY, ResponseMessage.class);;
		assertEquals("Title", quickReply.getTitle());
		assertEquals(2, quickReply.getReplies().size());
		assertEquals("one", quickReply.getReplies().get(0));
		assertEquals("two", quickReply.getReplies().get(1));
	}
	
	@Test
	public void testResponseImage() {
		
		ResponseImage image = (ResponseImage)gson.fromJson(TEST_IMAGE, ResponseMessage.class);
		assertEquals("http://image", image.getImageUrl());
	}
	
	@Test
	public void testResponsePayload() {
		
		ResponsePayload payload = (ResponsePayload)gson.fromJson(TEST_PAYLOAD, ResponseMessage.class);
		assertEquals(11, payload.getPayload().get("field1").getAsInt());
	}
	
	@Test(expected=JsonParseException.class)
	public void testBadType() {
		gson.fromJson(TEST_BAD_TYPE, ResponseMessage.class);
	}
}
