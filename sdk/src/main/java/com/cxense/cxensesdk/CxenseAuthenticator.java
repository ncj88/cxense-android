package com.cxense.cxensesdk;

import android.support.annotation.NonNull;

import com.cxense.exceptions.CxenseException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    static final DateFormat DATE_FORMAT = new SimpleDateFormat(ISO_8601, Locale.US);
    private static final String ALGORITHM = "HmacSHA256";
    private static final String CHARSET_NAME = "UTF-8";

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

    String createToken(String username,
                       String secret)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(new SecretKeySpec(secret.getBytes(CHARSET_NAME), ALGORITHM));
        String date = DATE_FORMAT.format(new Date());
        mac.update(date.getBytes(CHARSET_NAME));
        byte[] signature = mac.doFinal();
        Formatter hex = new Formatter();
        for (byte b : signature) {
            hex.format("%02X", b);
        }
        return String.format(Locale.US, "username=%s date=%s hmac-sha256-hex=%s", username, date, hex);
    }

    @Override
    public Request authenticate(@NonNull Route route, @NonNull Response response) throws IOException {
        if (responseCount(response) >= maxAttempts) {
            return null; // If we've failed N times, give up.
        }
        try {
            String token = createToken(username, apiKey);
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
