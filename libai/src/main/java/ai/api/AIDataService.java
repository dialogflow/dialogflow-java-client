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

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import ai.api.util.IOUtils;
import ai.api.util.StringUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.*;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ai.api.http.HttpClient;
import ai.api.model.AIContext;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Entity;
import ai.api.model.Status;

/**
 * Do simple requests to the AI Service
 */
public class AIDataService {

  private static final Logger logger = LoggerFactory.getLogger(AIDataService.class);
  private static final AIServiceContext UNDEFINED_SERVICE_CONTEXT = null;
  private static final String REQUEST_METHOD_POST = "POST";
  private static final String REQUEST_METHOD_DELETE = "DELETE";
  private static final String REQUEST_METHOD_GET = "GET";
  private static final String DEFAULT_REQUEST_METHOD = REQUEST_METHOD_POST;

  /**
   * Cannot be <code>null</code>
   */
  private final static Gson GSON = GsonFactory.getDefaultFactory().getGson();

  /**
   * Cannot be <code>null</code>
   */
  private final AIConfiguration config;

  /**
   * Cannot be <code>null</code>
   */
  private final AIServiceContext defaultServiceContext;

  /**
   * Create new service for given configuration and some predefined service context
   * 
   * @param config Service configuration data. Cannot be <code>null</code>
   * @param serviceContext Service context. If <code>null</code> then new context will be created
   * @throws IllegalArgumentException If config parameter is null
   */
  public AIDataService(final AIConfiguration config, final AIServiceContext serviceContext) {
    if (config == null) {
      throw new IllegalArgumentException("config should not be null");
    }
    this.config = config.clone();
    if (serviceContext == null) {
      this.defaultServiceContext = new AIServiceContextBuilder().generateSessionId().build();
    } else {
      this.defaultServiceContext = serviceContext;
    }
  }

  /**
   * Create new service with unique context for given configuration
   * 
   * @param config Service configuration data. Cannot be <code>null</code>
   * @throws IllegalArgumentException If config parameter is null
   */
  public AIDataService(final AIConfiguration config) {
    this(config, null);
  }

  /**
   * @return Current context used in each request. Never <code>null</code>
   */
  public AIServiceContext getContext() {
    return defaultServiceContext;
  }

  /**
   * Make request to the AI service.
   *
   * @param request request object to the service. Cannot be <code>null</code>
   * @return response object from service. Never <code>null</code>
   */
  public AIResponse request(final AIRequest request) throws AIServiceException {
    return request(request, (RequestExtras) null);
  }

  /**
   * Make request to the AI service.
   *
   * @param request request object to the service. Cannot be <code>null</code>
   * @param serviceContext custom service context that should be used instead of the default context
   * @return response object from service. Never <code>null</code>
   */
  public AIResponse request(final AIRequest request, final AIServiceContext serviceContext)
      throws AIServiceException {
    return request(request, (RequestExtras) null, serviceContext);
  }

  /**
   * Make request to the AI service.
   *
   * @param request request object to the service. Cannot be <code>null</code>
   * @param requestExtras object that can hold additional contexts and entities
   * @return response object from service. Never <code>null</code>
   */
  public AIResponse request(final AIRequest request, final RequestExtras requestExtras)
      throws AIServiceException {
    return request(request, requestExtras, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Make request to the AI service.
   *
   * @param request request object to the service. Cannot be <code>null</code>
   * @param requestExtras object that can hold additional contexts and entities
   * @param serviceContext custom service context that should be used instead of the default context
   * @return response object from service. Never <code>null</code>
   */
  public AIResponse request(final AIRequest request, final RequestExtras requestExtras,
      final AIServiceContext serviceContext) throws AIServiceException {
    if (request == null) {
      throw new IllegalArgumentException("Request argument must not be null");
    }

    logger.debug("Start request");

    try {

      if (StringUtils.isEmpty(request.getLanguage())) {
        request.setLanguage(config.getApiAiLanguage());
      }
      if (StringUtils.isEmpty(request.getSessionId())) {
        request.setSessionId(getSessionId(serviceContext));
      }
      if (StringUtils.isEmpty(request.getTimezone())) {
        request.setTimezone(getTimeZone(serviceContext));
      }

      Map<String, String> additionalHeaders = null;

      if (requestExtras != null) {
        fillRequest(request, requestExtras);
        additionalHeaders = requestExtras.getAdditionalHeaders();
      }

      final String queryData = GSON.toJson(request);
      final String response = doTextRequest(config.getQuestionUrl(request.getSessionId()),
          queryData, additionalHeaders);

      if (StringUtils.isEmpty(response)) {
        throw new AIServiceException(
            "Empty response from ai service. Please check configuration and Internet connection.");
      }

      logger.debug("Response json: " + response.replaceAll("[\r\n]+", " "));

      final AIResponse aiResponse = GSON.fromJson(response, AIResponse.class);

      if (aiResponse == null) {
        throw new AIServiceException(
            "API.AI response parsed as null. Check debug log for details.");
      }

      if (aiResponse.isError()) {
        throw new AIServiceException(aiResponse);
      }

      aiResponse.cleanup();

      return aiResponse;

    } catch (final MalformedURLException e) {
      logger.error("Malformed url should not be raised", e);
      throw new AIServiceException("Wrong configuration. Please, connect to API.AI Service support",
          e);
    } catch (final JsonSyntaxException je) {
      throw new AIServiceException(
          "Wrong service answer format. Please, connect to API.AI Service support", je);
    }

  }

  /**
   * Make requests to the AI service with voice data.
   *
   * @param voiceStream voice data stream for recognition. Cannot be <code>null</code>
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse voiceRequest(final InputStream voiceStream) throws AIServiceException {
    return voiceRequest(voiceStream, new RequestExtras());
  }

  /**
   * Make requests to the AI service with voice data.
   *
   * @param voiceStream voice data stream for recognition. Cannot be <code>null</code>
   * @param aiContexts additional contexts for request
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse voiceRequest(final InputStream voiceStream, final List<AIContext> aiContexts)
      throws AIServiceException {
    return voiceRequest(voiceStream, new RequestExtras(aiContexts, null));
  }

  /**
   * Make requests to the AI service with voice data.
   *
   * @param voiceStream voice data stream for recognition. Cannot be <code>null</code>
   * @param requestExtras object that can hold additional contexts and entities
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse voiceRequest(final InputStream voiceStream, final RequestExtras requestExtras)
      throws AIServiceException {
    return voiceRequest(voiceStream, requestExtras, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Make requests to the AI service with voice data.
   *
   * @param voiceStream voice data stream for recognition. Cannot be <code>null</code>
   * @param requestExtras object that can hold additional contexts and entities
   * @param serviceContext custom service context that should be used instead of the default context
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse voiceRequest(final InputStream voiceStream, final RequestExtras requestExtras,
      final AIServiceContext serviceContext) throws AIServiceException {
    assert voiceStream != null;
    logger.debug("Start voice request");

    try {
      final AIRequest request = new AIRequest();

      request.setLanguage(config.getApiAiLanguage());
      request.setSessionId(getSessionId(serviceContext));
      request.setTimezone(getTimeZone(serviceContext));

      Map<String, String> additionalHeaders = null;

      if (requestExtras != null) {
        fillRequest(request, requestExtras);
        additionalHeaders = requestExtras.getAdditionalHeaders();
      }

      final String queryData = GSON.toJson(request);

      logger.debug("Request json: " + queryData);

      final String response = doSoundRequest(voiceStream, queryData, additionalHeaders);

      if (StringUtils.isEmpty(response)) {
        throw new AIServiceException("Empty response from ai service. Please check configuration.");
      }

      logger.debug("Response json: " + response);

      final AIResponse aiResponse = GSON.fromJson(response, AIResponse.class);

      if (aiResponse == null) {
        throw new AIServiceException(
            "API.AI response parsed as null. Check debug log for details.");
      }

      if (aiResponse.isError()) {
        throw new AIServiceException(aiResponse);
      }

      aiResponse.cleanup();

      return aiResponse;

    } catch (final MalformedURLException e) {
      logger.error("Malformed url should not be raised", e);
      throw new AIServiceException("Wrong configuration. Please, connect to AI Service support", e);
    } catch (final JsonSyntaxException je) {
      throw new AIServiceException(
          "Wrong service answer format. Please, connect to API.AI Service support", je);
    }
  }

  /**
   * Forget all old contexts
   *
   * @return true if operation succeed, false otherwise
   */
  @Deprecated
  public boolean resetContexts() {
    final AIRequest cleanRequest = new AIRequest();
    cleanRequest.setQuery("empty_query_for_resetting_contexts"); // TODO remove it after protocol
                                                                 // fix
    cleanRequest.setResetContexts(true);
    try {
      final AIResponse response = request(cleanRequest);
      return !response.isError();
    } catch (final AIServiceException e) {
      logger.error("Exception while contexts clean.", e);
      return false;
    }
  }

  /**
   * Retrieves the list of all currently active contexts for a session
   * 
   * @return List of contexts, or empty list if there is no any active contexts
   * @throws AIServiceException
   */
  public List<AIContext> getActiveContexts() throws AIServiceException {
    return getActiveContexts(UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Retrieves the list of all currently active contexts for a session
   * 
   * @param serviceContext custom service context that should be used instead of the default context
   * @return List of contexts, or empty list if there is no any active contexts
   * @throws AIServiceException
   */
  public List<AIContext> getActiveContexts(final AIServiceContext serviceContext)
      throws AIServiceException {
    try {
      return doRequest(ApiActiveContextListResponse.class,
          config.getContextsUrl(getSessionId(serviceContext)), REQUEST_METHOD_GET);
    } catch (BadResponseStatusException e) {
      throw new AIServiceException(e.response);
    }
  }

  /**
   * Retrieves the specified context for a session
   * 
   * @param contextName The context name
   * @return <code>null</code> if context not found
   * @throws AIServiceException
   */
  public AIContext getActiveContext(final String contextName) throws AIServiceException {
    return getActiveContext(contextName, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Retrieves the specified context for a session
   * 
   * @param contextName The context name
   * @param serviceContext custom service context that should be used instead of the default context
   * @return <code>null</code> if context not found
   * @throws AIServiceException
   */
  public AIContext getActiveContext(final String contextName, final AIServiceContext serviceContext)
      throws AIServiceException {
    try {
      return doRequest(AIContext.class,
          config.getContextsUrl(getSessionId(serviceContext), contextName), REQUEST_METHOD_GET);
    } catch (BadResponseStatusException e) {
      if (e.response.getStatus().getCode() == 404) {
        return null;
      } else {
        throw new AIServiceException(e.response);
      }
    }
  }

  /**
   * Adds new active contexts for a session
   * 
   * @param contexts Iterable collection of contexts
   * @return List of added context names, or empty list if no contexts were added
   * @throws AIServiceException
   */
  public List<String> addActiveContext(final Iterable<AIContext> contexts)
      throws AIServiceException {
    return addActiveContext(contexts, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Adds new active contexts for a session
   * 
   * @param contexts Iterable collection of contexts
   * @param serviceContext custom service context that should be used instead of the default context
   * @return List of added context names, or empty list if no contexts were added
   * @throws AIServiceException
   */
  public List<String> addActiveContext(final Iterable<AIContext> contexts,
      final AIServiceContext serviceContext) throws AIServiceException {
    ApiActiveContextNamesResponse response;
    try {
      response = doRequest(contexts, ApiActiveContextNamesResponse.class,
          config.getContextsUrl(getSessionId(serviceContext)), REQUEST_METHOD_POST);
      return response.names;
    } catch (BadResponseStatusException e) {
      throw new AIServiceException(e.response);
    }
  }

  /**
   * Adds new active context for a session
   * 
   * @param context New context
   * @return Name of added context
   * @throws AIServiceException
   */
  public String addActiveContext(final AIContext context) throws AIServiceException {
    return addActiveContext(context, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Adds new active context for a session
   * 
   * @param context New context
   * @param serviceContext custom service context that should be used instead of the default context
   * @return Name of added context
   * @throws AIServiceException
   */
  public String addActiveContext(final AIContext context, final AIServiceContext serviceContext)
      throws AIServiceException {
    ApiActiveContextNamesResponse response;
    try {
      response = doRequest(context, ApiActiveContextNamesResponse.class,
          config.getContextsUrl(getSessionId(serviceContext)), REQUEST_METHOD_POST);
      return response.names != null && response.names.size() > 0 ? response.names.get(0) : null;
    } catch (BadResponseStatusException e) {
      throw new AIServiceException(e.response);
    }
  }

  /**
   * Deletes all active contexts for a session
   * 
   * @throws AIServiceException
   */
  public void resetActiveContexts() throws AIServiceException {
    resetActiveContexts(UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Deletes all active contexts for a session
   * 
   * @param serviceContext custom service context that should be used instead of the default context
   * @throws AIServiceException
   */
  public void resetActiveContexts(final AIServiceContext serviceContext) throws AIServiceException {
    try {
      doRequest(AIResponse.class, config.getContextsUrl(getSessionId(serviceContext)),
          REQUEST_METHOD_DELETE);
    } catch (BadResponseStatusException e) {
      throw new AIServiceException(e.response);
    }
  }

  /**
   * Deletes the specified context for a session
   * 
   * @param contextName The context name
   * @return <code>false</code> if context was not delete, <code>true</code> in otherwise case
   * @throws AIServiceException
   */
  public boolean removeActiveContext(final String contextName) throws AIServiceException {
    return removeActiveContext(contextName, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Deletes the specified context for a session
   * 
   * @param contextName The context name
   * @param serviceContext custom service context that should be used instead of the default context
   * @return <code>false</code> if context was not delete, <code>true</code> in otherwise case
   * @throws AIServiceException
   */
  public boolean removeActiveContext(final String contextName,
      final AIServiceContext serviceContext) throws AIServiceException {

    try {
      doRequest(AIResponse.class, config.getContextsUrl(getSessionId(serviceContext), contextName),
          REQUEST_METHOD_DELETE);
      return true;
    } catch (BadResponseStatusException e) {
      if (e.response.getStatus().getCode() == 404) {
        return false;
      } else {
        throw new AIServiceException(e.response);
      }
    }
  }

  /**
   * Add new entity to an agent entity list
   * 
   * @param userEntity new entity data
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse uploadUserEntity(final Entity userEntity) throws AIServiceException {
    return uploadUserEntity(userEntity, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Add new entity to an agent entity list
   * 
   * @param userEntity new entity data
   * @param serviceContext custom service context that should be used instead of the default context
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse uploadUserEntity(final Entity userEntity, AIServiceContext serviceContext)
      throws AIServiceException {
    return uploadUserEntities(Collections.singleton(userEntity), serviceContext);
  }

  /**
   * Add a bunch of new entity to an agent entity list
   * 
   * @param userEntities collection of a new entity data
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse uploadUserEntities(final Collection<Entity> userEntities)
      throws AIServiceException {
    return uploadUserEntities(userEntities, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Add a bunch of new entity to an agent entity list
   * 
   * @param userEntities collection of a new entity data
   * @param serviceContext custom service context that should be used instead of the default context
   * @return response object from service. Never <code>null</code>
   * @throws AIServiceException
   */
  public AIResponse uploadUserEntities(final Collection<Entity> userEntities,
      AIServiceContext serviceContext) throws AIServiceException {
    if (userEntities == null || userEntities.size() == 0) {
      throw new AIServiceException("Empty entities list");
    }

    final String requestData = GSON.toJson(userEntities);
    try {
      final String response =
          doTextRequest(config.getUserEntitiesEndpoint(getSessionId(serviceContext)), requestData);
      if (StringUtils.isEmpty(response)) {
        throw new AIServiceException(
            "Empty response from ai service. Please check configuration and Internet connection.");
      }
      logger.debug("Response json: " + response);

      final AIResponse aiResponse = GSON.fromJson(response, AIResponse.class);

      if (aiResponse == null) {
        throw new AIServiceException(
            "API.AI response parsed as null. Check debug log for details.");
      }

      if (aiResponse.isError()) {
        throw new AIServiceException(aiResponse);
      }

      aiResponse.cleanup();
      return aiResponse;

    } catch (final MalformedURLException e) {
      logger.error("Malformed url should not be raised", e);
      throw new AIServiceException("Wrong configuration. Please, connect to AI Service support", e);
    } catch (final JsonSyntaxException je) {
      throw new AIServiceException(
          "Wrong service answer format. Please, connect to API.AI Service support", je);
    }
  }

  /**
   * @param requestJson Cannot be <code>null</code>
   * @param serviceContext custom service context that should be used instead of the default context
   * @return Response string
   * @throws MalformedURLException
   * @throws AIServiceException
   */
  protected String doTextRequest(final String requestJson, AIServiceContext serviceContext)
      throws MalformedURLException, AIServiceException {
    return doTextRequest(config.getQuestionUrl(getSessionId(serviceContext)), requestJson);
  }

  /**
   * @param requestJson Cannot be <code>null</code>
   * @return Response string
   * @throws MalformedURLException
   * @throws AIServiceException
   */
  protected String doTextRequest(final String requestJson)
      throws MalformedURLException, AIServiceException {
    return doTextRequest(requestJson, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * @param endpoint Cannot be <code>null</code>
   * @param requestJson Cannot be <code>null</code>
   * @return Response string
   * @throws MalformedURLException
   * @throws AIServiceException
   */
  protected String doTextRequest(final String endpoint, final String requestJson)
      throws MalformedURLException, AIServiceException {
    return doTextRequest(endpoint, requestJson, null);
  }

  /**
   * @param endpoint Cannot be <code>null</code>
   * @param requestJson Cannot be <code>null</code>
   * @param additionalHeaders
   * @return Response string
   * @throws MalformedURLException
   * @throws AIServiceException
   */
  protected String doTextRequest(final String endpoint, final String requestJson,
      final Map<String, String> additionalHeaders)
      throws MalformedURLException, AIServiceException {
    // TODO call doRequest method
    assert endpoint != null;
    assert requestJson != null;
    HttpURLConnection connection = null;

    try {

      final URL url = new URL(endpoint);

      final String queryData = requestJson;

      logger.debug("Request json: " + queryData);

      if (config.getProxy() != null) {
        connection = (HttpURLConnection) url.openConnection(config.getProxy());
      } else {
        connection = (HttpURLConnection) url.openConnection();
      }

      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.addRequestProperty("Authorization", "Bearer " + config.getApiKey());
      connection.addRequestProperty("Content-Type", "application/json; charset=utf-8");
      connection.addRequestProperty("Accept", "application/json");

      if (additionalHeaders != null) {
        for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
          connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
      }

      connection.connect();

      final BufferedOutputStream outputStream =
          new BufferedOutputStream(connection.getOutputStream());
      IOUtils.writeAll(queryData, outputStream);
      outputStream.close();

      final InputStream inputStream = new BufferedInputStream(connection.getInputStream());
      final String response = IOUtils.readAll(inputStream);
      inputStream.close();

      return response;
    } catch (final IOException e) {
      if (connection != null) {
        try {
          final InputStream errorStream = connection.getErrorStream();
          if (errorStream != null) {
            final String errorString = IOUtils.readAll(errorStream);
            logger.debug(errorString);
            return errorString;
          } else {
            throw new AIServiceException("Can't connect to the api.ai service.", e);
          }
        } catch (final IOException ex) {
          logger.warn("Can't read error response", ex);
        }
      }
      logger.error(
          "Can't make request to the API.AI service. Please, check connection settings and API access token.",
          e);
      throw new AIServiceException(
          "Can't make request to the API.AI service. Please, check connection settings and API access token.",
          e);

    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }

  }

  /**
   * Method extracted for testing purposes
   * 
   * @param voiceStream Cannot be <code>null</code>
   * @param queryData Cannot be <code>null</code>
   */
  protected String doSoundRequest(final InputStream voiceStream, final String queryData)
      throws MalformedURLException, AIServiceException {
    return doSoundRequest(voiceStream, queryData, null, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Method extracted for testing purposes
   * 
   * @param voiceStream Cannot be <code>null</code>
   * @param queryData Cannot be <code>null</code>
   */
  protected String doSoundRequest(final InputStream voiceStream, final String queryData,
      final Map<String, String> additionalHeaders)
      throws MalformedURLException, AIServiceException {
    return doSoundRequest(voiceStream, queryData, additionalHeaders, UNDEFINED_SERVICE_CONTEXT);
  }

  /**
   * Method extracted for testing purposes
   * 
   * @param voiceStream Cannot be <code>null</code>
   * @param queryData Cannot be <code>null</code>
   */
  protected String doSoundRequest(final InputStream voiceStream, final String queryData,
      final Map<String, String> additionalHeaders, final AIServiceContext serviceContext)
      throws MalformedURLException, AIServiceException {

    // TODO call doRequest method
    assert voiceStream != null;
    assert queryData != null;
    HttpURLConnection connection = null;
    HttpClient httpClient = null;

    try {
      final URL url = new URL(config.getQuestionUrl(getSessionId(serviceContext)));

      logger.debug("Connecting to {}", url);

      if (config.getProxy() != null) {
        connection = (HttpURLConnection) url.openConnection(config.getProxy());
      } else {
        connection = (HttpURLConnection) url.openConnection();
      }

      connection.addRequestProperty("Authorization", "Bearer " + config.getApiKey());
      connection.addRequestProperty("Accept", "application/json");

      if (additionalHeaders != null) {
        for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
          connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
      }

      connection.setRequestMethod("POST");
      connection.setDoInput(true);
      connection.setDoOutput(true);

      httpClient = new HttpClient(connection);
      httpClient.setWriteSoundLog(config.isWriteSoundLog());

      httpClient.connectForMultipart();
      httpClient.addFormPart("request", queryData);
      httpClient.addFilePart("voiceData", "voice.wav", voiceStream);
      httpClient.finishMultipart();

      final String response = httpClient.getResponse();
      return response;

    } catch (final IOException e) {
      if (httpClient != null) {
        final String errorString = httpClient.getErrorString();
        logger.debug(errorString);
        if (!StringUtils.isEmpty(errorString)) {
          return errorString;
        } else if (e instanceof HttpRetryException) {
          final AIResponse response = new AIResponse();
          final int code = ((HttpRetryException) e).responseCode();
          final Status status = Status.fromResponseCode(code);
          status.setErrorDetails(((HttpRetryException) e).getReason());
          response.setStatus(status);
          throw new AIServiceException(response);
        }
      }

      logger.error(
          "Can't make request to the API.AI service. Please, check connection settings and API.AI keys.",
          e);
      throw new AIServiceException(
          "Can't make request to the API.AI service. Please, check connection settings and API.AI keys.",
          e);
    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  protected <TResponse> TResponse doRequest(final Type responseType, final String endpoint,
      final String method) throws AIServiceException, BadResponseStatusException {
    return doRequest(responseType, endpoint, method, (Map<String, String>) null);
  }

  protected <TRequest, TResponse> TResponse doRequest(final TRequest request,
      final Type responseType, final String endpoint, final String method)
      throws AIServiceException, BadResponseStatusException {
    return doRequest(request, responseType, endpoint, method, (Map<String, String>) null);
  }

  protected <TResponse> TResponse doRequest(final Type responseType, final String endpoint,
      final String method, final Map<String, String> additionalHeaders)
      throws AIServiceException, BadResponseStatusException {
    return doRequest((Object) null, responseType, endpoint, method, additionalHeaders);
  }

  protected <TRequest, TResponse> TResponse doRequest(final TRequest request,
      final Type responseType, final String endpoint, final String method,
      final Map<String, String> additionalHeaders)
      throws AIServiceException, BadResponseStatusException {

    assert endpoint != null;
    HttpURLConnection connection = null;

    try {

      final URL url = new URL(endpoint);

      final String queryData = request != null ? GSON.toJson(request) : null;
      final String requestMethod = method != null ? method : DEFAULT_REQUEST_METHOD;


      logger.debug("Request json: " + queryData);

      if (config.getProxy() != null) {
        connection = (HttpURLConnection) url.openConnection(config.getProxy());
      } else {
        connection = (HttpURLConnection) url.openConnection();
      }

      if (queryData != null && !REQUEST_METHOD_POST.equals(requestMethod)) {
        throw new AIServiceException("Non-empty request should be sent using POST method");
      }

      connection.setRequestMethod(requestMethod);
      if (REQUEST_METHOD_POST.equals(requestMethod)) {
        connection.setDoOutput(true);
      }
      connection.addRequestProperty("Authorization", "Bearer " + config.getApiKey());
      connection.addRequestProperty("Content-Type", "application/json; charset=utf-8");
      connection.addRequestProperty("Accept", "application/json");

      if (additionalHeaders != null) {
        for (final Map.Entry<String, String> entry : additionalHeaders.entrySet()) {
          connection.addRequestProperty(entry.getKey(), entry.getValue());
        }
      }

      connection.connect();

      if (queryData != null) {
        final BufferedOutputStream outputStream =
            new BufferedOutputStream(connection.getOutputStream());
        IOUtils.writeAll(queryData, outputStream);
        outputStream.close();
      }

      final InputStream inputStream = new BufferedInputStream(connection.getInputStream());
      final String response = IOUtils.readAll(inputStream);
      inputStream.close();

      try {
        AIResponse aiResponse = GSON.fromJson(response, AIResponse.class);

        if (aiResponse.getStatus() != null && aiResponse.getStatus().getCode() != 200) {
          throw new BadResponseStatusException(aiResponse);
        }
      } catch (JsonParseException e) {
        // response is not in a expected format
      }

      return GSON.fromJson(response, responseType);
    } catch (final IOException e) {
      if (connection != null) {
        try {
          final InputStream errorStream = connection.getErrorStream();
          if (errorStream != null) {
            final String errorString = IOUtils.readAll(errorStream);
            logger.debug(errorString);
            throw new AIServiceException(errorString, e);
          } else {
            throw new AIServiceException("Can't connect to the api.ai service.", e);
          }
        } catch (final IOException ex) {
          logger.warn("Can't read error response", ex);
        }
      }
      logger.error(
          "Can't make request to the API.AI service. Please, check connection settings and API access token.",
          e);
      throw new AIServiceException(
          "Can't make request to the API.AI service. Please, check connection settings and API access token.",
          e);

    } finally {
      if (connection != null) {
        connection.disconnect();
      }
    }
  }

  private void fillRequest(final AIRequest request, final RequestExtras requestExtras) {
    assert request != null;
    assert requestExtras != null;
    if (requestExtras.hasContexts()) {
      request.setContexts(requestExtras.getContexts());
    }

    if (requestExtras.hasEntities()) {
      request.setEntities(requestExtras.getEntities());
    }

    if (requestExtras.getLocation() != null) {
      request.setLocation(requestExtras.getLocation());
    }
  }

  private String getSessionId(AIServiceContext serviceContext) {
    return serviceContext != null ? serviceContext.getSessionId()
        : defaultServiceContext.getSessionId();
  }

  private String getTimeZone(AIServiceContext serviceContext) {
    TimeZone timeZone = serviceContext != null
        ? serviceContext.getTimeZone()
        : defaultServiceContext.getTimeZone();
    return (timeZone != null ? timeZone : Calendar.getInstance().getTimeZone()).getID();
  }

  private static class ApiActiveContextNamesResponse extends AIResponse {

    private static final long serialVersionUID = 1L;

    public List<String> names;
  }

  private static interface ApiActiveContextListResponse extends List<AIContext> {
  }

  private static class BadResponseStatusException extends Exception {

    private static final long serialVersionUID = 1L;

    private final AIResponse response;

    public BadResponseStatusException(final AIResponse response) {
      this.response = response;
    }
  }
}
