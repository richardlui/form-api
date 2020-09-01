package com.pegasus.form.http;

import com.azure.core.http.HttpClient;
import com.azure.core.http.HttpClientProvider;

public class JDKHttpClientProvider implements HttpClientProvider {

    @Override
    public HttpClient createInstance() {
        return new JDKHttpClient();
    }

}
