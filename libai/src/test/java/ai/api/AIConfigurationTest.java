package ai.api;

import static org.junit.Assert.*;

import org.junit.Test;

import ai.api.AIConfiguration;
import ai.api.AIConfiguration.SupportedLanguages;

public class AIConfigurationTest {

	@Test
	public void testSupportedLanguage() {
		assertEquals(SupportedLanguages.EnglishGB, SupportedLanguages.fromLanguageTag("en-GB"));
		assertNotNull(SupportedLanguages.DEFAULT);
		assertEquals(SupportedLanguages.DEFAULT, SupportedLanguages.fromLanguageTag(null));
		assertEquals(SupportedLanguages.DEFAULT, SupportedLanguages.fromLanguageTag(""));
		assertEquals(SupportedLanguages.DEFAULT, SupportedLanguages.fromLanguageTag("uu-UU"));
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
}
