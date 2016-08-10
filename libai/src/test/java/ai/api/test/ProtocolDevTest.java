package ai.api.test;

/**
 * ********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2014 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 * **********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * *********************************************************************************************************************
 */

import ai.api.util.StringUtils;
import org.junit.Test;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class ProtocolDevTest extends ProtocolTestBase {

    // Testing keys
    protected static final String ACCESS_TOKEN = "a410386b327946c6ac162aad10cf9788";

    @Override
    protected String getAccessToken() {
        return ACCESS_TOKEN;
    }

    @Override
    protected String getSecondAccessToken() {
        return "212bf93686444afab30e20ed6f2dff1d";
    }

    @Override
    protected String getRuAccessToken() {
        return "e479d45dc09046c1984d6c546973dd60";
    }

    @Override
    protected String getBrAccessToken() {
        return "";
    }

    @Override
    protected String getPtBrAccessToken() {
        return "2291f2e31fe34a2fab224fd228c39bf2";
    }

    @Override
    protected String getJaAccessToken() {
        return "c82b0a650c9a4758984fb53411f271e4";
    }

    private final String DEV_URL = "https://dev.api.ai/api/";

    @Override
    protected void updateConfig(final AIConfiguration config) {
        config.setServiceUrl(DEV_URL);
    }

    @Test
    public void AIDataServiceDevRuTest() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration("e479d45dc09046c1984d6c546973dd60",
                AIConfiguration.SupportedLanguages.Russian);

        config.setServiceUrl(DEV_URL);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("привет");

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
        assertEquals("helloAction", aiResponse.getResult().getAction());
        assertEquals("Добрый день", aiResponse.getResult().getFulfillment().getSpeech());

    }

    @Test
    public void AIDataServiceDevTest() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        config.setServiceUrl(DEV_URL);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("hello");

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
        assertEquals("greeting", aiResponse.getResult().getAction());
        assertEquals("Hi! How are you?", aiResponse.getResult().getFulfillment().getSpeech());

    }

}
