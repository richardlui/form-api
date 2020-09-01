package com.pegasus.form.http;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.Executors;

import com.azure.core.http.HttpMethod;
import com.azure.core.util.FluxUtil;

import reactor.core.publisher.Mono;

public class JDKHttpClient implements com.azure.core.http.HttpClient {

    private final HttpClient client;
    
    public JDKHttpClient() {
        client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .executor(Executors.newFixedThreadPool(10))
                .followRedirects(Redirect.NEVER)
                .priority(1) // HTTP/2 priority
                .version(Version.HTTP_2)
                .build();
    }
    
    @Override
    public Mono<com.azure.core.http.HttpResponse> send(com.azure.core.http.HttpRequest request) {
        System.out.println("Request URL: "+ request.getUrl());
        System.out.println("Request Method: "+ request.getHttpMethod());
        System.out.println("Request header: " + request.getHeaders());
        System.out.println("API Key: " + request.getHeaders().get("Ocp-Apim-Subscription-Key").getValue());
        
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder()
            .uri(URI.create(request.getUrl().toString()))
            .timeout(Duration.ofSeconds(30))
            //.method(request.getHttpMethod().toString(), BodyPublishers.ofString("{\"source\":\"https://scan5354.blob.core.windows.net/1467/1467_001.pdf\"}"))
            .header("Content-Type",  "application/json")
            .header("Ocp-Apim-Subscription-Key", request.getHeaders().get("Ocp-Apim-Subscription-Key").getValue());
        
        Mono<byte[]> bodyMono = (request.getBody() != null)
                ? FluxUtil.collectBytesInByteBufferStream(request.getBody())
                : Mono.just(new byte[0]);
                
        return bodyMono.flatMap(bodyBytes -> {
            if (request.getHttpMethod() == HttpMethod.GET) {
                httpRequestBuilder.GET();
            } else if (request.getHttpMethod() == HttpMethod.POST) {
                httpRequestBuilder.POST(BodyPublishers.ofByteArray(bodyBytes));
            }
            System.out.println("Request body: " + new String (bodyBytes).toString());
            try {
                return Mono.just(new JDKHttpResponse(request, client.send(httpRequestBuilder.build(), HttpResponse.BodyHandlers.ofString())));
            } catch (Exception ex) {
                return Mono.error(ex);
            }
        });
    }

}
