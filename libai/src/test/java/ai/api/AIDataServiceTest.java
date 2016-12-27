package ai.api;

import static org.junit.Assert.*;

import java.net.MalformedURLException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import ai.api.model.AIRequest;

public class AIDataServiceTest {

	@Test
	public void testCustomContext() throws AIServiceException {
		
		AIConfiguration config = new AIConfiguration("");
		
		final AtomicReference<String> endpointValue = new AtomicReference<>();
		
		AIDataService dataService = new AIDataService(config) {

			@Override
			protected String doTextRequest(String endpoint, String requestJson, Map<String, String> additionalHeaders)
					throws MalformedURLException, AIServiceException {
				endpointValue.set(endpoint);
				return "{}";
			}
			
		};
		AIServiceContext customContext = AIServiceContextBuilder.buildFromSessionId("customSessionId");
		
		AIRequest request = new AIRequest();
		
		dataService.request(request);
		assertTrue(endpointValue.get().indexOf("sessionId=customSessionId") == -1);
		
		dataService.request(request, customContext);
		assertTrue(endpointValue.get().indexOf("sessionId=customSessionId") > 0);
	}
}
