package ai.api.model;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.junit.Test;

import java.util.Calendar;

import ai.api.GsonFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AIResponseTest {

    public static final String TEST_JSON = "{\n" +
            "  \"id\": \"d872e7d9-d2ee-4ebd-aaff-655bfc8fbf33\",\n" +
            "  \"timestamp\": \"2015-03-18T09:54:36.216Z\",\n" +
            "  \"result\": {\n" +
            "    \"resolvedQuery\": \"remind feed cat tomorrow 7 am\",\n" +
            "    \"action\": \"task_create\",\n" +
            "    \"parameters\": {\n" +
            "      \"date\": \"\",\n" +
            "      \"date-time\": \"2015-03-19T07:00:00+0600\",\n" +
            "      \"time\": \"\",\n" +
            "      \"text\": \"feed cat\",\n" +
            "      \"priority\": \"\",\n" +
            "      \"remind\": \"remind\",\n" +
            "      \"complex_param\": {\"nested_key\": \"nested_value\"}\n" +
            "    }\n" +
            "  },\n" +
            "  \"status\": {\n" +
            "    \"code\": 200,\n" +
            "    \"errorType\": \"success\"\n" +
            "  }\n" +
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

            assertEquals(2015, dateTimeParameter.get(Calendar.YEAR));
            assertEquals(Calendar.MARCH, dateTimeParameter.get(Calendar.MONTH));
            assertEquals(19, dateTimeParameter.get(Calendar.DATE));
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
}
