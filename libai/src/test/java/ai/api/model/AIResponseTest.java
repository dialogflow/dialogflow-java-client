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

import ai.api.model.ResponseMessage.ResponsePayload;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import ai.api.GsonFactory;
import ai.api.util.ParametersConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AIResponseTest {

    public static final String TEST_JSON;

    final static Gson gson = GsonFactory.getDefaultFactory().getGson();

    static {
      Calendar testTime = Calendar.getInstance(Locale.US);
      testTime.set(2016, Calendar.DECEMBER, 21, 7, 0, 0);

      DateFormat DATE_TIME_FORMAT =
          new SimpleDateFormat(ParametersConverter.PROTOCOL_DATE_TIME_FORMAT, Locale.US);

      TEST_JSON = "{\n" +
          "  \"id\": \"d872e7d9-d2ee-4ebd-aaff-655bfc8fbf33\",\n" +
          "  \"timestamp\": \"2015-03-18T09:54:36.216Z\",\n" +
          "  \"lang\":\"en\",\n" + 
          "  \"result\": {\n" +
          "    \"resolvedQuery\": \"remind feed cat tomorrow 7 am\",\n" +
          "    \"action\": \"task_create\",\n" +
          "    \"parameters\": {\n" +
          "      \"date\": \"\",\n" +
          "      \"date-time\": \""+DATE_TIME_FORMAT.format(testTime.getTime())+"\",\n" +
          "      \"time\": \"\",\n" +
          "      \"text\": \"feed cat\",\n" +
          "      \"priority\": \"\",\n" +
          "      \"remind\": \"remind\",\n" +
          "      \"complex_param\": {\"nested_key\": \"nested_value\"}\n" +
          "    },\n" +
          "    \"score\":0.875\n" +
          "  },\n" +
          "  \"status\": {\n" +
          "    \"code\": 200,\n" +
          "    \"errorType\": \"success\"\n" +
          "  },\n" +
          "  \"sessionId\":\"0123456789\"\n" +
          "}";
    }

    @Test
    public void trimParametersTest() {
        final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
        aiResponse.cleanup();

        assertFalse(aiResponse.getResult().getParameters().containsKey("date"));
        assertFalse(aiResponse.getResult().getParameters().containsKey("time"));
        assertFalse(aiResponse.getResult().getParameters().containsKey("priority"));

        assertTrue(aiResponse.getResult().getParameters().containsKey("date-time"));
        assertTrue(aiResponse.getResult().getParameters().containsKey("text"));
        assertTrue(aiResponse.getResult().getParameters().containsKey("remind"));

    }

    @Test
    public void getDateParameterTest() {
        final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);

        try {
        	final Calendar dateTimeParameter = Calendar.getInstance();
        	dateTimeParameter.setTime(aiResponse.getResult().getDateTimeParameter("date-time"));

            assertEquals(2016, dateTimeParameter.get(Calendar.YEAR));
            assertEquals(Calendar.DECEMBER, dateTimeParameter.get(Calendar.MONTH));
            assertEquals(21, dateTimeParameter.get(Calendar.DATE));
            assertEquals(7, dateTimeParameter.get(Calendar.HOUR_OF_DAY));
            assertEquals(0, dateTimeParameter.get(Calendar.MINUTE));

        } catch (final Exception e) {
            assertTrue(e.getMessage(), false);
        }
    }

    @Test
    public void getComplexParameterTest(){
        final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);

        final JsonObject jsonObject = aiResponse.getResult().getComplexParameter("complex_param");

        assertNotNull(jsonObject);
        assertNotNull(jsonObject.get("nested_key"));
        assertEquals("nested_value", jsonObject.get("nested_key").getAsString());
    }
    
    @Test
    public void getSessionIdTest() {
    	final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
    	assertEquals("0123456789", aiResponse.getSessionId());
    }
    
    @Test
    public void getIdTest() {
    	final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
    	assertEquals("d872e7d9-d2ee-4ebd-aaff-655bfc8fbf33", aiResponse.getId());
    }
    
    @Test
    public void getTimestampTest() {
    	final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(2015, 2, 18, 9, 54, 36);
    	calendar.set(Calendar.MILLISECOND, 216);
    	assertEquals( calendar.getTime(), aiResponse.getTimestamp());
    }
    
    @Test
    public void getLangTest() {
        final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
        assertEquals("en", aiResponse.getLang());
    }
    
    @Test
    public void getResultScoreTest() {
    	final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
    	assertEquals(0.875, aiResponse.getResult().getScore(), 1e-6);
    }
    
    @Test
    public void ResponseToStringTest() {
    	final AIResponse aiResponse = gson.fromJson(TEST_JSON, AIResponse.class);
    	assertEquals("AIResponse{id='d872e7d9-d2ee-4ebd-aaff-655bfc8fbf33', "
    			+ "timestamp=Wed Mar 18 09:54:36 "+(new SimpleDateFormat("zzz").format(new Date()))+" 2015, result=Result {action='task_create', resolvedQuery='remind feed cat tomorrow 7 am'}, "
    			+ "status=Status{code=200, errorType='success', errorDetails='null'}, sessionId=0123456789}",
    			aiResponse.toString());
    }
    
    @Test
    public void ResponseToStringTest2() {
        final AIResponse response = gson.fromJson(TEST_JSON, AIResponse.class);
        for (Entry<String,JsonElement> parameter : response.getResult().getParameters().entrySet()) {
          System.out.printf("%s : %s%n", parameter.getKey(), parameter.getValue());
        }
    }

    @Test
    public void complexResponseTest(){
      final String responseJson = "{\n"
          + "  \"id\": \"b5765188-227c-465f-abc4-38674d18e1b6\",\n"
          + "  \"timestamp\": \"2017-07-19T21:24:40.661Z\",\n"
          + "  \"lang\": \"en\",\n"
          + "  \"result\": {\n"
          + "    \"source\": \"agent\",\n"
          + "    \"resolvedQuery\": \"hi\",\n"
          + "    \"action\": \"\",\n"
          + "    \"actionIncomplete\": false,\n"
          + "    \"parameters\": {},\n"
          + "    \"contexts\": [],\n"
          + "    \"metadata\": {\n"
          + "      \"intentId\": \"df4522d6-5222-4bab-96e1-2218b4a12561\",\n"
          + "      \"webhookUsed\": \"false\",\n"
          + "      \"webhookForSlotFillingUsed\": \"false\",\n"
          + "      \"intentName\": \"hi\"\n"
          + "    },\n"
          + "    \"fulfillment\": {\n"
          + "      \"speech\": \"Hi there!\",\n"
          + "      \"messages\": [\n"
          + "        {\n"
          + "          \"type\": 0,\n"
          + "          \"speech\": \"Hi there!\"\n"
          + "        },\n"
          + "        {\n"
          + "          \"type\": 4,\n"
          + "          \"payload\": {\n"
          + "            \"native-android\": {\n"
          + "              \"quick-responses\": [\n"
          + "                \"Yes\",\n"
          + "                \"No\",\n"
          + "                \"I don't know\"\n"
          + "              ]\n"
          + "            }\n"
          + "          }\n"
          + "        }\n"
          + "      ]\n"
          + "    },\n"
          + "    \"score\": 1\n"
          + "  },\n"
          + "  \"status\": {\n"
          + "    \"code\": 200,\n"
          + "    \"errorType\": \"success\"\n"
          + "  },\n"
          + "  \"sessionId\": \"9bdd9303-4937-4464-82f7-3d260d7283c3\"\n"
          + "}";

      final AIResponse aiResponse = gson.fromJson(responseJson, AIResponse.class);

      List<ResponseMessage> messages = aiResponse.getResult().getFulfillment().getMessages();

      for (ResponseMessage message : messages) {
        if (message instanceof ResponsePayload) {
          final ResponsePayload customPayload = (ResponsePayload)message;

          final JsonObject payload = customPayload.getPayload();

          final JsonObject androidPayload = payload.getAsJsonObject("native-android");
          final JsonArray quickResponses = androidPayload.getAsJsonArray("quick-responses");

          JsonElement firstReply = quickResponses.get(0);
          assertEquals("Yes", firstReply.getAsString());

          JsonElement secondReply = quickResponses.get(1);
          assertEquals("No", secondReply.getAsString());

          JsonElement thirdReply = quickResponses.get(2);
          assertEquals("I don't know", thirdReply.getAsString());
        }
      }
    }
}
