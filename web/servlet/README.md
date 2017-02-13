# Servlets based AI data service

This library contains basic servlets implementations to be used in your own web-service.

## Web-hook basic implementation

To create custom web-hook implementation you have to create new servlet class inherited from
[AIWebhookServlet](https://github.com/api-ai/apiai-java-sdk/blob/master/web/servlet/src/main/java/ai/api/web/AIWebhookServlet.java) and override abstract method `doWebhook(AIWebhookRequest input, Fulfillment output)`:

```java
import javax.servlet.annotation.WebServlet;

import ai.api.model.Fulfillment;
import ai.api.web.AIWebhookServlet;

@WebServlet("/webhook")
public class MyWebhookServlet extends AIWebhookServlet {
  @Override
  protected void doWebhook(AIWebhookRequest input, Fulfillment output) {
    output.setSpeech("You said: " + input.getResult().getFulfillment().getSpeech());
  }
}
```
Run web-server and ensure that your web-hook responses correctly:

    $ curl -H "Content-Type: application/json; charset=utf-8" \
        --data '{"result":{"fulfillment":{"speech":"Some speech here"}}}' \
        "http://localhost:8080/webhook"
    {"speech":"You said: Some speech here"}
    

## AI data service basic implementation

To create custom data service implementation you have to create new servlet class inherited from
[AIServiceServlet](https://github.com/api-ai/apiai-java-sdk/blob/master/web/servlet/src/main/java/ai/api/web/AIServiceServlet.java). Servlet must be initialized with `ServiceServletExample.PARAM_API_AI_KEY`
parameter, and it's value must be equals to your agent api.ai access token.

```java
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ai.api.AIServiceException;
import ai.api.model.AIResponse;
import ai.api.web.AIServiceServlet;

@WebServlet(urlPatterns = {"/ai"},
    initParams = {
        @WebInitParam(name = ServiceServletExample.PARAM_API_AI_KEY,
        value = "1bea0a262c924f43bf87a88e4a69cd94")
    })
public class MyServiceServlet extends AIServiceServlet {
  /**
   * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
   */
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    try {
      AIResponse aiResponse = request(request.getParameter("query"), request.getSession());
      response.setContentType("text/plain");
      response.getWriter().append(aiResponse.getResult().getFulfillment().getSpeech());
    } catch (AIServiceException e) {
      e.printStackTrace();
    }
  }
}
```
Run web-server and ensure that your service responses correctly:

    $ curl "http://localhost:8080/ai?query=hello" 
    Howdy.