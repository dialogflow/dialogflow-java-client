# Voice Recognition Client Sample

This is a sample project that illustrates the way to add Google Cloud Speech API
into your api.ai client project.

## 1. Install and set up Google Cloud SDK

As a first step you need to install [Google Cloud SDK](https://cloud.google.com/sdk/).
Please follow the instructions on the SDK home page to get it installed on your
machine.

## 2. Set up speech recognition project

If you still have no a Google Cloud project you may follow the
[official Google instruction](https://cloud.google.com/speech/docs/getting-started#set_up_your_project)
to set it up.

After setting up [Service account](https://cloud.google.com/speech/docs/common/auth#set_up_a_service_account)
make sure that the sample application would be able to find a credentials data.
There are two ways available.

By using a environment variable

    export GOOGLE_APPLICATION_CREDENTIALS=<service-account-key-file>

or by activating service account via `gcloud` tool

    gcloud auth activate-service-account --key-file=<service-account-key-file> 

## 3. Checkout and build the source code

The last version of code always can be found at Github repository:

    git clone https://github.com/api-ai/apiai-java-sdk.git

change a work directory

    cd apiai-java-sdk/samples/clients/voice-client

and run the assembly routine

    mvn package

## 4. Run the application

After build is complete you should have a runnable JAR file in the `target`
directory. It could be run directly by calling java

    java -jar target/voice-client-jar-with-dependencies.jar \
        <api_ai_access_token> [<audio_file> ...]

or you may use a shell script which does right the same thing but in a shorter
way:

    bin/voice-client.sh <api_ai_access_token> [<audio_file> ...]

e.g.

    bin/voice-client.sh 1bea0a262c924f43bf87a88e4a69cd94 audio/how_are_you.raw
