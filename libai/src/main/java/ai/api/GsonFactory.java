package ai.api;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com) https://www.api.ai
 *
 ***********************************************************************************************************************
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import ai.api.model.ResponseMessage;
import ai.api.model.ResponseMessage.MessageType;
import ai.api.model.ResponseMessage.Platform;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Locale;

/** 
 * {@link Gson} object factory used in serialization 
 */
public class GsonFactory {

  private static final Gson DEFAULT_GSON = new GsonBuilder().create();

  private static final Gson PROTOCOL_GSON = new GsonBuilder()
      .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).toPattern())
      .registerTypeAdapter(ResponseMessage.class, new ResponseItemAdapter())
      .registerTypeAdapter(ResponseMessage.MessageType.class, new ResponseMessageTypeAdapter())
      .registerTypeAdapter(ResponseMessage.Platform.class, new ResponseMessagePlatformAdapter())
      .registerTypeAdapter(ResponseMessage.ResponseSpeech.class, new ResponseSpeechDeserializer())
      .create();

  private static final GsonFactory DEFAULT_FACTORY = new GsonFactory();

  /**
   * Get a {@link Gson} object 
   */
  public Gson getGson() {
    return PROTOCOL_GSON;
  }

  /**
   * Create a default factory
   */
  public static GsonFactory getDefaultFactory() {
    return DEFAULT_FACTORY;
  }
  
  private static class ResponseMessagePlatformAdapter implements
    JsonDeserializer<ResponseMessage.Platform>,
    JsonSerializer<ResponseMessage.Platform> {

    @Override
    public JsonElement serialize(Platform src, Type typeOfT, JsonSerializationContext context) {
      return context.serialize(src.getName());
    }

    @Override
    public Platform deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      String name = json.getAsString();
      if (name == null) {
        return Platform.DEFAULT;
      }
      Platform result = Platform.fromName(name);
      if (result == null) {
        throw new JsonParseException(String.format("Unexpected platform name: %s", json));
      }
      return result;
    }
  }
  private static class ResponseMessageTypeAdapter implements
    JsonDeserializer<ResponseMessage.MessageType>,
    JsonSerializer<ResponseMessage.MessageType> {

    @Override
    public JsonElement serialize(MessageType src, Type typeOfT, JsonSerializationContext context) {
      return context.serialize(src.getCode() <= 4 ? src.getCode() : src.getName());
    }

    @Override
    public MessageType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
        throws JsonParseException {
      JsonPrimitive jsonValue = json.getAsJsonPrimitive();
      MessageType result = null;
      if (jsonValue.isNumber()) {
        result = MessageType.fromCode(jsonValue.getAsInt());
      } else {
        result = MessageType.fromName(jsonValue.getAsString());
      }
      if (result == null) {
        throw new JsonParseException(String.format("Unexpected message type value: %s", jsonValue));
      }
      return result;
    }
  }
  
  private static class ResponseItemAdapter implements JsonDeserializer<ResponseMessage>,
      JsonSerializer<ResponseMessage> {

    @Override
    public ResponseMessage deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {
      MessageType messageType = context.deserialize(json.getAsJsonObject().get("type"), MessageType.class);
      return context.deserialize(json, messageType.getType());
    }

    @Override
    public JsonElement serialize(ResponseMessage src, Type typeOfSrc,
        JsonSerializationContext context) {
      return context.serialize(src, src.getClass());
    }
  }

  private static class ResponseSpeechDeserializer implements JsonDeserializer<ResponseMessage> {
    public ResponseMessage.ResponseSpeech deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {

      if (json.isJsonObject() && ((JsonObject) json).get("speech").isJsonPrimitive()) {
        JsonArray array = new JsonArray();
        array.add(((JsonObject) json).get("speech"));
        ((JsonObject) json).add("speech", array);
      }

      return DEFAULT_GSON.fromJson(json, typeOfT);
    }
  }
}
