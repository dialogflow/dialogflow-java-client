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
 
package ai.api.model;

import ai.api.AIServiceException;

import java.io.Serializable;

public class AIError implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private final String message;
    @SuppressWarnings("unused")
	private final AIResponse aiResponse;

    private AIServiceException exception;

    public AIError(final String message) {
        aiResponse = null;

        this.message = message;
    }

    public AIError(final AIServiceException e) {
        aiResponse = e.getResponse();
        message = e.getMessage();
        exception = e;
    }

    public AIError(final AIResponse aiResponse) {
        this.aiResponse = aiResponse;

        if (aiResponse == null) {
            message = "API.AI service returns empty result";
        }
        else if (aiResponse.getStatus() != null) {
            message = aiResponse.getStatus().getErrorDetails();
        } else {
            message = "API.AI service returns error code with empty status";
        }
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        if (exception != null) {
            return exception.toString();
        } else {
            return message;
        }
    }
}
