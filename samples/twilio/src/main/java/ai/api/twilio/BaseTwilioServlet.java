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
 
package ai.api.twilio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServlet;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * BaseTwilioServlet. Encapsulates methods to communicate with api.ai.
 */
public class BaseTwilioServlet extends HttpServlet {
	private static final long serialVersionUID = -8510776154233631616L;
	private static final CloseableHttpClient POOLING_HTTP_CLIENT;

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseTwilioServlet.class);

	protected Gson gson = new GsonBuilder().create();

	static {
		PoolingHttpClientConnectionManager POOLING_HTTP_CONN_MANAGER = new PoolingHttpClientConnectionManager();
		POOLING_HTTP_CONN_MANAGER.setDefaultMaxPerRoute(20);
		POOLING_HTTP_CLIENT = HttpClients.custom()
				.setDefaultRequestConfig(RequestConfig.custom().setStaleConnectionCheckEnabled(true).build())
				.setConnectionManager(POOLING_HTTP_CONN_MANAGER).build();
	}

	/**
	 * Send request to api.ai
	 * 
	 * @param query
	 * @param parameters
	 *            - parameters received from www.twilio.com
	 * @return response from api.ai
	 */
	protected String sendRequestToApiAi(String query, Map<String, String[]> parameters) {
		HttpResponse response = null;
		try {
			StringEntity input = new StringEntity(createApiRequest(query, parameters), ContentType.APPLICATION_JSON);

			response = POOLING_HTTP_CLIENT.execute(RequestBuilder.post().setUri(TwilioProperties.INSTANCE.getApiUrl())
					.addHeader("Authorization", "Bearer " + TwilioProperties.INSTANCE.getApiAccessToken())
					.addHeader("Accept", ContentType.APPLICATION_JSON.getMimeType()).setEntity(input).build());

			if (response.getStatusLine().getStatusCode() / 100 != 2) {
				return "Error: " + response.getStatusLine();
			}
			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

			StringBuilder resultBuilder = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				resultBuilder.append(line);
			}

			ApiResponse resultResp = gson.fromJson(resultBuilder.toString(), ApiResponse.class);
			if (Strings.isNullOrEmpty(Optional.ofNullable(resultResp.result).map(r -> r.fulfillment).map(f -> f.speech)
					.orElse(null))) {
				return "Action: "
						+ Optional.ofNullable(resultResp.result).map(r -> r.action).orElse("none")
						+ "\n"
						+ gson.toJson(Optional.ofNullable(resultResp.result).map(r -> r.parameters)
								.orElse(Collections.emptyMap()));
			}
			return resultResp.result.fulfillment.speech;
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
			return "Error: " + e.getMessage();
		} finally {
			if (response != null) {
				try {
					EntityUtils.consume(response.getEntity());
				} catch (IOException e) {
					// Ignore
				}
			}
		}
	}

	/**
	 * Create request to api.ai
	 * 
	 * @param query
	 * @param parameters
	 *            - parameters received from www.twilio.com
	 * @return request to api.ai
	 */
	protected String createApiRequest(String query, Map<String, String[]> parameters) {
		ApiRequest req = new ApiRequest();
		req.lang = "en";
		req.sessionId = Optional.ofNullable(parameters.get("From")).map(p -> p[0]).orElse("defaultSession");
		req.timezone = TwilioProperties.INSTANCE.getTimezone();
		req.query = query;

		return gson.toJson(req);
	}

	public static class ApiRequest {
		public String query;
		public String lang;
		public String timezone;
		public String sessionId;

		public List<ApiContext> contexts;
	}

	public static class ApiContext {
		public String name;
		public Map<String, String> parameters;
	}

	public static class ApiResponse {
		@Expose
		public Result result;
	}

	public static class Result {
		public String source;
		public String resolvedQuery;
		public String action;
		Map<String, Object> parameters;
		public Map<String, Object> metadata = new HashMap<>();
		public Fulfillment fulfillment;
	}

	public static class Fulfillment {
		public String speech;
		public String source;

		public Fulfillment() {
		};

		public Fulfillment(String speech, Object source) {
			this.speech = speech;
			this.source = source instanceof String ? (String) source : null;
		};
	}
}
