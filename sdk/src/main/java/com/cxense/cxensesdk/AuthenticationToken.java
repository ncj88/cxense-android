package com.cxense.cxensesdk;

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

/**
 * @author Dmitriy Konopelkin (dmitry.konopelkin@cxense.com) on (2017-06-05).
 */
class AuthenticationToken {

    private static final String ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    static final DateFormat DATE_FORMAT = new SimpleDateFormat(ISO_8601, Locale.US);
    private static final String ALGORITHM = "HmacSHA256";
    private static final String CHARSET_NAME = "UTF-8";

    static String create(String username,
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
}