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


public class MetadataTest {
	
	final static Gson gson = GsonFactory.getDefaultFactory().getGson();

    private static final String TEST_METADATA_SIMPLE = "{\"intentName\":\"Name\","
    		+ "\"intentId\":\"Id\","
    		+ "\"webhookUsed\":\"true\""
    		+ "}";
    
    private static final String TEST_METADATA_EMPTY = "{}";

	@Test
	public void testDeserialization() {
		
		final Metadata metadata = gson.fromJson(TEST_METADATA_SIMPLE, Metadata.class);

		assertEquals("Name", metadata.getIntentName());
		assertEquals("Id", metadata.getIntentId());
		assertTrue(metadata.isWebhookUsed());
	}
	
	@Test
	public void testDeserializationNoWebhook() {
		
		final Metadata metadata = gson.fromJson(TEST_METADATA_EMPTY, Metadata.class);

		assertNull(metadata.getIntentName());
		assertNull(metadata.getIntentId());
		assertFalse(metadata.isWebhookUsed());
	}
}
