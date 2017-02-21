package ai.api;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import ai.api.model.AIRequest;

public class AIDataServiceTest {

  @Test
  public void testCustomContext() throws AIServiceException {

    AIConfiguration config = new AIConfiguration("");

    final AtomicReference<String> endpointValue = new AtomicReference<>();
    final AtomicReference<String> requestJsonValue = new AtomicReference<>();

    AIDataService dataService = new AIDataService(config) {

      @Override
      protected String doTextRequest(String endpoint, String requestJson,
          Map<String, String> additionalHeaders) throws MalformedURLException, AIServiceException {
        endpointValue.set(endpoint);
        requestJsonValue.set(requestJson);
        return "{}";
      }

    };
    AIServiceContext customContext = new AIServiceContextBuilder()
        .setSessionId("customSessionId")
        .setSessionId(TimeZone.getTimeZone("Europe/London"))
        .build();

    String defaultTimeZoneID = Calendar.getInstance().getTimeZone().getID();
    AIRequest request;

    // Timezone is undefined
    request = new AIRequest();
    dataService.request(request);
    assertTrue(endpointValue.get().indexOf("sessionId=customSessionId") == -1);
    assertEquals(defaultTimeZoneID, request.getTimezone());
    assertTrue(requestJsonValue.get().indexOf(defaultTimeZoneID) > 0);
    assertTrue(requestJsonValue.get().indexOf("Europe/London") == -1);
    
    // Timezone is defined in custom context
    request = new AIRequest();
    dataService.request(request, customContext);
    assertTrue(endpointValue.get().indexOf("sessionId=customSessionId") > 0);
    assertEquals("Europe/London", request.getTimezone());
    assertTrue(requestJsonValue.get().indexOf("Europe/London") > 0);
    assertTrue(requestJsonValue.get().indexOf(defaultTimeZoneID) == -1);
    
    // Timezone is defined in request and custom context
    request = new AIRequest();
    request.setTimezone("GMT");
    dataService.request(request, customContext);
    assertTrue(endpointValue.get().indexOf("sessionId=customSessionId") > 0);
    assertEquals("GMT", request.getTimezone());
    assertTrue(requestJsonValue.get().indexOf("Europe/London") == -1);
    assertTrue(requestJsonValue.get().indexOf(defaultTimeZoneID) == -1);
    assertTrue(requestJsonValue.get().indexOf("GMT") > 0);
  }
}
