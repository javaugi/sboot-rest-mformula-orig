/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.spring5.streaming;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.springframework.stereotype.Service;

/*
🚀 Java Support for JavaScript: GraalVM is the Answer
The short answer to your question is Yes, modern Java (and thus Spring Boot) fully supports calling JavaScript, but the method has changed:

Old Method (Deprecated): Java 8 introduced the Nashorn engine (a successor to Rhino) to execute JavaScript via the JSR 223 Scripting API. However, 
    Nashorn was removed entirely in JDK 15. If you are using a modern version of Spring Boot (which typically uses JDK 17 or newer), Nashorn is 
    not an option without manually adding the open-source community version.

New, Recommended Method (Current): Use GraalVM JavaScript (GraalJS) along with the Polyglot API. GraalVM is a high-performance runtime that 
    allows Java to seamlessly call JavaScript, Python, R, and other languages, sharing memory and data structures efficiently.

1. Calling JavaScript/Node.js Code (Recommended: GraalJS)
    GraalVM's Polyglot API allows you to treat JavaScript code as if it were a native Java function call, with high performance
2. Calling an External Node.js Service (Microservice Architecture)
    If your JavaScript or Node.js code is a standalone service (e.g., a microservice running its own web server), the most common and robust way 
        to integrate it is through standard network communication. This approach is preferred for architectural decoupling and scalability.

Implementation:
    (1) Node.js Service: Runs a simple HTTP server (e.g., using Express) that exposes a REST endpoint:
        JavaScript
        // Node.js (Express)
        app.post('/api/process-data', (req, res) => {
            // ... perform complex JS logic ...
            res.json({ status: 'ok', result: 'processed' });
        });
    (2) Spring Boot Service: Uses a REST client (like RestTemplate or the modern WebClient) to call the Node.js endpoint:
        Example: NodeJsClientService

 */

 /*
Summary of Integration Methods
Method              Description             Best For            Pros            Cons
GraalVM Polyglot    APIExecutes JavaScript directly inside the JVM/Spring Boot process.High-performance, low-latency, 
    small JavaScript snippets/libraries.Extremely fast, no network overhead, 
    seamless data sharing.Requires GraalVM setup (though can run on standard JDK), increases JVM memory footprint.
REST/WebClientCalls an external Node.js service via HTTP.   Large, complex Node.js applications or architectural decoupling 
    (microservices).Decouples languages, allows independent scaling and deployment. Network latency overhead, 
    requires marshaling/unmarshaling data (JSON).
 */
@Service
public class JavaScriptExecutionService {

    public String executeJsFunction(int a, int b) {
        // 1. Create a GraalVM Context for the JavaScript language
        try (Context context = Context.create("js")) {

            // 2. Define the JavaScript function as a string
            String jsCode
                    = "(function(num1, num2) {"
                    + "  let result = num1 * 10 + num2;"
                    + "  return 'JS Result: ' + result.toString();"
                    + "})";

            // 3. Evaluate the code to get a JavaScript Value (the function object)
            Value jsFunction = context.eval("js", jsCode);

            // 4. Execute the function, passing Java values as arguments
            Value result = jsFunction.execute(a, b);

            // 5. Convert the JavaScript result back to a Java String
            return result.asString();

        } catch (Exception e) {
            // Handle execution errors
            return "Error executing JS: " + e.getMessage();
        }
    }
}
