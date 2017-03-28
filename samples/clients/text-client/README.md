# Text Client Sample

This is a sample project that illustrates the way to create a simple api.ai
client application.

## 1. Checkout and build the source code

The last version of code always can be found at Github repository:

    git clone https://github.com/api-ai/apiai-java-sdk.git

change a work directory

    cd apiai-java-sdk/examples/text-client

and run the assembly routine

    mvn package

## 4. Run the application

After build is complete you should have a runnable JAR file in the `target`
directory. It could be run directly by calling java

    java -jar target/text-client-jar-with-dependencies.jar \
        <api_ai_access_token>

or you may use a shell script which does right the same thing but in a shorter
way:

    bin/text-client.sh <api_ai_access_token>

e.g.

    bin/text-client.sh 1bea0a262c924f43bf87a88e4a69cd94

After running the applocation you will see an input prompt:

    > 

By writing some text requests you will see something like the following:

    > hello
    Hi there, friend!
    > how are you?
    Couldn't be better.
    > See ya!

