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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.api.util.StringUtils;

public class AIRequest extends QuestionMetadata implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @SerializedName("query")
    private String[] query;

    @SerializedName("confidence")
    private float[] confidence;

    @SerializedName("contexts")
    private List<AIContext> contexts;

    @SerializedName("resetContexts")
    private Boolean resetContexts;

    @SerializedName("event")
    private AIEvent event;

    private AIOriginalRequest originalRequest;

    public AIRequest() {
    }

    public AIRequest(final String query) {
        setQuery(query);
    }

    public void setQuery(final String query) {
        this.query = StringUtils.isEmpty(query) ? null : new String[]{query};
        confidence = null;
    }

    public void setQuery(final String[] query, final float[] confidence) {
        if (query == null) {
            throw new IllegalStateException("Query array must not be null");
        }

        if (confidence == null && query.length > 1) {
            throw new IllegalStateException("Then confidences array is null, query must be one or zero item length");
        }

        if (confidence != null && query.length != confidence.length) {
            throw new IllegalStateException("Query and confidence arrays must be equals size");
        }

        this.query = query;
        this.confidence = confidence;
    }

    public float[] getConfidence() {
        return confidence;
    }

    public void setConfidence(final float[] confidence) {
        this.confidence = confidence;
    }

    public void setResetContexts(final Boolean resetContexts) {
        this.resetContexts = resetContexts;
    }

    public Boolean getResetContexts() {
        return resetContexts;
    }

    public void setContexts(final List<AIContext> contexts) {
        this.contexts = contexts;
    }

    public void addContext(final AIContext aiContext) {
        if (contexts == null) {
            contexts = new ArrayList<>(1);
        }

        contexts.add(aiContext);
    }


    public void setEvent(AIEvent event) {
        this.event = event;
    }

    /**
     * Full request coming from the integrated platform (Facebook Messenger, Slack, etc.) 
     * @return <code>null</code> if request is not defined
     */
    public final AIOriginalRequest getOriginalRequest() {
      return originalRequest;
    }

    /**
     * Set full request coming from the integrated platform (Facebook Messenger, Slack, etc.)
     * @param originalRequest <code>null</code> if request is not defined
     */
    public final void setOriginalRequest(AIOriginalRequest originalRequest) {
      this.originalRequest = originalRequest;
    }

    @Override
    public String toString() {
        return String.format("AIRequest{query=%s, resetContexts=%s, language='%s', timezone='%s'}",
                Arrays.toString(query),
                resetContexts,
                getLanguage(),
                getTimezone());
    }
}
