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
 
package ai.api.test.compatibility;

import com.google.gson.Gson;

import ai.api.util.StringUtils;
import org.junit.Test;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Calendar;

import ai.api.AIConfiguration;
import ai.api.AIServiceException;
import ai.api.GsonFactory;
import ai.api.model.AIRequest;
import ai.api.test.compatibility.default_protocol_model.AIResponseDefault;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test for backward compatibility to the first protocol version ("default")
 */
public class DefaultProtocolTest {

    final Gson gson = GsonFactory.getDefaultFactory().getGson();

    @Test
    public void legacyContextsTest() {
        final AIConfiguration config = new AIConfiguration(
                "3485a96fb27744db83e78b8c4bc9e7b7",
                AIConfiguration.SupportedLanguages.English);

        config.setProtocolVersion(null);

        final SimpleProtocolTestingService aiDataService = new SimpleProtocolTestingService(config);

        try {
            final AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery("weather");
            prepareRequest(aiRequest, config);

            final String textRequest = gson.toJson(aiRequest);

            final String textResponse = aiDataService.doDefaultProtocolTextRequest(textRequest);

            final AIResponseDefault aiResponse = gson.fromJson(textResponse, AIResponseDefault.class);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("showWeather", aiResponse.getResult().getAction());

            final String[] contexts = aiResponse.getResult().getMetadata().getContexts();
            assertNotNull(contexts);
            boolean contextLoaded = false;
            for (int i = 0; i < contexts.length; i++) {
                if ("weather".equalsIgnoreCase(contexts[i])) {
                    contextLoaded = true;
                }
            }
            assertTrue(contextLoaded);

        } catch (final AIServiceException | MalformedURLException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void outputContextVoiceTest() {
        final AIConfiguration config = new AIConfiguration(
                "3485a96fb27744db83e78b8c4bc9e7b7",
                AIConfiguration.SupportedLanguages.English);

        config.setProtocolVersion(null);

        final SimpleProtocolTestingService aiDataService = new SimpleProtocolTestingService(config);

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("what_is_your_name.raw");

        try {

            final AIRequest aiRequest = new AIRequest();
            prepareRequest(aiRequest, config);

            final String textRequest = gson.toJson(aiRequest);

            final String textResponse = aiDataService.doDefaultProtocolSoundRequest(inputStream, textRequest);

            final AIResponseDefault aiResponse = gson.fromJson(textResponse, AIResponseDefault.class);

            assertNotNull(aiResponse);

            assertNotNull(aiResponse.getResult());
            assertNotNull(aiResponse.getResult().getMetadata());

            final String[] contexts = aiResponse.getResult().getMetadata().getContexts();
            assertNotNull(contexts);
            boolean contextLoaded = false;
            for (int i = 0; i < contexts.length; i++) {
                if ("name_question".equalsIgnoreCase(contexts[i])) {
                    contextLoaded = true;
                }
            }
            assertTrue(contextLoaded);


        } catch (final AIServiceException | MalformedURLException e) {
            e.printStackTrace();
            assertTrue(e.getMessage(), false);
        }
    }

    private void prepareRequest(final AIRequest request, final AIConfiguration config) {
        request.setLanguage(config.getApiAiLanguage());
        request.setTimezone(Calendar.getInstance().getTimeZone().getID());
    }

}
