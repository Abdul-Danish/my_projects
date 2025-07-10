package com.dsa;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;
import org.apache.hc.core5.http.io.entity.EntityUtils;

public class CustomHttpClientResponseHandler implements HttpClientResponseHandler<HttpResponseData> {

    @Override
    public HttpResponseData handleResponse(ClassicHttpResponse response) throws HttpException, IOException {
        return new HttpResponseData(response.getCode(),
            Objects.nonNull(response.getEntity()) ? EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8) : "");
    }

}
