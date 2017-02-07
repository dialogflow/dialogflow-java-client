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

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Executors;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.speech.v1beta1.RecognitionAudio;
import com.google.cloud.speech.v1beta1.SpeechGrpc;
import com.google.cloud.speech.v1beta1.SyncRecognizeRequest;
import com.google.cloud.speech.v1beta1.SyncRecognizeResponse;
import com.google.cloud.speech.v1beta1.SpeechGrpc.SpeechBlockingStub;
import com.google.cloud.speech.v1beta1.SpeechRecognitionResult;
import com.google.protobuf.ByteString;

import ai.api.AIDataService;
import ai.api.AIServiceContext;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import io.grpc.auth.ClientAuthInterceptor;

/**
 * AIDataService with Google Speech API support
 */
public class GcpAIDataService extends AIDataService {
	
	private final GcpAIConfiguration config;
	private ManagedChannel channel;
	private SpeechBlockingStub speechClient;

	public GcpAIDataService(GcpAIConfiguration config) {
		super(config);
		this.config = config.clone();
	}

	public GcpAIDataService(GcpAIConfiguration config, AIServiceContext serviceContext) {
		super(config, serviceContext);
		this.config = config.clone();
	}

	@Override
	public AIResponse voiceRequest(InputStream voiceStream, RequestExtras requestExtras,
			AIServiceContext serviceContext) throws AIServiceException {
		
		SyncRecognizeResponse response;
		try {
			SpeechBlockingStub speechClient = getSpeechBlockingStub();
			RecognitionAudio recognitionAudio = createRecognitionAudio(voiceStream);
	        SyncRecognizeRequest request = SyncRecognizeRequest
	        		.newBuilder()
	        		.setConfig(config.getRecognitionConfig())
	        		.setAudio(recognitionAudio)
	        		.build();

	        response = speechClient.syncRecognize(request);
		} catch (IOException | StatusRuntimeException e) {
			throw new AIServiceException("Failed to recognize speech", e);
		}
		if ((response.getResultsCount() == 0) || (response.getResults(0).getAlternativesCount() == 0)) {
			throw new AIServiceException("No speech");
		}
		String transcript = response.getResults(0).getAlternatives(0).getTranscript();
		AIRequest request = new AIRequest(transcript);
		return request(request, requestExtras, serviceContext);
	}
	
	private final ManagedChannel getChannel() throws IOException {
		if (channel == null) {
			GoogleCredentials creds = GoogleCredentials.getApplicationDefault();
		    creds = creds.createScoped(config.getAuthScope());
		    channel = ManagedChannelBuilder
		    		.forAddress(config.getSpeechApiHost(), config.getSpeechApiPort())
		            .intercept(new ClientAuthInterceptor(creds, Executors.newSingleThreadExecutor()))
		            .build();
		}
		return channel;
	}
	
	private final SpeechBlockingStub getSpeechBlockingStub() throws IOException {
		if (speechClient == null) {
			speechClient = SpeechGrpc.newBlockingStub(getChannel());
		}
		return speechClient;
	}
	
	private RecognitionAudio createRecognitionAudio(InputStream voiceStream) throws IOException {
		return RecognitionAudio.newBuilder().setContent(ByteString.readFrom(voiceStream)).build();
	}
}
