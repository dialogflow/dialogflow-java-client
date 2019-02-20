# DEPRECATED Java SDK for api.ai

| Deprecated |
|-------|
| This Dialogflow client library and Dialogflow API V1 [have been deprecated and will be shut down on October 23th, 2019](https://blog.dialogflow.com/post/migrate-to-dialogflow-api-v2/). Please migrate to Dialogflow API V2 and the [v2 client library](https://cloud.google.com/dialogflow-enterprise/docs/reference/libraries/java) |

[![Release status](https://travis-ci.org/dialogflow/dialogflow-java-client.svg?branch=master)](https://travis-ci.org/api-ai/apiai-java-client) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/ai.api/libai/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ai.api/libai)

The API.AI Java SDK makes it easy to integrate speech recognition with [API.AI](http://www.api.ai) natural language processing API on Android devices. API.AI allows using voice commands and integration with dialog scenarios defined for a particular agent in API.AI.


Authentication is accomplished through setting the client access token when initializing an [AIConfiguration](https://github.com/api-ai/apiai-java-sdk/blob/master/libai/src/main/java/ai/api/AIConfiguration.java) object. The client access token specifies which agent will be used for natural language processing.

**Note:** The API.AI Java SDK only makes query requests, and cannot be used to manage entities and intents. Instead, use the API.AI user interface or REST API to  create, retrieve, update, and delete entities and intents.


# Tutorial
This section contains a detailed tutorial on how to work with libai. This tutorial was written for [IntelliJ IDEA](https://developer.android.com/sdk/installing/studio.html).


## Quick Start
1. Create an API.AI agent with entities and intents, or use one that you've already created. See the API.AI documentation for instructions on how to do this. 
2. Open [IntelliJ IDEA](https://developer.android.com/sdk/installing/studio.html).
3. From the start screen (or **File** menu) , choose **Open...**.<br/> ![Open project](docs/images/OpenProject.png)
4. In the Open project dialog, fill **Path** to **apiai-java-sdk** directory, then expand exmaples directory and choose one of the client examples. Then click **Ok**.<br/> ![Open project dialog](docs/images/OpenProjectDialog.png)
5. Open **Run**, choose **Edit Configuration**. In the Run/Debug Configuration Dialog, fill **program arguments** with your **Client access token**<br/> ![Api keys](docs/images/apiKeys.png)
6. If there are no errors, you can get the result using **Idea Input/Output** to make text request (**text-client**).

## Work with library source code 
If you want to work with library source code, for some reason, follow these steps:

1. First of all, execute the steps specified in **Quick Start**. Only then go to the next step here.
2.  Open **File**, choose **Project Structure...**, choose **Modules**. Add new module **libai**. After this choose **text-client**(or **voice client**) module. **Remove** Maven: ai.api.libai:1.1.0 and **Add** Module dependency libai. After this click **Apply**.<br/> ![ProjectStructure](docs/images/ProjectStructure.png)
3. Try to Run. If there are no errors, you can get the result using **Idea Input/Output** to make text request (**text-client**).




