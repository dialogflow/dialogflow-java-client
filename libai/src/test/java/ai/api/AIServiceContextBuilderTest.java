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
