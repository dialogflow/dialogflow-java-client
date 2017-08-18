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
 
package ai.api.examples;

import java.io.File;
import java.io.FileInputStream;

import ai.api.model.AIResponse;
import ai.api.speech.gcp.GcpAIConfiguration;
import ai.api.speech.gcp.GcpAIDataService;

/**
 * Voice client sends audio requests from files passed by command line arguments.
 */
public class VoiceClientApplication {

  /**
   * Default exit code in case of error
   */
  private static final int ERROR_EXIT_CODE = 1;

  /**
   * @param args List of parameters:<br>
   *        First parameters should be valid api key<br>
   *        Second and the following args should be file names containing audio data.
   */
  public static void main(String[] args) {
    if (args.length < 1) {
      showHelp("Please specify API key", ERROR_EXIT_CODE);
    }

    GcpAIConfiguration configuration = new GcpAIConfiguration(args[0]);

    GcpAIDataService dataService = new GcpAIDataService(configuration);

    for (int i = 1; i < args.length; ++i) {
      File file = new File(args[i]);
      try (FileInputStream inputStream = new FileInputStream(file)) {

        System.out.println(file);

        AIResponse response = dataService.voiceRequest(inputStream);

        if (response.getStatus().getCode() == 200) {
          System.out.println(response.getResult().getFulfillment().getSpeech());
        } else {
          System.err.println(response.getStatus().getErrorDetails());
        }
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }


  /**
   * Output application usage information to stdout and exit. No return from function.
   * 
   * @param errorMessage Extra error message. Would be printed to stderr if not null and not empty.
   * 
   */
  private static void showHelp(String errorMessage, int exitCode) {
    if (errorMessage != null && errorMessage.length() > 0) {
      System.err.println(errorMessage);
      System.err.println();
    }
    System.out.println("Usage: APIKEY FILE...");
    System.out.println();
    System.out.println("APIKEY  Your unique application key");
    System.out.println("        See https://docs.api.ai/docs/key-concepts for details");
    System.out.println();
    System.out.println("FILE    Path to file containing raw audio data");
    System.out
        .println("        See https://docs.api.ai/docs/query#post-query-multipart for details");
    System.out.println();

    System.exit(exitCode);
  }
}
