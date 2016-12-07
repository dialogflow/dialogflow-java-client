package ai.api.model;

import ai.api.GsonFactory;
import com.google.gson.Gson;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Created by alexey on 06/12/2016.
 */
public class ParamsDateTest {

    private static final String NO_DATE = "{\n" +
            "    \"parameters\": {\n" +
            "      \"date\": \"\"\n" +
            "    }\n" +
            "}";

    private static final String ORIGINAL_DATE = "{\n" +
            "    \"parameters\": {\n" +
            "      \"date\": \"tomorrow\"\n" +
            "    }\n" +
            "}";

    private static final String PARTIAL_DATE = "{\n" +
            "    \"parameters\": {\n" +
            "      \"date\": \"UUUU-04-01\"\n" +
            "    }\n" +
            "}";

    private static final String RECENT_DATE = "{\n" +
            "    \"parameters\": {\n" +
            "      \"date\": \"2016-04-01\"\n" +
            "    }\n" +
            "}";

    final static Gson gson = GsonFactory.getDefaultFactory().getGson();

    @Test
    public void NoDateParams() {
        final Result result = gson.fromJson(NO_DATE, Result.class);
        result.trimParameters();
        assertFalse(result.getParameters().containsKey("date"));
    }

    @Test
    public void OriginalDateParams() {
        final Result result = gson.fromJson(ORIGINAL_DATE, Result.class);
        result.trimParameters();
        assertEquals("tomorrow", result.getParameters().get("date").getAsString());
    }

    @Test
    public void PartialDateParams() {
        final Result result = gson.fromJson(PARTIAL_DATE, Result.class);
        result.trimParameters();
        assertEquals("UUUU-04-01", result.getParameters().get("date").getAsString());
    }

    @Test
    public void RecentDateParams() {
        final Result result = gson.fromJson(RECENT_DATE, Result.class);
        result.trimParameters();
        assertEquals("2016-04-01", result.getParameters().get("date").getAsString());
    }
}
