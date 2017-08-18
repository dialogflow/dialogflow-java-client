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
 
package ai.api.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.AIServiceContext;
import ai.api.AIServiceContextBuilder;
import ai.api.AIServiceException;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * Basic AI service servlet.
 * 
 * Must be initialized with {@link AIServiceServlet#PARAM_API_AI_KEY} parameter.
 * Set your <a href="https://docs.api.ai/docs/authentication#obtaining-access-tokens">
 * api.ai access token</a> as a value.
 */
public abstract class AIServiceServlet extends HttpServlet {
  private static final long serialVersionUID = 1L;

  /**
   * Api.ai access token parameter name
   */
  public static final String PARAM_API_AI_KEY = "apiaiKey";

  private AIDataService aiDataService;

  /**
   * @see HttpServlet#init(ServletConfig config)
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    super.init(config);

    AIConfiguration aiConfig = new AIConfiguration(config.getInitParameter(PARAM_API_AI_KEY));
    aiDataService = new AIDataService(aiConfig);
  }

  /**
   * Perform request to AI data service
   * @param aiRequest Request object. Cannot be <code>null</code>.
   * @param serviceContext Service context. If <code>null</code> then default context will be used.
   * @return Response object
   * @throws AIServiceException Thrown on server access error
   */
  protected final AIResponse request(AIRequest aiRequest, AIServiceContext serviceContext)
      throws AIServiceException {
    return aiDataService.request(aiRequest, serviceContext);
  }

  /**
   * Perform request to AI data service
   * @param query Request plain text string. Cannot be <code>null</code>.
   * @param serviceContext Service context. If <code>null</code> then default context will be used.
   * @return Response object
   * @throws AIServiceException Thrown on server access error
   */
  protected final AIResponse request(String query, AIServiceContext serviceContext)
      throws AIServiceException {
    return request(new AIRequest(query), serviceContext);
  }

  /**
   * Perform request to AI data service
   * @param aiRequest Request object. Cannot be <code>null</code>.
   * @param session Session object. If <code>null</code> then default context will be used.
   * @return Response object
   * @throws AIServiceException Thrown on server access error
   */
  protected final AIResponse request(AIRequest aiRequest, HttpSession session)
      throws AIServiceException {
    return request(aiRequest,
        (session != null) ? AIServiceContextBuilder.buildFromSessionId(session.getId()) : null);
  }

  /**
   * Perform request to AI data service
   * @param query Request plain text string. Cannot be <code>null</code>.
   * @param session Session object. If <code>null</code> then default context will be used.
   * @return Response object
   * @throws AIServiceException Thrown on server access error
   */
  protected final AIResponse request(String query, HttpSession session) throws AIServiceException {
    return request(new AIRequest(query),
        (session != null) ? AIServiceContextBuilder.buildFromSessionId(session.getId()) : null);
  }

  /**
   * Perform request to AI data service
   * @param aiRequest Request object. Cannot be <code>null</code>.
   * @param sessionId Session string id. If <code>null</code> then default context will be used.
   * @return Response object
   * @throws AIServiceException Thrown on server access error
   */
  protected final AIResponse request(AIRequest aiRequest, String sessionId)
      throws AIServiceException {
    return request(aiRequest,
        (sessionId != null) ? AIServiceContextBuilder.buildFromSessionId(sessionId) : null);
  }

  /**
   * Perform request to AI data service
   * @param query Request plain text string. Cannot be <code>null</code>.
   * @param sessionId Session string id. If <code>null</code> then default context will be used.
   * @return Response object
   * @throws AIServiceException Thrown on server access error
   */
  protected final AIResponse request(String query, String sessionId) throws AIServiceException {
    return request(new AIRequest(query),
        (sessionId != null) ? AIServiceContextBuilder.buildFromSessionId(sessionId) : null);
  }
}
