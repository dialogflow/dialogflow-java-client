package ai.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ai.api.AIConfiguration;
import ai.api.AIDataService;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;

/**
 * Text client reads requests line by line from stdandart input.
 */
public class TextClientApplication {
	
	private static final String INPUT_PROMPT = "> ";
	/**
	 * Default exit code in case of error
	 */
	private static final int ERROR_EXIT_CODE = 1;
	
	public static String getAPIAIReponse(String message) {
		AIConfiguration configuration = new AIConfiguration("29ae50f9b7494eefb4aadd2d4b069fa9");
		
		AIDataService dataService = new AIDataService(configuration);
		try {
			AIRequest request = new AIRequest(message);
			
			AIResponse response = dataService.request(request);
			
			if (response.getStatus().getCode() == 200) {
				System.out.println(response.getId());
				//System.out.println(response.getResult().getContexts().get(0).getName());
				System.out.println(response.getSessionId());
				System.out.println(response.getResult().getFulfillment().getSpeech());
				return response.getResult().getFulfillment().getSpeech();
			} else {
				System.err.println(response.getStatus().getErrorDetails());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "No Response";
	}
	
	/**
	 * @param args
	 *            List of parameters:<br>
	 *            First parameters should be valid api key<br>
	 *            Second and the following args should be file names containing audio data.
	 */
	public static void main(String[] args) {
		
		AIConfiguration configuration = new AIConfiguration("29ae50f9b7494eefb4aadd2d4b069fa9");
		
		AIDataService dataService = new AIDataService(configuration);
		
		String line;
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			System.out.print(INPUT_PROMPT);
			while (null != (line = reader.readLine())) {
				
				try {
					AIRequest request = new AIRequest(line);
					
					AIResponse response = dataService.request(request);
					
					if (response.getStatus().getCode() == 200) {
						System.out.println("Id = "+response.getId());
						//System.out.println(response.getResult().getContexts().get(0).getName());
						System.out.println(response.getSessionId());
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
	 * Output application usage information to stdout and exit. No return from function.
	 * 
	 * @param errorMessage
	 *            Extra error message. Would be printed to stderr if not null and not empty.
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
