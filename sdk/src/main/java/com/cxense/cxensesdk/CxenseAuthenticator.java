package com.cxense.cxensesdk;

import com.cxense.exceptions.CxenseException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * Default Cxense authenticator. It use {@code X-cXense-Authentication} header.
 *
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
class CxenseAuthenticator implements Authenticator {
    public static final int DEFAULT_MAX_ATTEMPTS = 3;
    static final String AUTH_HEADER = "X-cXense-Authentication";

    private int maxAttempts;
    private String username;
    private String apiKey;

    public CxenseAuthenticator() {
        this("", "");
    }

    public CxenseAuthenticator(String username, String apiKey) {
        this(username, apiKey, DEFAULT_MAX_ATTEMPTS);
    }

    public CxenseAuthenticator(String username, String apiKey, int maxAttempts) {
        this.maxAttempts = maxAttempts;
        updateCredentials(username, apiKey);
    }

    void updateCredentials(String username, String apiKey) {
        this.username = username;
        this.apiKey = apiKey;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        if (responseCount(response) >= maxAttempts) {
            return null; // If we've failed N times, give up.
        }
        try {
            String token = AuthenticationToken.create(username, apiKey);
            return response.request().newBuilder().header(AUTH_HEADER, token).build();
        } catch (NoSuchAlgorithmException | InvalidKeyException | IllegalArgumentException e) {
            throw new CxenseException("Failed to create authenticationToken!", e);
        }
    }

    int responseCount(Response response) {
        int result = 1;
        while ((response = response.priorResponse()) != null) {
            result++;
        }
        return result;
    }
}
