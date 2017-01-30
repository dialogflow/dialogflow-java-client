package ai.api.speech.gcp;

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
