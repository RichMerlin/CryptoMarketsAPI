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
import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author serg.merlin
 */
abstract class BtceApiPublic<RESULT extends BtceApiTypes.Result> extends CustomJsonAPI<RESULT> {
    
    private static final String URL_PUBLIC_API = "https://btc-e.com/api/3/%s/%s";
    
    private Markets market = null;
    private boolean ignoreInvalid = false;
    
    @Override
    protected final String getApiURL() {
        return String.format(URL_PUBLIC_API, getApiQueryName(),
                market == null ? "" : market);
    }
    
    @Override
    protected Boolean beforeQuery() {
        if (ignoreInvalid) {
            setParam("ignore_invalid", 1);
        }
        return true;
    }
    
    @Override
    protected final Boolean isHttpPostQuery() {
        return false;
    }
    
    @Override
    protected final Boolean isGzipEncoding() {
        return true;
    }
    
    @Override
    protected final void afterQuery(String answer, RESULT result) {
        try (JsonReader reader = Json.createReader(new StringReader(answer))) {
            JsonValue obj = reader.read();
            if (obj.getValueType() == JsonValue.ValueType.OBJECT) {
                result.success = !((JsonObject)obj).containsKey("error");
                if (!result.success) {
                    // Read error message
                    result.error = ((JsonObject)obj).getString("error");
                }
            } else
                result.success = true;
            if (result.success) {
                // Children work with an answer
                parseAnswer(obj, result);
            }
        } catch (NullPointerException | ClassCastException
                | JsonException | IllegalArgumentException except) {
            throw new RuntimeException("Incorrect server response:\n" + answer, except);
        }
    }
    
    protected abstract String getApiQueryName();
    
    protected abstract void parseAnswer(JsonValue answer, RESULT result);
    
    public void setMarket(Markets market) {
        this.market = market;
    }
    
    public Markets getMarket() {
        return market;
    }
    
    public void setIgnoreInvalid(boolean ignore) {
        this.ignoreInvalid = ignore;
    }
    
    public boolean getIgnoreInvalid() {
        return ignoreInvalid;
    }
}
