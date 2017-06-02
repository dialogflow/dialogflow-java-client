# api-ai-twilio
Sample agent to integrate [Api.ai](http://api.ai) with [Twilio SMS](https://twilio.com) service.

# Deploy

Insert API key to the configuration file: src/main/resources/twilio.properties:
```
apiai.accessToken=<YOUR_CLIENT_ACCESS_TOKEN>
```

Run maven build:
```
mvn clean install
```

Deploy war file to the servlet container.

Go to www.twilio.com. Configure your number and set the following url as a callback on incoming SMS:
```
<YOUR_SERVER_URL>/twilio/sms
```
