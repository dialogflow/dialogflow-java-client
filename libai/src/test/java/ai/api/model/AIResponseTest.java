package ai.api.model;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map.Entry;

import ai.api.GsonFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AIResponseTest {

    public static final String TEST_JSON = "{\n" +
            "  \"id\": \"d872e7d9-d2ee-4ebd-aaff-655bfc8fbf33\",\n" +
            "  \"timestamp\": \"2015-03-18T09:54:36.216Z\",\n" +
            "  \"lang\":\"en\",\n" + 
            "  \"result\": {\n" +
            "    \"resolvedQuery\": \"remind feed cat tomorrow 7 am\",\n" +
            "    \"action\": \"task_create\",\n" +
            "    \"parameters\": {\n" +
            "      \"date\": \"\",\n" +
            "      \"date-time\": \"2016-12-21T07:00:00"+(new SimpleDateFormat("Z", Locale.US).format(Calendar.getInstance().getTime()))+"\",\n" +
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

    final static Gson gson = GsonFactory.getDefaultFactory().getGson();

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
}
