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

import ai.api.model.GoogleAssistantResponseMessages.ResponseChatBubble;
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

  private static final Gson SIMPLIFIED_GSON;

  private static final Gson PROTOCOL_GSON;

  private static final GsonFactory DEFAULT_FACTORY = new GsonFactory();
  
  static {
    GsonBuilder gsonBuilder = new GsonBuilder()
        .setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).toPattern())
        .registerTypeAdapter(ResponseMessage.class, new ResponseItemAdapter())
        .registerTypeAdapter(ResponseMessage.MessageType.class, new ResponseMessageTypeAdapter())
        .registerTypeAdapter(ResponseMessage.Platform.class, new ResponseMessagePlatformAdapter());
    SIMPLIFIED_GSON = gsonBuilder.create();
    
    gsonBuilder.registerTypeAdapter(ResponseMessage.ResponseSpeech.class, new ResponseSpeechAdapter());
    gsonBuilder.registerTypeAdapter(ResponseChatBubble.class, new ResponseChatBubbleAdapter());
    PROTOCOL_GSON = gsonBuilder.create();
  }

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

  private static class ResponseSpeechAdapter implements JsonDeserializer<ResponseMessage>,
  JsonSerializer<ResponseMessage> {
    public ResponseMessage.ResponseSpeech deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {

      if (json.isJsonObject()) {
        final JsonObject jsonObject = (JsonObject) json;
        final JsonElement speechValue = jsonObject.get("speech");
        if (speechValue.isJsonPrimitive()) {
          JsonArray array = new JsonArray();
          array.add(speechValue);
          jsonObject.add("speech", array);
        }
      }

      return SIMPLIFIED_GSON.fromJson(json, typeOfT);
    }

    @Override
    public JsonElement serialize(ResponseMessage src, Type typeOfSrc, JsonSerializationContext context) {
      return SIMPLIFIED_GSON.toJsonTree(src, ResponseMessage.class);
    }
  }

  private static class ResponseChatBubbleAdapter implements JsonDeserializer<ResponseChatBubble>,
  JsonSerializer<ResponseChatBubble> {
    public ResponseChatBubble deserialize(JsonElement json, Type typeOfT,
        JsonDeserializationContext context) throws JsonParseException {

      if (json.isJsonObject()) {
        final JsonObject jsonObject = (JsonObject) json;
        if (jsonObject.has("textToSpeech")) {
          JsonArray items = jsonObject.getAsJsonArray("items");
          if (items == null) {
            items = new JsonArray(1);
            jsonObject.add("items", items);
          }
          JsonObject item = new JsonObject();
          item.add("textToSpeech", jsonObject.get("textToSpeech"));
          item.add("ssml", jsonObject.get("ssml"));
          item.add("displayText", jsonObject.get("displayText"));
          items.add(item);
        }
      }

      return SIMPLIFIED_GSON.fromJson(json, typeOfT);
    }

    @Override
    public JsonElement serialize(ResponseChatBubble src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject result = (JsonObject)SIMPLIFIED_GSON.toJsonTree(src, ResponseMessage.class);
      JsonArray items = result.getAsJsonArray("items");
      if ((items != null) && (items.size() == 1)) {
        JsonObject item = (JsonObject)items.get(0);
        result.add("textToSpeech", item.get("textToSpeech"));
        result.add("ssml", item.get("ssml"));
        result.add("displayText", item.get("displayText"));
        result.remove("items");
      }
      return result;
    }
  }
}
