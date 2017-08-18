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

import java.util.TimeZone;
import java.util.UUID;

/**
 * Builds {@link AIServiceContext} to be used in {@link AIDataService}
 */
public class AIServiceContextBuilder {

  private String sessionId;
  private TimeZone timeZone;

  /**
   * Default builder constructor. Does not initialise any building options.
   */
  public AIServiceContextBuilder() {}

  /**
   * @return Current sessionId value or <code>null</code> if value was not defined
   */
  public String getSessionId() {
    return sessionId;
  }

  /**
   * Replace current session id with given value
   * 
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
   * @return Current time zone value or <code>null</code> if value was not defined
   */
  public TimeZone getTimeZone() {
    return timeZone;
  }

  /**
   * Replace current time zone with given value
   * 
   * @param timeZone Time zone value. May be <code>null</code>
   */
  public AIServiceContextBuilder setTimeZone(final TimeZone timeZone) {
    this.timeZone = timeZone;
    return this;
  }

  /**
   * Use {@link AIServiceContextBuilder}.setTimeZone method insted
   */
  @Deprecated()
  public AIServiceContextBuilder setSessionId(final TimeZone timeZone) {
    this.timeZone = timeZone;
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
    return new PlainAIServiceContext(sessionId, timeZone);
  }

  public static AIServiceContext buildFromSessionId(String sessionId) {
    return new AIServiceContextBuilder().setSessionId(sessionId).build();
  }

  private static String createRandomSessionId() {
    return UUID.randomUUID().toString();
  }

  private static class PlainAIServiceContext implements AIServiceContext {

    private final String sessionId;
    private final TimeZone timeZone;

    public PlainAIServiceContext(String sessionId, TimeZone timeZone) {
      assert sessionId != null;
      this.sessionId = sessionId;
      this.timeZone = timeZone;
    }

    @Override
    public String getSessionId() {
      return sessionId;
    }

    @Override
    public TimeZone getTimeZone() {
      return timeZone;
    }
  }
}
