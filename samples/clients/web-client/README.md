# Web Client Sample

This is a sample project that illustrates the way to create a simple api.ai
client application.

## 1. Checkout and build the source code

The last version of code always can be found at Github repository:

    git clone https://github.com/api-ai/apiai-java-sdk.git

change a work directory

    cd apiai-java-sdk/samples/clients/web-client

and run the assembly routine to create a WAR file

    mvn package

## 4. Deploy the application

After build is complete you should have a `web-client.war` file in the `target`
directory. Deploy it to server and open the deployed application in your browser. For instance if
you have a local running server the URL may be a following

    http://localhost:8080/web-client/

