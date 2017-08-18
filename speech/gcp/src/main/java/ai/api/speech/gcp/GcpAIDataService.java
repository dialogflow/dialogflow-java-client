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

import java.io.IOException;
import java.io.InputStream;

import com.google.cloud.speech.spi.v1.SpeechClient;
import com.google.cloud.speech.v1.RecognitionAudio;
import com.google.cloud.speech.v1.RecognizeResponse;
import com.google.protobuf.ByteString;

import ai.api.AIDataService;
import ai.api.AIServiceContext;
import ai.api.AIServiceException;
import ai.api.RequestExtras;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import io.grpc.StatusRuntimeException;

/**
 * AIDataService with Google Speech API support
 */
public class GcpAIDataService extends AIDataService {
	
	private final GcpAIConfiguration config;

	/**
     * Create new instance of AI data service
     * @param config Service configuration
     */
	public GcpAIDataService(GcpAIConfiguration config) {
		super(config);
		this.config = config.clone();
	}

	/**
	 * Create new instance of AI data service
	 * @param config Service configuration
	 * @param serviceContext Default service context
	 */
	public GcpAIDataService(GcpAIConfiguration config, AIServiceContext serviceContext) {
		super(config, serviceContext);
		this.config = config.clone();
	}

	/**
	 * @see AIDataService#voiceRequest(InputStream, RequestExtras, AIServiceContext)
	 */
	@Override
	public AIResponse voiceRequest(InputStream voiceStream, RequestExtras requestExtras,
			AIServiceContext serviceContext) throws AIServiceException {
		
	  RecognizeResponse response;
		try {
		    SpeechClient speechClient = SpeechClient.create();
			RecognitionAudio recognitionAudio = createRecognitionAudio(voiceStream);

	        response = speechClient.recognize(config.getRecognitionConfig(), recognitionAudio);
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
	
	private RecognitionAudio createRecognitionAudio(InputStream voiceStream) throws IOException {
		return RecognitionAudio.newBuilder().setContent(ByteString.readFrom(voiceStream)).build();
	}
}
