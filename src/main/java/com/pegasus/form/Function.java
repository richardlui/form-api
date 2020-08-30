package com.pegasus.form;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("form")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        // Parse query parameter
        final String query = request.getQueryParameters().get("filename");
        final String filename = request.getBody().orElse(query);
        Boolean errorFound = false;
        String errorMsg = "";

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .executor(Executors.newFixedThreadPool(10))
                    .followRedirects(Redirect.NEVER)
                    .priority(1) // HTTP/2 priority
                    .version(Version.HTTP_2)
                    .build();
            //byte[] localImageBytes = Files.readAllBytes(Paths.get("/Users/richard_lui/temp/Vincent/docs/"+filename));
            URI uri = new URI("https://scan5354.blob.core.windows.net/scan/"+filename);
            //uri.toURL().openStream().readAllBytes()
            byte[] localImageBytes = uri.toURL().openStream().readAllBytes();
            
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    //.uri(URI.create("https://ocr-dev-09238.cognitiveservices.azure.com/vision/v3.0/read/analyze?language=en"))
                    .uri(URI.create("https://ocr-dev-09238.cognitiveservices.azure.com/vision/v3.1-preview.1/read/analyze?language=zh-Hans"))
                    .timeout(Duration.ofSeconds(30))
                    .method("POST", BodyPublishers.ofByteArray(localImageBytes))
                    .header("Content-Type",  "application/octet-stream")
                    .header("Ocp-Apim-Subscription-Key", "3c9c8481044648d5957f7bbb08377693")
                    .build();
            HttpResponse<String> response = null;
            try {
                System.out.println("postHttpRequest uri: " +  httpRequest.uri() + " request headers: " + httpRequest.headers());
                response = httpClient.send(httpRequest,  HttpResponse.BodyHandlers.ofString());
                System.out.println("postHttpRequest response body: " + response.body());
                System.out.println("postHttpRequest response header: " + response.headers());
                System.out.println("postHttpRequest status code: " + response.statusCode());
            }
            catch (Exception e) {
                System.out.println("ERROR");
                System.out.println(e);
                errorFound = true;
                errorMsg = e.getMessage();
            }
            
            Thread.sleep(10000);
            
            // get the read result operation id
            String url = response.headers().firstValue("operation-location").get();
            // Now call readResult
            httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .header("Ocp-Apim-Subscription-Key", "3c9c8481044648d5957f7bbb08377693")
                    .build();
            response = null;
            String body = "";
            try {
                System.out.println("postHttpRequest uri: " +  httpRequest.uri() + " request headers: " + httpRequest.headers());
                response = httpClient.send(httpRequest,  HttpResponse.BodyHandlers.ofString());
                body = response.body();
                System.out.println("postHttpRequest response body: " + body);
                System.out.println("postHttpRequest response header: " + response.headers());
                System.out.println("postHttpRequest status code: " + response.statusCode());
            }
            catch (Exception e) {
                System.out.println("ERROR");
                System.out.println(e);
                errorFound = true;
                errorMsg = e.getMessage();
            }
            
            //extractCompanyName(body);
            
            if (filename.equals("1464_001.pdf")) {
                ScanProcessor processor = new ProStretchProcessor(body);
                processor.process();
                System.out.println("PONumber = " + processor.getPoNumber());
            }
            if (errorFound) {
                return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(errorMsg)
                    .build();
            } else {
                return request.createResponseBuilder(HttpStatus.OK)
                    .body(body)
                    .build();
            }
        }
        catch (Exception e) {
            System.out.println("Encountered error");
            System.out.println(e);
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Encountered error: " + e.getMessage())
                .build();
        }
    }
}
