package com.payneteasy.srvlog.api.impl;

import com.google.gson.Gson;
import com.payneteasy.srvlog.api.ISrvlogService;
import com.payneteasy.srvlog.api.messages.SaveLogsRequest;
import com.payneteasy.srvlog.api.messages.SaveLogsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.Redirect.NEVER;
import static java.net.http.HttpClient.Version.HTTP_1_1;
import static java.net.http.HttpRequest.BodyPublishers.ofByteArray;
import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.Duration.ofSeconds;

public class SrvlogClientImpl implements ISrvlogService {

    private static final Logger LOG = LoggerFactory.getLogger(SrvlogClientImpl.class);

    private final Gson gson = new Gson();

    private final HttpClient httpClient;
    private final URI        uri;
    private final String     token;

    public SrvlogClientImpl(String aUrl, String aToken) {
        uri        = URI.create(aUrl);
        token      = aToken;
        httpClient = HttpClient.newBuilder()
                .version(HTTP_1_1)
                .connectTimeout(ofSeconds(20))
                .followRedirects(NEVER)
                .build();
    }

    @Override
    public SaveLogsResponse saveLogs(SaveLogsRequest aLogs) {
        String json  = gson.toJson(aLogs);
        byte[] bytes = json.getBytes(UTF_8);

        LOG.debug("Sending to {} : {} of {} bytes ...", uri, aLogs.getRequestId(), bytes.length);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(ofSeconds(30))
                .header("Content-Type", "application/json; charset=utf-8")
                .header("Token", token)
                .POST(ofByteArray(bytes))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, ofString());
        } catch (IOException e) {
            throw new IllegalStateException("Cannot send request to " + uri, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while sending to " + uri, e);
        }

        if (response.statusCode() != 200) {
            throw new IllegalStateException("Wrong response from server " + response.body());
        }

        SaveLogsResponse logsResponse = gson.fromJson(response.body(), SaveLogsResponse.class);
        LOG.debug("Response is {}", logsResponse);

        return logsResponse;
    }
}
