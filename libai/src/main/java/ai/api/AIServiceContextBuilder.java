package ai.api;

import java.util.UUID;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 * *********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

/**
 * Builds {@link AIServiceContext} to be used in {@link AIDataService} 
 */
public class AIServiceContextBuilder {
	
	String sessionId;
	
	/**
	 * Default builder constructor. 
	 * Does not initialise any building options.
	 */
	public AIServiceContextBuilder() {
	}
	
	/**
	 * @return Current sessionId value or <code>null</code> if
	 * value was not defined
	 */
	public String getSessionId() {
		return sessionId;
	}
	
	/**
	 * Replace current session id with given value
	 * @param sessionId Unique string session id. Cannot be <code>null</code>
	 * 
	 * @throws IllegalArgumentException Thrown if sessionId parameter value is null
	 */
	public AIServiceContextBuilder setSessionId(final String sessionId) {
		if (sessionId == null) {
			throw new IllegalArgumentException("sessionId cannot be null");
		}
		this.sessionId = sessionId;
		return this;
	}
	
	/**
	 * Replace current session id with some new random value
	 */
	public AIServiceContextBuilder generateSessionId() {
		this.sessionId = createRandomSessionId();
		return this;
	}
	
	/**
	 * Build new context instance
	 * 
	 * @throws IllegalStateException Thrown if session id was not defined
	 */
	public AIServiceContext build() {
		if (sessionId == null) {
			throw new IllegalStateException("Session id is undefined");
		}
		return new PlainAIServiceContext(sessionId);
	}
	
	public static AIServiceContext buildFromSessionId(String sessionId) {
		return new AIServiceContextBuilder().setSessionId(sessionId).build();
	}
	
	private static String createRandomSessionId() {
		return UUID.randomUUID().toString();
	}
	
	private static class PlainAIServiceContext implements AIServiceContext {
		
		private final String sessionId;
		
		public PlainAIServiceContext(String sessionId) {
			this.sessionId = sessionId;
		}

		@Override
		public String getSessionId() {
			return sessionId;
		}
	}
}
