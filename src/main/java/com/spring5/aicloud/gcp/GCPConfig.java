/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * @author javau
 */
public class GCPConfig {
}

/*
Spring Boot & Google Cloud Pub/Sub Integration
This document walks you through a complete example of integrating a Spring Boot application with Google Cloud Pub/Sub. You'll learn how to publish messages to a topic and subscribe to a subscription to receive messages.

Prerequisites
Java Development Kit (JDK): Version 17 or higher.

Apache Maven: To build the project and manage dependencies.

Google Cloud SDK (gcloud): Installed and configured on your machine.

A Google Cloud Platform (GCP) Project: With the Pub/Sub API enabled.

Step 1: Google Cloud Setup
Before running the application, you need to set up a Pub/Sub topic and a corresponding subscription in your GCP project.

Authenticate with gcloud:
Open your terminal and run:

gcloud auth application-default login

This command handles authentication, allowing your local application to securely interact with Google Cloud APIs.

Create a Pub/Sub Topic:
A topic is a named resource to which messages are sent. Choose a name for your topic (e.g., my-cool-topic) and run:

gcloud pubsub topics create my-cool-topic

Create a Pub/Sub Subscription:
A subscription is a named resource representing the stream of messages from a single, specific topic. Choose a name for your subscription (e.g., my-cool-subscription) and link it to your topic:

gcloud pubsub subscriptions create my-cool-subscription --topic=my-cool-topic

Step 2: Project Files
Here are the complete code files for the Spring Boot application.

pom.xml: Defines the project dependencies. The key dependency is spring-cloud-gcp-starter-pubsub.

application.properties: Configures your GCP Project ID and the topic/subscription names.

PubSubExampleApplication.java: The main Spring Boot application class.

PubSubPublisherController.java: A REST controller with an endpoint to publish messages.

MessageSubscriber.java: A component that listens for and processes incoming messages from your subscription.

Step 3: Running the Application
Update application.properties: Make sure you replace your-gcp-project-id with your actual Google Cloud Project ID.

Build and Run: Navigate to the project's root directory in your terminal and run the application using Maven:

mvn spring-boot:run

Step 4: Testing the Integration
Publish a Message:
Once the application is running, open a new terminal or a tool like Postman and send a POST request to the publisher endpoint.

# Using curl
curl -X POST -H "Content-Type: application/json" -d "Hello, Pub/Sub!" "http://localhost:8080/publish?message=Hello-World"

# Or provide a message in the request body
curl -X POST -H "Content-Type: text/plain" -d "This is a test message." http://localhost:8080/publish

Check the Console:
Look at the console output of your running Spring Boot application. You should see the log from MessageSubscriber indicating that the message was received and processed.

INFO 12345 --- [sub-executor-0] c.e.pubsub.MessageSubscriber         : Message received! Payload: Hello-World

This confirms that the entire publish-subscribe loop is working correctly.
 */
