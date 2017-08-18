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
 
package ai.api.twilio.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.servlet.ServletException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import ai.api.twilio.TwilioSmsServlet;

public class SmsServletTest {
	private TwilioSmsServlet servlet;
	private MockHttpServletRequest request;
	private MockHttpServletResponse response;

	@Before
	public void setUp() {
		servlet = new TwilioSmsServlet();
		request = new MockHttpServletRequest();
		response = new MockHttpServletResponse();
	}

	@Ignore
	@Test
	public void sendSms() throws ServletException, IOException {
		request.addParameter("From", "+79131112233");
		request.addParameter("Body", "test1");

		servlet.service(request, response);

		assertEquals("application/xml", response.getContentType());
		assertEquals("<Response><Message>response1</Message></Response>", response.getContentAsString());
	}
}
