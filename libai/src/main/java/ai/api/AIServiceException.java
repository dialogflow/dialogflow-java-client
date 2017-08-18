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

import ai.api.util.StringUtils;
import ai.api.model.AIResponse;

public class AIServiceException extends Exception {

	private static final long serialVersionUID = 1L;
	
    private final AIResponse aiResponse;

    public AIServiceException() {
        aiResponse = null;
    }

    public AIServiceException(final String detailMessage, final Throwable throwable) {
        super(detailMessage, throwable);
        aiResponse = null;
    }

    public AIServiceException(final String detailMessage) {
        super(detailMessage);
        aiResponse = null;

    }

    public AIServiceException(final AIResponse aiResponse) {
        super();
        this.aiResponse = aiResponse;
    }

    public AIResponse getResponse() {
        return aiResponse;
    }

    @Override
    public String getMessage() {
        if ((aiResponse != null) && (aiResponse.getStatus() != null)) {

            final String errorDetails = aiResponse.getStatus().getErrorDetails();
            if (!StringUtils.isEmpty(errorDetails)) {
                return errorDetails;
            }
        }
        return super.getMessage();
    }
}
