package ai.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class AIServiceContextBuilderTest {

	@Test
	public void testBuild() {
		AIServiceContextBuilder builder = new AIServiceContextBuilder();
		
		assertNull(builder.getSessionId());
		
		builder.setSessionId(DUMMY_SESSION_ID);
		assertEquals(DUMMY_SESSION_ID, builder.getSessionId());

		AIServiceContext context = builder.build();
		assertEquals(DUMMY_SESSION_ID, context.getSessionId());
	}
	
	@Test
	public void testBuildFromSessionId() {
		AIServiceContext context = AIServiceContextBuilder.buildFromSessionId(DUMMY_SESSION_ID);
		assertEquals(DUMMY_SESSION_ID, context.getSessionId());
	}
	
	@Test
	public void testBuildFromRandomSessionId() {
		AIServiceContextBuilder builder = new AIServiceContextBuilder();
		
		builder.generateSessionId();
		String generatedSessionId = builder.getSessionId();
		assertNotNull(generatedSessionId);
		
		AIServiceContext context = builder.build();
		assertEquals(generatedSessionId, context.getSessionId());
	}
	
	@Test(expected=IllegalStateException.class)
	public void testBuildUnitialisedSessionId() {
		new AIServiceContextBuilder().build();
	}

	private final static String DUMMY_SESSION_ID = "dummySessionId";
}
