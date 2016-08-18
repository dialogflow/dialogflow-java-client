package ai.api.examples;

/***********************************************************************************************************************
 *
 * API.AI Java SDK - client-side libraries for API.AI
 * =================================================
 *
 * Copyright (C) 2016 by Speaktoit, Inc. (https://www.speaktoit.com)
 * https://www.api.ai
 *
 * *********************************************************************************************************************
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * Text client reads requests line by line
 * from stdandart input.
 */
public class TextClientApplication {

	private static final String INPUT_PROMPT = "> ";
	/**
	 * Default exit code in case of error
	 */
	private static final int ERROR_EXIT_CODE = 1;

	/**
	 * @param args List of parameters:<br>
	 *  First parameters should be valid api key<br>
	 *  Second and the following args should be file names containing 
	 *  audio data.
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			showHelp("Please specify API key", ERROR_EXIT_CODE);
		}
		
		AIConfiguration configuration = new AIConfiguration(args[0]);
		
		// Let's create a unique session id for each application run
		configuration.generateSessionId();
		
		AIDataService dataService = new AIDataService(configuration);
		
		String line;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print(INPUT_PROMPT);
			while (null != (line = reader.readLine())) {

				try {
					AIRequest request = new AIRequest(line);

					AIResponse response = dataService.request(request);

					if (response.getStatus().getCode() == 200) {
						System.out.println(response.getResult().getFulfillment().getSpeech());
					} else {
						System.err.println(response.getStatus().getErrorDetails());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}

				System.out.print(INPUT_PROMPT);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		System.out.println("See ya!");
	}


	/**
	 * Output application usage information to stdout and exit.
	 * No return from function.
	 * @param errorMessage Extra error message. Would be printed to stderr
	 * if not null and not empty.
	 * 
	 */
	private static void showHelp(String errorMessage, int exitCode) {
		if (errorMessage != null && errorMessage.length() > 0) {
			System.err.println(errorMessage);
			System.err.println();
		}
		
		System.out.println("Usage: APIKEY");
		System.out.println();
		System.out.println("APIKEY  Your unique application key");
		System.out.println("        See https://docs.api.ai/docs/key-concepts for details");
		System.out.println();
		System.exit(exitCode);
	}
}
