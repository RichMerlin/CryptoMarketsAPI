/* 
 * Copyright 2015 Sergey Orlov <serg.merlin@gmail.com>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.rich_merlin.crypto.api;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author serg.merlin
 */
abstract class BtceApiPrivate<RESULT extends BtceApiTypes.Result> extends CustomJsonAPI<RESULT> {
    
    private static final AtomicInteger nonce = new AtomicInteger(
            (int)(System.currentTimeMillis() / 100));
    
    private static final String URL_PRIVATE_API = "https://btc-e.com/tapi";
    private static final String CRYPTO_SIGN_ALGO = "HmacSHA512";
    
    private final String apiKey;
    private final String apiSecret;
    
    public BtceApiPrivate(final String apiKey, final String apiSecret) {
        this.apiSecret = apiSecret;
        this.apiKey = apiKey;
    }
    
    @Override
    protected final String getApiURL() {
        return URL_PRIVATE_API;
    }
    
    @Override
    protected final void setupHeaders(byte[] postData) {
        try {
            Mac mac = Mac.getInstance(CRYPTO_SIGN_ALGO);
            SecretKeySpec secretSpec = new SecretKeySpec(apiSecret.getBytes(ENC_UTF8), CRYPTO_SIGN_ALGO);
            mac.init(secretSpec);
            String signature = DatatypeConverter.printHexBinary(mac.doFinal(postData));
            setHeader("Sign", signature.toLowerCase());
            setHeader("Key", apiKey);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException 
                | InvalidKeyException ex) {
            throw new RuntimeException("Internal error: Can't sign headers", ex);
        }
    }
    
    @Override
    protected Boolean beforeQuery() {
        setParam("method", getApiQueryName());
        setParam("nonce", nonce.getAndIncrement());
        return true;
    }
    
    @Override
    protected final Boolean isHttpPostQuery() {
        return true;
    }
    
    @Override
    protected final Boolean isGzipEncoding() {
        return true;
    }
    
    @Override
    protected final void afterQuery(String answer, RESULT result) {
        try (JsonReader reader = Json.createReader(new StringReader(answer))) {
            JsonObject obj = reader.readObject();
            result.success = obj.getInt("success") == 1;
            if (result.success) {
                // Children work with an answer
                parseAnswer(obj.get("return"), result);
            }
            else
                // Read error message
                result.error = obj.getString("error");
        } catch (JsonException | IllegalArgumentException ex) {
            throw new RuntimeException("Incorrect server response:\n" + answer, ex);
        }
    }
    
    protected abstract String getApiQueryName();
    
    protected abstract void parseAnswer(JsonValue answer, RESULT result);
}
