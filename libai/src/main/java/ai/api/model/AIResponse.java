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

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Date;

public class AIResponse implements Serializable {

	private static final long serialVersionUID = 1L;
	
    /**
     * Unique identifier of the result.
     */
    @SerializedName("id")
    private String id;

    @SerializedName("timestamp")
    private Date timestamp;
    
    @SerializedName("lang")
    private String lang;

    /**
     * Result object
     */
    @SerializedName("result")
    private Result result;

    @SerializedName("status")
    private Status status;
    
    @SerializedName("sessionId")
    private String sessionId;

    /**
     * Unique identifier of the result.
     */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Date and time of the request in UTC timezone using ISO-8601 format.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Agent's language.
     * @return Language tag. See <a href="https://docs.api.ai/docs/languages">languages</a> 
     * for details
     */
    public String getLang() {
      return lang;
    }
    
    public void setLang(String lang) {
      this.lang = lang;
    }

    /**
     * Contains the results of the natual language processing.
     */
    public Result getResult() {
        return result;
    }

    public void setResult(final Result result) {
        this.result = result;
    }

    /**
     * Contains data on how the request succeeded or failed.
     */
    public Status getStatus() {
        return status;
    }

    public void setStatus(final Status status) {
        this.status = status;
    }
    
    /**
     * Session ID
     */
    public String getSessionId() {
    	return sessionId;
    }
    
    public void setSessionId(String sessionId) {
    	this.sessionId = sessionId;
    }

    public boolean isError() {
        if (status != null && status.getCode() != null && status.getCode() >= 400) {
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("AIResponse{id='%s', timestamp=%s, result=%s, status=%s, sessionId=%s}",
                id,
                timestamp,
                result,
                status,
        		sessionId);
    }

    public void cleanup() {
        if (result != null) {
            result.trimParameters();
        }
    }
}
