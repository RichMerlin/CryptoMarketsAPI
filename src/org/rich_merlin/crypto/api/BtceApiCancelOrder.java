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
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author serg.merlin
 */
public class BtceApiCancelOrder extends BtceApiPrivate<BtceApiCancelOrder.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public Integer orderId;
        public Map<BtceApiTypes.Coins, BigDecimal> funds = new HashMap<>();
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("\norderId=").append(orderId)
                    .append("\nfunds=");
            for (Map.Entry<BtceApiTypes.Coins, BigDecimal> fund : funds.entrySet()) {
                res.append("(").append(fund.getKey()).append(", ")
                        .append(fund.getValue()).append(")");
            }
            return super.toString() + res.toString();
        }
    }
    
    private int orderId;
    
    public BtceApiCancelOrder(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "CancelOrder";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected Boolean beforeQuery() {
        setParam("order_id", orderId);
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject object = (JsonObject)answer;
        result.orderId = object.getInt("order_id");
        JsonObject funds = object.getJsonObject("funds");
        for (Map.Entry<String, JsonValue> value: funds.entrySet()) {
            JsonNumber fund = (JsonNumber)value.getValue();
            result.funds.put(BtceApiTypes.Coins.getEnum(value.getKey()), fund.bigDecimalValue());
        }
    }
    
    public BtceApiCancelOrder setOrderId(int orderId) {
        this.orderId = orderId;
        return this;
    }
}
