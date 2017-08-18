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
 
package ai.api.test;

import ai.api.*;
import ai.api.model.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import ai.api.util.StringUtils;

import org.junit.Test;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public abstract class ProtocolTestBase {

    protected abstract String getAccessToken();

    protected abstract String getSecondAccessToken();

    protected abstract String getRuAccessToken();

    protected abstract String getBrAccessToken();

    protected abstract String getPtBrAccessToken();

    protected abstract String getJaAccessToken();

    protected ProtocolTestBase() {
    }

    @Test
    public void textRequestTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("Hello");

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
        assertEquals("greeting", aiResponse.getResult().getAction());
        assertEquals("Hi! How are you?", aiResponse.getResult().getFulfillment().getSpeech());

    }

    private AIDataService createDataService() {
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        return new AIDataService(config);
    }

    private AIDataService createDataService(final String accessToken) {
        final AIConfiguration config = new AIConfiguration(accessToken,
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        return new AIDataService(config);
    }

    @Test(expected=AIServiceException.class)
    public void voiceRequestTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();

        final InputStream inputStream = getClass().getClassLoader().getResourceAsStream("what_is_your_name.raw");

        final AIResponse aiResponse = aiDataService.voiceRequest(inputStream);
        assertNotNull(aiResponse);
        assertFalse(aiResponse.getStatus().getErrorDetails(), aiResponse.isError());
        assertFalse(StringUtils.isEmpty(aiResponse.getId()));
        assertNotNull(aiResponse.getResult());

        final String resolvedQuery = aiResponse.getResult().getResolvedQuery();
        assertFalse(StringUtils.isEmpty(resolvedQuery));
        assertTrue(resolvedQuery.contains("what is your"));
        assertTrue(resolvedQuery.contains("name"));
    }

    @Test
    public void inputContextTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();

        final AIRequest aiRequest = new AIRequest("Hello");

        AIResponse aiResponse;
        String action;

        cleanContexts(aiDataService);

        aiResponse = makeRequest(aiDataService, aiRequest);
        action = aiResponse.getResult().getAction();
        assertEquals("greeting", action);

        aiRequest.addContext(new AIContext("firstContext"));
        aiResponse = makeRequest(aiDataService, aiRequest);
        action = aiResponse.getResult().getAction();
        assertEquals("firstGreeting", action);

        aiRequest.setResetContexts(true);
        aiRequest.setContexts(null);
        aiRequest.addContext(new AIContext("secondContext"));
        aiResponse = makeRequest(aiDataService, aiRequest);
        action = aiResponse.getResult().getAction();
        assertEquals("secondGreeting", action);

        cleanContexts(aiDataService);
    }

    @Test
    public void outputContextTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("weather");

        cleanContexts(aiDataService);

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);
        final String action = aiResponse.getResult().getAction();
        assertEquals("showWeather", action);
        assertNotNull(aiResponse.getResult().getContexts());

        assertContainsContext(aiResponse, "weather");
    }

    private void assertContainsContext(final AIResponse aiResponse, final String contextName) {
        boolean contextExist = false;
        for (final AIOutputContext outputContext : aiResponse.getResult().getContexts()) {
            if (outputContext.getName().equalsIgnoreCase(contextName)) {
                contextExist = true;
            }
        }
        assertTrue(contextExist);
    }

    private void assertNotContainsContext(final AIResponse aiResponse, final String contextName) {
        boolean contextExist = false;
        for (final AIOutputContext outputContext : aiResponse.getResult().getContexts()) {
            if (outputContext.getName().equalsIgnoreCase(contextName)) {
                contextExist = true;
            }
        }
        assertFalse(contextExist);
    }

    @Test
    public void differentAgentsTest() throws AIServiceException {

        final String query = "I want pizza";

        {
            final AIDataService aiDataService = createDataService();

            final AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery(query);

            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertNotNull(aiResponse.getResult());
            assertEquals("pizza", aiResponse.getResult().getAction());

        }

        {
            final AIConfiguration secondConfig = new AIConfiguration(getSecondAccessToken(),
                    AIConfiguration.SupportedLanguages.English);

            updateConfig(secondConfig);

            final AIDataService aiDataService = new AIDataService(secondConfig);

            final AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery(query);

            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertNotNull(aiResponse.getResult());
            assertTrue(StringUtils.isEmpty(aiResponse.getResult().getAction()));
        }
    }

    @Test
    public void sessionTest() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        final AIDataService firstService = new AIDataService(config);
        final AIDataService secondService = new AIDataService(config, firstService.getContext());

        {
            final AIRequest weatherRequest = new AIRequest();
            weatherRequest.setQuery("weather");
            final AIResponse weatherResponse = makeRequest(firstService, weatherRequest);
            assertNotNull(weatherResponse);
        }

        {
            final AIRequest checkSecondRequest = new AIRequest();
            checkSecondRequest.setQuery("check weather");
            final AIResponse checkSecondResponse = makeRequest(secondService, checkSecondRequest);
            assertNotNull(checkSecondResponse.getResult().getAction());
        }

        {
            final AIRequest checkFirstRequest = new AIRequest();
            checkFirstRequest.setQuery("check weather");
            final AIResponse checkFirstResponse = makeRequest(firstService, checkFirstRequest);
            assertNotNull(checkFirstResponse.getResult().getAction());
            assertTrue(checkFirstResponse.getResult().getAction().equalsIgnoreCase("checked"));
        }
    }

    @Test
    public void testParameters() throws AIServiceException {

        final AIDataService aiDataService = createDataService();
        final AIResponse response = aiDataService.request(new AIRequest("what is your name"));

        assertNotNull(response.getResult().getParameters());
        assertFalse(response.getResult().getParameters().isEmpty());

        final AIOutputContext context = response.getResult().getContexts().get(0);
        assertNotNull(context.getParameters());

        {
            assertTrue(context.getParameters().containsKey("param"));
            final JsonElement contextParam = context.getParameters().get("param");
            assertEquals("blabla", contextParam.getAsString());
        }

        {
            assertTrue(context.getParameters().containsKey("my_name"));
            final JsonElement contextParam = context.getParameters().get("my_name");
            assertEquals("Sam", contextParam.getAsString());
        }

    }

    @Test
    public void testRussianLanguage() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getRuAccessToken(),
                AIConfiguration.SupportedLanguages.Russian);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest("привет");
        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));

        assertEquals("helloAction", aiResponse.getResult().getAction());
        assertEquals("Добрый день", aiResponse.getResult().getFulfillment().getSpeech());
    }

    @Test
    public void testBrazilLanguage() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getPtBrAccessToken(),
                AIConfiguration.SupportedLanguages.PortugueseBrazil);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("oi");

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));

        assertEquals("helloAction", aiResponse.getResult().getAction());
        assertEquals("como você está", aiResponse.getResult().getFulfillment().getSpeech());
    }

    @Test
    public void errorTextRequestTest() {
        final AIConfiguration config = new AIConfiguration("WRONG_ACCESS_TOKEN",
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("Hello");

        try {
            aiDataService.request(aiRequest);
            assertTrue("Method should produce exception", false);
        } catch (final AIServiceException e) {
            assertNotNull(e.getResponse());
            assertEquals("unauthorized", e.getResponse().getStatus().getErrorType());
            assertEquals("Authorization failed. Please check your access keys.", e.getMessage());
        }
    }

    @Test
    public void resetContextsTest() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);

        {
            final AIRequest aiRequest = new AIRequest("what is your name");

            final AIResponse aiResponse = aiDataService.request(aiRequest);
            assertContainsContext(aiResponse, "name_question");

            aiDataService.resetActiveContexts();
        }

        {
            final AIRequest aiRequest = new AIRequest("hello");
            final AIResponse aiResponse = aiDataService.request(aiRequest);
            assertNotNull(aiResponse);
            assertFalse(aiResponse.getStatus().getErrorDetails(), aiResponse.isError());
            assertNotContainsContext(aiResponse, "name_question");
        }

    }

    @Test
    public void requestEntitiesTest() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);
        final AIDataService secondService = new AIDataService(config, aiDataService.getContext());

        {
            final AIRequest aiRequest = new AIRequest();
            aiRequest.setQuery("hi nori");

            final Entity dwarfsEntity = createDwarfsEntity();

            final List<Entity> extraEntities = Collections.singletonList(dwarfsEntity);

            aiRequest.setEntities(extraEntities);

            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Ori", aiResponse.getResult().getFulfillment().getSpeech());

        }

        {
            // check entities also work in another instance
            final AIRequest secondRequest = new AIRequest("hi nori");
            final AIResponse secondResponse = makeRequest(aiDataService, secondRequest);

            assertFalse(StringUtils.isEmpty(secondResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", secondResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Ori", secondResponse.getResult().getFulfillment().getSpeech());
        }

        // check previous entities overwritten
        {
            final AIRequest aiRequest = new AIRequest("hi dwalin");
            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertTrue(StringUtils.isEmpty(aiResponse.getResult().getAction()));
            assertTrue(StringUtils.isEmpty(aiResponse.getResult().getFulfillment().getSpeech()));
        }

        // check entities work in another instance

        {
            final AIRequest aiRequest = new AIRequest("hi nori");
            final AIResponse aiResponse = makeRequest(secondService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Ori", aiResponse.getResult().getFulfillment().getSpeech());
        }

        {
            final AIRequest aiRequest = new AIRequest("hi dwalin");
            final AIResponse aiResponse = makeRequest(secondService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertTrue(StringUtils.isEmpty(aiResponse.getResult().getAction()));
            assertTrue(StringUtils.isEmpty(aiResponse.getResult().getFulfillment().getSpeech()));
        }
    }


    @Test(expected = AIServiceException.class)
    public void wrongRequestEntitiesTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("hi Bofur");

        final Entity notDwarfs = createDwarfsEntity();
        notDwarfs.setName("not_dwarfs");

        final ArrayList<Entity> extraEntities = new ArrayList<>();
        extraEntities.add(notDwarfs);
        aiRequest.setEntities(extraEntities);

        makeRequest(aiDataService, aiRequest);
    }

    @Test
    public void userEntitiesTest() throws AIServiceException{
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);
        final AIDataService secondDataService = new AIDataService(config, aiDataService.getContext());

        final Entity dwarfsEntity = createDwarfsEntity();

        final AIResponse uploadResult = aiDataService.uploadUserEntity(dwarfsEntity);
        assertNotNull(uploadResult);
        assertFalse(uploadResult.isError());

        {
            final AIRequest aiRequest = new AIRequest("hi nori");
            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Ori", aiResponse.getResult().getFulfillment().getSpeech());

            // check entities working for session, not for one request
            final AIRequest secondRequest = new AIRequest("hi bombur");
            final AIResponse secondResponse = makeRequest(aiDataService, secondRequest);

            assertFalse(StringUtils.isEmpty(secondResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", secondResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am bifur", secondResponse.getResult().getFulfillment().getSpeech());
        }

        {
            // check entities changed for another instance

            final AIRequest aiRequest = new AIRequest("hi bombur");
            final AIResponse aiResponse = makeRequest(secondDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am bifur", aiResponse.getResult().getFulfillment().getSpeech());
        }

    }

    @Test(expected = AIServiceException.class)
    public void userEntitiesEmptyCollectionTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();
        aiDataService.uploadUserEntities(Collections.<Entity>emptyList());
    }

    @Test
    public void userEntitiesCollectionTest() throws AIServiceException {
        final AIConfiguration config = new AIConfiguration(getAccessToken(),
                AIConfiguration.SupportedLanguages.English);

        updateConfig(config);

        final AIDataService aiDataService = new AIDataService(config);
        final AIDataService secondDataService = new AIDataService(config, aiDataService.getContext());

        final Entity dwarfsEntity = createDwarfsEntity();
        final Entity hobbitsEntity = createHobbitsEntity();

        final AIResponse uploadResult = aiDataService.uploadUserEntities(Arrays.asList(dwarfsEntity, hobbitsEntity));
        assertNotNull(uploadResult);
        assertFalse(uploadResult.isError());

        // check first entity
        {
            final AIRequest aiRequest = new AIRequest("hi nori");
            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Ori", aiResponse.getResult().getFulfillment().getSpeech());

            // check entities working for session, not for one request
            final AIRequest secondRequest = new AIRequest("hi bombur");
            final AIResponse secondResponse = makeRequest(aiDataService, secondRequest);

            assertFalse(StringUtils.isEmpty(secondResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", secondResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am bifur", secondResponse.getResult().getFulfillment().getSpeech());
        }

        // check second entity
        {
            final AIRequest aiRequest = new AIRequest("hi Brandybuck");
            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("hi_hobbit", aiResponse.getResult().getAction());
            assertEquals("Hi, Gendalf! I am Meriadoc", aiResponse.getResult().getFulfillment().getSpeech());

            // check entities working for session, not for one request
            final AIRequest secondRequest = new AIRequest("hi peregrin");
            final AIResponse secondResponse = makeRequest(aiDataService, secondRequest);

            assertFalse(StringUtils.isEmpty(secondResponse.getResult().getResolvedQuery()));
            assertEquals("hi_hobbit", secondResponse.getResult().getAction());
            assertEquals("Hi, Gendalf! I am Peregrin", secondResponse.getResult().getFulfillment().getSpeech());
        }

        {
            // check entities was changed in another instance

            final AIRequest aiRequest = new AIRequest("hi bombur");
            final AIResponse aiResponse = makeRequest(secondDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am bifur", aiResponse.getResult().getFulfillment().getSpeech());
        }

    }

    @Test
    public void extendUserEntitiesTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();
        final Entity dwarfsEntity = createDwarfsEntity();
        dwarfsEntity.setExtend(true);

        final AIResponse uploadResult = aiDataService.uploadUserEntity(dwarfsEntity);
        assertNotNull(uploadResult);
        assertFalse(uploadResult.isError());

        {
            final AIRequest aiRequest = new AIRequest("hi nori");
            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Ori", aiResponse.getResult().getFulfillment().getSpeech());
        }

        {
            // check original entity values not changed
            final AIRequest aiRequest = new AIRequest("hi dwalin");
            final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

            assertFalse(StringUtils.isEmpty(aiResponse.getResult().getResolvedQuery()));
            assertEquals("say_hi", aiResponse.getResult().getAction());
            assertEquals("hi Bilbo, I am Balin", aiResponse.getResult().getFulfillment().getSpeech());
        }

    }

    @Test(expected = AIServiceException.class)
    public void wrongUserEntitiesTest() throws AIServiceException{
        final AIDataService aiDataService = createDataService();

        final Entity myDwarfs = createDwarfsEntity();
        myDwarfs.setName("notDwarfs");
        aiDataService.uploadUserEntity(myDwarfs);
    }

    @Test
    public void inputContextWithParametersTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();

        final AIContext weatherContext = new AIContext("weather");
        weatherContext.setParameters(Collections.singletonMap("location", "London"));

        final List<AIContext> contexts = Collections.singletonList(weatherContext);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("and for tomorrow");
        aiRequest.setContexts(contexts);

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertEquals("Weather in London for tomorrow", aiResponse.getResult().getFulfillment().getSpeech());
    }

    @Test
    public void contextWithLifespanTest() throws AIServiceException{
        final AIDataService aiDataService = createDataService();

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("weather in london");

        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertEquals(5, aiResponse.getResult().getContext("weather").getLifespan().intValue());
        assertEquals(2, aiResponse.getResult().getContext("shortContext").getLifespan().intValue());
        assertEquals(10, aiResponse.getResult().getContext("longContext").getLifespan().intValue());

        // check if contexts live as much time as it must
        AIResponse nextResponse = null;

        for (int i = 0; i < 3; i++) {
            nextResponse = makeRequest(aiDataService, new AIRequest("another request"));
        }

        assertNull(nextResponse.getResult().getContext("shortContext"));
        assertNotNull(nextResponse.getResult().getContext("weather"));
        assertNotNull(nextResponse.getResult().getContext("longContext"));

        for (int i = 0; i < 3; i++) {
            nextResponse = makeRequest(aiDataService, new AIRequest("another request"));
        }

        assertNull(nextResponse.getResult().getContext("shortContext"));
        assertNull(nextResponse.getResult().getContext("weather"));
        assertNotNull(nextResponse.getResult().getContext("longContext"));
    }

    @Test
    public void inputContextWithLifespanTest() throws AIServiceException {
        final int lifespan = 3;
        final AIDataService aiDataService = createDataService();

        final AIContext weatherContext = new AIContext("weather");
        weatherContext.setParameters(Collections.singletonMap("location", "London"));
        weatherContext.setLifespan(lifespan);

        final AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery("and for tomorrow");
        aiRequest.setContexts(Collections.singletonList(weatherContext));

        AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        assertEquals("Weather in London for tomorrow", aiResponse.getResult().getFulfillment().getSpeech());

        for (int i = 0; i < lifespan - 1; i++) {
            assertNotNull(String.format("Request #%d failed", i + 1), aiResponse.getResult().getContext("weather"));
            aiResponse = makeRequest(aiDataService, new AIRequest("next request"));
        }
        assertNull(aiResponse.getResult().getContext("weather"));
    }

    @Test
    public void compositeEntitiesTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService();
        final AIRequest aiRequest = new AIRequest("hello remind me to feed cat");
        final AIResponse aiResponse = makeRequest(aiDataService, aiRequest);

        final String stringParam = aiResponse.getResult().getStringParameter("greeting");
        assertEquals("hello", stringParam);

        final String emptyParam = aiResponse.getResult().getStringParameter("pet", null);
        assertNull(emptyParam);

        final JsonObject jsonParam = aiResponse.getResult().getComplexParameter("action");
        assertNotNull(jsonParam);

        final String actionPet = jsonParam.get("pet").getAsString();
        assertEquals("cat", actionPet);
    }

    // TODO Fix test
    //@Test
    public void testUserEnumEntities() throws AIServiceException {
        final AIDataService dataService = createDataService();

        final String requestText = "I want milk";
        final AIRequest request = new AIRequest(requestText);

        final Entity productsListEntity = new Entity("productsList");
        productsListEntity.setIsEnum(true);
        productsListEntity.addEntry(new EntityEntry("@productsFood:productId"));
        request.setEntities(Collections.singletonList(productsListEntity));

        final AIResponse aiResponse = makeRequest(dataService, request);

        assertFalse(aiResponse.getResult().getParameters().isEmpty());
        final JsonObject productParameter = aiResponse.getResult().getComplexParameter("product");
        assertEquals("milk", productParameter.get("productId").getAsString());
    }

    @Test
    public void testSourceField() throws AIServiceException {
        final AIDataService aiDataService = createDataService("23e7d37f6dd24e4eb7dbbd7491f832cf");

        final AIRequest domainsRequest = new AIRequest("hi");
        final AIResponse domainsResponse = makeRequest(aiDataService, domainsRequest);

        assertEquals("domains", domainsResponse.getResult().getSource());
        assertEquals("smalltalk.greetings.hello", domainsResponse.getResult().getAction());

        final AIRequest agentRequest = new AIRequest("not from domains");
        final AIResponse agentResponse = makeRequest(aiDataService, agentRequest);

        assertEquals("agent", agentResponse.getResult().getSource());
        assertEquals("Yes, it is not from domains", agentResponse.getResult().getFulfillment().getSpeech());
    }
    
    @Test
	public void activeContextsTest() throws AIServiceException {
	    final AIDataService aiDataService = createDataService();
		
	    List<AIContext> contexts = aiDataService.getActiveContexts();
	    assertEquals(0, contexts.size());
	    
	    String name = aiDataService.addActiveContext(createAIContext("context1"));
	    assertEquals("context1", name);
	    
	    contexts = aiDataService.getActiveContexts();
	    assertEquals(1, contexts.size());
	    assertEquals("context1", contexts.get(0).getName());
	    
	    AIContext context = aiDataService.getActiveContext("context1");
	    assertEquals("context1", context.getName());
	    
	    assertTrue(aiDataService.removeActiveContext("context1"));
	    assertFalse(aiDataService.removeActiveContext("context1"));
	    
	    assertNull(aiDataService.getActiveContext("context1"));
	}
	
	@Test
	public void resetActiveContextsTest() throws AIServiceException {
	    final AIDataService aiDataService = createDataService();
	
	    List<String> names = aiDataService.addActiveContext(Arrays.asList(
	    		createAIContext("context1"), createAIContext("context2")));
	    assertTrue(Arrays.asList("context1", "context2").equals(names));
	    
	    List<AIContext> contexts = aiDataService.getActiveContexts();
	    assertEquals(2, contexts.size());
	    
	    aiDataService.resetActiveContexts();
	    contexts = aiDataService.getActiveContexts();
	    assertEquals(0, contexts.size());
	}
	
	private AIContext createAIContext(String name) {
		return new AIContext(name);
	}

    //@Test TODO
    public void locationFieldTest() throws AIServiceException {
        final AIDataService aiDataService = createDataService("23e7d37f6dd24e4eb7dbbd7491f832cf");

        // no location means empty weather
        final AIRequest emptyLocationRequest = new AIRequest("weather");
        final AIResponse emptyLocationResponse = makeRequest(aiDataService, emptyLocationRequest);
        assertTrue(StringUtils.isEmpty(emptyLocationResponse.getResult().getFulfillment().getSpeech()));

        // location can be set using RequestExtras
        final RequestExtras requestExtras = new RequestExtras();
        requestExtras.setLocation(new Location(55.05, 82.95));
        final AIResponse extrasResponse = aiDataService.request(emptyLocationRequest, requestExtras);
        assertNotNull(extrasResponse);
        assertNotNull(extrasResponse.getResult().getFulfillment().getSpeech());
        assertTrue(extrasResponse.getResult().getFulfillment().getSpeech().contains("Novosibirsk"));
        assertTrue(extrasResponse.getResult().getFulfillment().getSpeech().contains("degree"));

        // location can be set explicitly
        final AIRequest locationRequest = new AIRequest("weather");
        locationRequest.setLocation(new Location(55.05, 82.95));
        final AIResponse locationResponse = makeRequest(aiDataService, locationRequest);

        assertNotNull(locationResponse);
        assertNotNull(locationResponse.getResult().getFulfillment().getSpeech());
        assertTrue(locationResponse.getResult().getFulfillment().getSpeech().contains("Novosibirsk"));
        assertTrue(locationResponse.getResult().getFulfillment().getSpeech().contains("degree"));

    }

    private Entity createHobbitsEntity() {
        final Entity hobbits = new Entity("hobbits");
        hobbits.addEntry(new EntityEntry("Meriadoc", new String[]{"Brandybuck", "Merry"}));
        hobbits.addEntry(new EntityEntry("Peregrin", new String[]{"Took", "Peregrin", "Pippin"}));
        return hobbits;
    }

    private Entity createDwarfsEntity() {
        final Entity dwarfs = new Entity("dwarfs");
        dwarfs.addEntry(new EntityEntry("Ori", new String[]{"ori", "Nori"}));
        dwarfs.addEntry(new EntityEntry("bifur", new String[]{"Bofur", "Bombur"}));
        return dwarfs;
    }

    protected void updateConfig(final AIConfiguration config) {

    }

    /**
     * Cleanup contexts to prevent Tests correlation
     */
    protected void cleanContexts(final AIDataService aiDataService) throws AIServiceException {
        aiDataService.resetActiveContexts();
    }

    protected AIResponse makeRequest(final AIDataService aiDataService, final AIRequest aiRequest) throws AIServiceException {
        final AIResponse aiResponse = aiDataService.request(aiRequest);
        assertNotNull(aiResponse);
        assertFalse(aiResponse.getStatus().getErrorDetails(), aiResponse.isError());
        assertFalse(StringUtils.isEmpty(aiResponse.getId()));
        assertNotNull(aiResponse.getResult());
        return aiResponse;
    }

    protected void assertArrayContains(final String expected, final String[] array) {
        assertNotNull(array);
        boolean exist = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(expected)) {
                exist = true;
            }
        }
        assertTrue(exist);
    }

    protected void assertArrayNotContains(final String expected, final String[] array) {
        assertNotNull(array);
        boolean exist = false;
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(expected)) {
                exist = true;
            }
        }
        assertFalse(exist);
    }
}
