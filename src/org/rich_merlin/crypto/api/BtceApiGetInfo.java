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

import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;
import java.util.Date;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonNumber;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author serg.merlin
 */
public class BtceApiGetInfo extends BtceApiPrivate<BtceApiGetInfo.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
	public Integer openOrders;
	public Date serverTime;
        public Map<BtceApiTypes.Coins, BigDecimal> funds = new HashMap<>();
        public Map<BtceApiTypes.Rights, Boolean> rights = new HashMap<>();
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("openOrders=").append(openOrders)
                    .append(", serverTime=").append(serverTime)
                    .append("\nfunds=");
            for (Coins fund : funds.keySet()) {
                res.append("(").append(fund).append("=").append(funds.get(fund)).append(")");
            }
            res.append("\nrights=");
            for (Rights right: rights.keySet()) {
                res.append("(").append(right).append("=").append(rights.get(right)).append(")");
            }
            return super.toString() + "\n" + res.toString();
        }
    }
    
    public BtceApiGetInfo(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "getInfo";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject obj = (JsonObject)answer;
        result.openOrders = obj.getInt("open_orders");
        result.serverTime = BtceApiTypes.getTimestampToDate(
                obj.getJsonNumber("server_time").longValue());
        JsonObject funds = obj.getJsonObject("funds");
        for (Map.Entry<String, JsonValue> value: funds.entrySet()) {
            BtceApiTypes.Coins coin = BtceApiTypes.Coins.getEnum(value.getKey());
            result.funds.put(coin, (((JsonNumber)value.getValue()).bigDecimalValue()));
        }
        JsonObject rights = obj.getJsonObject("rights");
        for (Map.Entry<String, JsonValue> value: rights.entrySet()) {
            BtceApiTypes.Rights right = BtceApiTypes.Rights.getEnum(value.getKey());
            result.rights.put(right, (((JsonNumber)value.getValue()).intValue()) == 1);
        }
    }
}
