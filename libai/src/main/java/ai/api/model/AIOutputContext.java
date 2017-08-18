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

import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Map;

public class AIOutputContext implements Serializable {

	private static final long serialVersionUID = 1L;
	
    @SerializedName("name")
    private String name;

    @SerializedName("parameters")
    private Map<String, JsonElement> parameters;

    @SerializedName("lifespan")
    private Integer lifespan;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Map<String, JsonElement> getParameters() {
        return parameters;
    }

    public void setParameters(final Map<String, JsonElement> parameters) {
        this.parameters = parameters;
    }

    /**
     * Lifespan of the context measured in requests
     * @return count of requests context will live
     */
    public Integer getLifespan() {
        return lifespan;
    }

    /**
     * Lifespan of the context measured in requests
     * @param lifespan count of requests context will live
     */
    public void setLifespan(final Integer lifespan) {
        this.lifespan = lifespan;
    }
}
