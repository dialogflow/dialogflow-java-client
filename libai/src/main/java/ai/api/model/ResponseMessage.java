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

import java.lang.reflect.Type;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;

public abstract class ResponseMessage {
	
	@Expose
	private int type;
	
	public static enum MessageType {
		SPEECH     (0, ResponseSpeech.class),
		CARD       (1, ResponseCard.class),
		QUICK_REPLY(2, ResponseQuickReply.class),
		IMAGE      (3, ResponseImage.class),
		PAYLOAD    (4, ResponsePayload.class);
		
		private final int code;
		private final Type type;
		
		private MessageType(int code, Type curClass) {
			this.code = code;
			this.type = curClass;
		}
		
		public int getCode() {
			return this.code;
		}
		
		public Type getType() {
			return type;
		}
	}
	
	public static class ResponseSpeech extends ResponseMessage {

		@Expose
		public List<String> speech;
				
		public List<String> getSpeech() {
			return this.speech;
		}
		
		public void setSpeech(List<String> speech) {
			this.speech = speech;
		}
	}
	
	public static class ResponseCard extends ResponseMessage {

		@Expose
		private String title;
		
		@Expose
		private String subtitle;
		
		@Expose
		private String imageUrl;
		
		@Expose
		private List <Button> buttons;
				
		public String getTitle() {
			return this.title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getSubtitle() {
			return this.subtitle;
		}
		
		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
		
		public String getImageUrl() {
			return this.imageUrl;
		}
		
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		
		public List <Button> getButtons() {
			return this.buttons;
		}
		
		public void setButtons(List <Button> buttons) {
			this.buttons = buttons;
		}
		
		
		public class Button {
			
			@Expose
			private String text;
			
			@Expose
			private String postback;
						
			public Button(String text, String postback) {
				this.text = text;
				this.postback = postback;
			}
			
			public void setText(String text) {
				this.text = text;
			}
			
			public String getText() {
				return this.text;
			}
			
			public void setPostback(String postback) {
				this.postback = postback;
			}
			
			public String getPostback() {
				return this.postback;
			}
		}
	}
	
	public static class ResponseQuickReply extends ResponseMessage {

		@Expose
		private String title;
		
		@Expose
		private List <String> replies;
				
		public List <String> getReplies() {
			return this.replies;
		}
		
		public void setReplies(List <String> replies) {
			this.replies = replies;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getTitle() {
			return this.title;
		}
	}
	
	public static class ResponseImage extends ResponseMessage  {

		@Expose
		private String imageUrl;
		
		public String getImageUrl() {
			return this.imageUrl;
		}
		
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
	}
	
	public static class ResponsePayload extends ResponseMessage  {

		@Expose
		private JsonObject payload;
		
		public JsonObject getPayload() {
			return this.payload;
		}
		
		public void setPayload(JsonObject payload) {
			this.payload = payload;
		}
	}
}
