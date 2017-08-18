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
 
package ai.api;

import static org.junit.Assert.*;

import org.junit.Test;

import ai.api.AIConfiguration;
import ai.api.AIConfiguration.SupportedLanguages;

public class AIConfigurationTest {

	private static final String DEFAULT_SERVICE_URL = "http://localhost/";
	private static final String EMPTY_PROTOCOL_VERSION = null;

	@Test
	public void testSupportedLanguage() {
		assertEquals(SupportedLanguages.EnglishGB, SupportedLanguages.fromLanguageTag("en-GB"));
		assertNotNull(SupportedLanguages.DEFAULT);
		assertEquals(SupportedLanguages.DEFAULT, SupportedLanguages.fromLanguageTag(null));
		assertEquals(SupportedLanguages.DEFAULT, SupportedLanguages.fromLanguageTag(""));
		assertEquals(SupportedLanguages.DEFAULT, SupportedLanguages.fromLanguageTag("uu-UU"));
		assertEquals(SupportedLanguages.Dutch, SupportedLanguages.fromLanguageTag("nl"));
		assertEquals(SupportedLanguages.Ukrainian, SupportedLanguages.fromLanguageTag("uk"));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testNullClientAccessToken() {
		new AIConfiguration(null);
	}
	
	@Test
	public void testEmptyClientAccessToken() {
		AIConfiguration config = new AIConfiguration("");
		assertNotNull(config.getLanguage());
		assertNotNull(config.getApiAiLanguage());
	}
	
	@Test
	public void testGetContextUrl() {
		AIConfiguration config = new AIConfiguration("accessToken");
		config.setServiceUrl(DEFAULT_SERVICE_URL);
		
		config.setProtocolVersion(EMPTY_PROTOCOL_VERSION);
		assertEquals("http://localhost/contexts?sessionId=sessionId", config.getContextsUrl("sessionId"));
		assertEquals("http://localhost/contexts?sessionId=sessionId", config.getContextsUrl("sessionId", null));
		assertEquals("http://localhost/contexts/suffix?sessionId=sessionId", config.getContextsUrl("sessionId", "suffix"));
		assertEquals("http://localhost/contexts/50%2F50+suffix?sessionId=sessionId", config.getContextsUrl("sessionId", "50/50 suffix"));
		
		config.setProtocolVersion("ver");
		assertEquals("http://localhost/contexts?v=ver&sessionId=sessionId", config.getContextsUrl("sessionId"));
		assertEquals("http://localhost/contexts/suffix?v=ver&sessionId=sessionId", config.getContextsUrl("sessionId", "suffix"));
	}
}
