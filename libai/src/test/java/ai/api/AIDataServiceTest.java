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
 
package ai.api;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

import org.junit.Test;

import ai.api.model.AIOriginalRequest;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

public class AIDataServiceTest {

  @Test
  public void testCustomContext() throws AIServiceException {

    final String expectedTimeZone = getNonDefaultTimeZoneID();
    AIServiceContext customContext = new AIServiceContextBuilder()
        .setSessionId("customSessionId")
        .setTimeZone(TimeZone.getTimeZone(expectedTimeZone))
        .build();

    TestableAIDataService dataService = new TestableAIDataService();
    
    String defaultTimeZoneID = Calendar.getInstance().getTimeZone().getID();
    AIRequest request;

    // Timezone is undefined
    request = new AIRequest();
    dataService.request(request);
    assertTrue(dataService.endpointValue.indexOf("sessionId=customSessionId") == -1);
    assertTrue(dataService.requestJsonValue.indexOf("\"sessionId\"=\"customSessionId\"") == -1);
    assertEquals(defaultTimeZoneID, request.getTimezone());
    assertTrue(dataService.requestJsonValue.indexOf(defaultTimeZoneID) > 0);
    assertTrue(dataService.requestJsonValue.indexOf(expectedTimeZone) == -1);

    // Timezone is defined in custom context
    request = new AIRequest();
    dataService.request(request, customContext);
    assertTrue(dataService.endpointValue.indexOf("sessionId=customSessionId") > 0);
    assertTrue(dataService.requestJsonValue.indexOf("\"sessionId\":\"customSessionId\"") > 0);
    assertEquals(expectedTimeZone, request.getTimezone());
    assertTrue(dataService.requestJsonValue.indexOf(expectedTimeZone) > 0);
    assertTrue(dataService.requestJsonValue.indexOf(defaultTimeZoneID) == -1);

    // Timezone is defined in request and custom context
    request = new AIRequest();
    request.setTimezone("GMT");
    dataService.request(request, customContext);
    assertTrue(dataService.endpointValue.indexOf("sessionId=customSessionId") > 0);
    assertTrue(dataService.requestJsonValue.indexOf("\"sessionId\":\"customSessionId\"") > 0);
    assertEquals("GMT", request.getTimezone());
    assertTrue(dataService.requestJsonValue.indexOf(expectedTimeZone) == -1);
    assertTrue(dataService.requestJsonValue.indexOf(defaultTimeZoneID) == -1);
    assertTrue(dataService.requestJsonValue.indexOf("GMT") > 0);
    
    // Session Id defined in request
    request = new AIRequest();
    request.setSessionId("requestSessionId");
    dataService.request(request);
    assertTrue(dataService.endpointValue.indexOf("sessionId=requestSessionId") > 0);
    assertTrue(dataService.requestJsonValue.indexOf("\"sessionId\":\"requestSessionId\"") > 0);
    dataService.request(request, customContext);
    assertTrue(dataService.endpointValue.indexOf("sessionId=requestSessionId") > 0);
    assertTrue(dataService.requestJsonValue.indexOf("\"sessionId\":\"requestSessionId\"") > 0);
  }
  
  @Test
  public void testOriginalRequest() throws AIServiceException {
    TestableAIDataService dataService = new TestableAIDataService();
    AIRequest request = new AIRequest();
    
    dataService.request(request);
    assertTrue(dataService.requestJsonValue.indexOf("\"originalRequest\":{}") == -1);
    
    request.setOriginalRequest(new AIOriginalRequest());
    dataService.request(request);
    assertTrue(dataService.requestJsonValue.indexOf("\"originalRequest\":{}") > 0);
  }

  private static String getNonDefaultTimeZoneID() {
    final String defaultID = TimeZone.getDefault().getID();
    for (String result : TimeZone.getAvailableIDs()) {
      if (! result.equals(defaultID)) {
        return result;
      }
    }
    throw new RuntimeException("Only default time zone available");
  }

  private class TestableAIDataService extends AIDataService {
    String endpointValue;
    String requestJsonValue;

    public TestableAIDataService() {
      super(new AIConfiguration(""));
    }
    
    @Override
    public AIResponse request(AIRequest request, RequestExtras requestExtras,
        AIServiceContext serviceContext) throws AIServiceException {
      // Clear values before request
      endpointValue = null;
      requestJsonValue = null;
      return super.request(request, requestExtras, serviceContext);
    }

    @Override
    protected String doTextRequest(String endpoint, String requestJson,
        Map<String, String> additionalHeaders) throws MalformedURLException, AIServiceException {
      endpointValue = endpoint;
      requestJsonValue = requestJson;
      return "{}";
    }
  }
}
