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
 
package ai.api.speech.gcp;

import java.util.Arrays;
import java.util.List;

import com.google.cloud.speech.v1.RecognitionConfig;
import com.google.cloud.speech.v1.RecognitionConfig.AudioEncoding;

import ai.api.AIConfiguration;

/**
 * Configuration for {@link GcpAIDataService}
 */
public class GcpAIConfiguration extends AIConfiguration {
	
	private static final int DEFAULT_SAMPLING_RATE = 16000; 
	
	private String speechApiHost = "speech.googleapis.com";
	private int speechApiPort = 443;
	private List<String> authScope = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");
	private RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
            .setEncoding(AudioEncoding.LINEAR16)
            .setSampleRateHertz(DEFAULT_SAMPLING_RATE)
            .setLanguageCode(getLanguage())
            .build();

	/**
	 * Create new configuration and initialize client access token
	 * @param clientAccessToken <a href="https://docs.api.ai/docs/authentication#obtaining-access-tokens">
	 * Client access token</a>
	 */
	public GcpAIConfiguration(final String clientAccessToken) {
		super(clientAccessToken);
	}
	
	/**
     * Create new configuration and initialize client access token
     * @param clientAccessToken <a href="https://docs.api.ai/docs/authentication#obtaining-access-tokens">
     * Client access token</a>
     * @param language Supported language
     */
	public GcpAIConfiguration(final String clientAccessToken, final SupportedLanguages language) {
		super(clientAccessToken, language);
	}

	/**
	 * @see AIConfiguration#clone()
	 */
	@Override
	public GcpAIConfiguration clone() {
		return (GcpAIConfiguration)super.clone();
	}

	/**
	 * Get URI for speech API service
	 */
	public final String getSpeechApiHost() {
		return speechApiHost;
	}

	/**
     * Set URI for speech API service
     */
	public final void setSpeechApiHost(String speechApiHost) {
		this.speechApiHost = speechApiHost;
	}

	/**
     * Get port number for speech API service
     */
	public final int getSpeechApiPort() {
		return speechApiPort;
	}

	/**
     * Set port number for speech API service
     */
	public final void setSpeechApiPort(int speechApiPort) {
		this.speechApiPort = speechApiPort;
	}

	public final List<String> getAuthScope() {
		return authScope;
	}

	public final void setAuthScope(List<String> authScope) {
		this.authScope = authScope;
	}

	/**
	 * Get recognition configuration
	 * @return <code>null</code> if recognition configuration is undefined
	 */
	public final RecognitionConfig getRecognitionConfig() {
		return recognitionConfig;
	}

	/**
     * Set recognition configuration
     */
	public final void setRecognitionConfig(RecognitionConfig recognitionConfig) {
		this.recognitionConfig = recognitionConfig;
	}
}
