package com.bybit.api.client.security;

import com.bybit.api.client.exception.BybitApiException;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Utility class to sign messages using HMAC-SHA256.
 */
public class HmacSHA256Signer {

    /**
     * Sign the given message using the given secret.
     * @param apiKey api key
     * @param apiSecret api secret
     * @param payload query parameters
     * @param timestamp current time in milliseconds
     * @param recvWindow server receives window
     * @return a signed message
     */
    public static String sign(String apiKey, String apiSecret, String payload, long timestamp, long recvWindow) {
        if (apiKey == null || apiSecret == null) {
            throw new BybitApiException("Authenticated endpoints require keys.");
        }

        String message = Long.toString(timestamp) + apiKey + Long.toString(recvWindow) + payload;
        Mac sha256_HMAC = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secretKeySpec);
            return new String(Hex.encodeHex(sha256_HMAC.doFinal(message.getBytes())));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * To convert bytes to hex
     * @param hash
     * @return hex string
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}