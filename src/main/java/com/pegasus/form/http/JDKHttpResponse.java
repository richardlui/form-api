package com.pegasus.form.http;

import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.azure.core.http.HttpHeaders;
import com.azure.core.http.HttpRequest;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class JDKHttpResponse extends com.azure.core.http.HttpResponse {
    private final int statusCode;
    private final HttpResponse<String> jdkResponse;
    private final HttpHeaders headers;

    public JDKHttpResponse(HttpRequest request, HttpResponse<String> jdkResponse) {
        super(request);
        this.statusCode = jdkResponse.statusCode();
        this.jdkResponse = jdkResponse;
        this.headers = new HttpHeaders();
        
        Map<String, List<String>> headerMap = jdkResponse.headers().map();
        for (Entry<String, List<String>> entry : headerMap.entrySet()) {
            this.headers.put(entry.getKey(), entry.getValue().get(0));
        }
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getHeaderValue(String s) {
        return headers.getValue(s);
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public Flux<ByteBuffer> getBody() {
        return getBodyAsByteArray().map(ByteBuffer::wrap).flux();
    }

    public Mono<byte[]> getBodyAsByteArray() {
        return Mono.just(jdkResponse.body().getBytes());
    }

    public Mono<String> getBodyAsString() {
        return getBodyAsByteArray().map(String::new);
    }

    public Mono<String> getBodyAsString(Charset charset) {
        return getBodyAsByteArray().map(bytes -> new String(bytes, charset));
    }
}
