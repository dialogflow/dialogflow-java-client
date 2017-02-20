package ai.api.model;
/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 ***********************************************************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 ***********************************************************************************************************************/

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import ai.api.util.StringUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Fulfillment implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @SerializedName("speech")
    private String speech;
    
    @SerializedName("messages")
    private List<ResponseMessage> messages;
    
    @SerializedName("displayText")
    private String displayText;

    @SerializedName("data")
    private Map<String,JsonElement> data;

    @SerializedName("source")
    private String source;
    
    @SerializedName("contextOut")
    private List<AIOutputContext> contextOut;

    public String getSpeech() {
        return speech;
    }

    public void setSpeech(final String speech) {
        this.speech = speech;
    }
    
    public List<ResponseMessage> getMessages() {
    	return messages;
    }
    
    public void getMessages(List<ResponseMessage> messages) {
    	this.messages = messages;
    }
    
    public Map<String,JsonElement> getData() {
        return data;
    }

    public void setData(final Map<String,JsonElement> data) {
        this.data = data;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(final String displayText) {
        this.displayText = displayText;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }


    public List<AIOutputContext> getContextOut() {
        return contextOut;
    }

    public AIOutputContext getContext(final String name) {
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("name argument must be not empty");
        }

        if (contextOut == null) {
            return null;
        }

        for (final AIOutputContext c : contextOut) {
            if (name.equalsIgnoreCase(c.getName())) {
                return c;
            }
        }

        return null;
    }
    
    public void setContextOut(final List<AIOutputContext> contextOut) {
        this.contextOut = contextOut;
    }
    
    @Override
    public String toString(){
    	return String.format("Metadata{speech='%s', messages=%s displayText=%s, data=%s,"
    			+ "source=%s, contextOut=%s}",
			speech,
			messages,
			displayText,
			data,
			source,
			contextOut);
    }
}
