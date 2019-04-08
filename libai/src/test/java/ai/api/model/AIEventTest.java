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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import ai.api.GsonFactory;

/**
 * Created by alexey on 07/12/2016.
 */
public class AIEventTest {

    private static final String EMPTY_EVENT = "{\n" +
            "    \"name\": \"test\",\n" +
            "    \"data\": {}\n" +
            "}";

    private static final String ONE_EVENT = "{\n" +
            "    \"name\": \"joke\",\n" +
            "    \"data\": {" +
            "      \"poker\": \"face\""+
            "    }\n" +
            "}";
    
    private static final String ONE_EVENT_COLLECTION = "{\n" +
            "    \"name\": \"joke\",\n" +
            "    \"data\": {" +
            "      \"poker\": [\"face\",\"hand\"] "+
            "    }\n" +
            "}";

    private static final String EVENTS_ARRAY = "{\n" +
            "    \"name\": \"test\",\n" +
            "    \"data\": {" +
            "      \"heart\": \"ace\","+
            "      \"diamond\": \"King\","+
            "      \"club\": \"queen\","+
            "      \"spade\": \"Jack\","+
            "      \"poker\": [\"face\",\"hand\"] "+
            "    }\n" +
            "}";


    final static Gson gson = GsonFactory.getDefaultFactory().getGson();

    @Test
    public void EmptyEvent() {
        final AIEvent aiEvent = gson.fromJson(EMPTY_EVENT, AIEvent.class);
        assertEquals("test", aiEvent.getName());
        assertFalse(aiEvent.getData().containsKey("data"));
    }

    @Test
    public void OneEvent() {
        final AIEvent aiEvent = gson.fromJson(ONE_EVENT, AIEvent.class);
        assertEquals("joke", aiEvent.getName());
        assertEquals("face", aiEvent.getDataField("poker"));
    }
    
    @Test
    public void OneEventCollection() {
        final AIEvent aiEvent = gson.fromJson(ONE_EVENT_COLLECTION, AIEvent.class);
        List<String> pokerBits = new ArrayList<>();
        pokerBits.add("face");
        pokerBits.add("hand");
        assertEquals("joke", aiEvent.getName());
        assertEquals("[\"face\",\"hand\"]", aiEvent.getDataField("poker"));
    }

    @Test
    public void ListEvents() {
        final AIEvent aiEvent = gson.fromJson(EVENTS_ARRAY, AIEvent.class);
        assertEquals("test", aiEvent.getName());
        assertEquals("ace", aiEvent.getDataField("heart"));
        assertEquals("King", aiEvent.getDataField("diamond"));
        assertEquals("queen", aiEvent.getDataField("club"));
        assertEquals("Jack", aiEvent.getDataField("spade"));
        assertEquals("[\"face\",\"hand\"]", aiEvent.getDataField("poker"));
    }

    @Test
    public void CreateEmptyEvent() {
        final AIEvent aiEvent = new AIEvent("test");
        aiEvent.setData(new HashMap<String, JsonElement>());
        assertEquals("{\"name\":\"test\",\"data\":{}}", gson.toJson(aiEvent));
    }

    @Test
    public void CreateOneEvent() {
        final AIEvent aiEvent = new AIEvent("test");
        aiEvent.addDataField("heart", "ace");
        assertEquals("{\"name\":\"test\",\"data\":{\"heart\":\"ace\"}}", gson.toJson(aiEvent));
    }

    @Test
    public void CreateListEvents() {
        final AIEvent aiEvent = new AIEvent("test");
        final LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("diamond", "King");
        linkedHashMap.put("club", "queen");
        aiEvent.addDataField(linkedHashMap);
        aiEvent.addDataField("heart", "ace");
        assertEquals("King", aiEvent.getDataField("diamond"));
        assertEquals("queen", aiEvent.getDataField("club"));
        assertEquals("ace", aiEvent.getDataField("heart"));
    }

    @Test
    public void CreateAIRequest() {
        final AIRequest aiRequest = new AIRequest();
        aiRequest.setContexts(new ArrayList<AIContext>());
        aiRequest.setQuery(null);
        aiRequest.setConfidence(new float[]{0.5f, 9.6f});
        final AIEvent aiEvent = new AIEvent("test");
        aiEvent.addDataField("heart", "ace");
        aiRequest.setEvent(aiEvent);
        assertEquals("{\"confidence\":[0.5,9.6],\"contexts\":[],\"event\":{\"name\":\"test\",\"data\":{\"heart\":\"ace\"}}}", gson.toJson(aiRequest));
    }
}
