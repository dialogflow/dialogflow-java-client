package ai.api.speech.gcp;

/***********************************************************************************************************************
*
* API.AI Java SDK - client-side libraries for API.AI
* =================================================
*
* Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com) https://www.api.ai
*
* *********************************************************************************************************************
*
* Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
* in compliance with the License. You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software distributed under the License
* is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
* or implied. See the License for the specific language governing permissions and limitations under
* the License.
*
***********************************************************************************************************************/

import java.util.Arrays;
import java.util.List;

import com.google.cloud.speech.v1beta1.RecognitionConfig;
import com.google.cloud.speech.v1beta1.RecognitionConfig.AudioEncoding;

import ai.api.AIConfiguration;

public class GcpAIConfiguration extends AIConfiguration {
	
	private static final int DEFAULT_SAMPLING_RATE = 16000; 
	
	private String speechApiHost = "speech.googleapis.com";
	private int speechApiPort = 443;
	private List<String> authScope = Arrays.asList("https://www.googleapis.com/auth/cloud-platform");
	private RecognitionConfig recognitionConfig = RecognitionConfig.newBuilder()
            .setEncoding(AudioEncoding.LINEAR16)
            .setSampleRate(DEFAULT_SAMPLING_RATE)
            .build();

	public GcpAIConfiguration(final String clientAccessToken) {
		super(clientAccessToken);
	}
	
	public GcpAIConfiguration(final String clientAccessToken, final SupportedLanguages language) {
		super(clientAccessToken, language);
	}

	@Override
	public GcpAIConfiguration clone() {
		return (GcpAIConfiguration)super.clone();
	}

	public final String getSpeechApiHost() {
		return speechApiHost;
	}

	public final void setSpeechApiHost(String speechApiHost) {
		this.speechApiHost = speechApiHost;
	}

	public final int getSpeechApiPort() {
		return speechApiPort;
	}

	public final void setSpeechApiPort(int speechApiPort) {
		this.speechApiPort = speechApiPort;
	}

	public final List<String> getAuthScope() {
		return authScope;
	}

	public final void setAuthScope(List<String> authScope) {
		this.authScope = authScope;
	}

	public final RecognitionConfig getRecognitionConfig() {
		return recognitionConfig;
	}

	public final void setRecognitionConfig(RecognitionConfig recognitionConfig) {
		this.recognitionConfig = recognitionConfig;
	}
}
