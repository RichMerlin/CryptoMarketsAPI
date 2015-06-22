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

import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.json.JsonNumber;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author serg.merlin
 */
public class BtceApiTrade extends BtceApiPrivate<BtceApiTrade.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public Integer orderId;
        public BigDecimal received;
        public BigDecimal remains;
        public Map<BtceApiTypes.Coins, BigDecimal> funds = new HashMap<>();
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("\norderId=").append(orderId)
                    .append(", received=").append(received)
                    .append(", remains=").append(remains)
                    .append("\nfunds=");
            for (Map.Entry<BtceApiTypes.Coins, BigDecimal> fund : funds.entrySet()) {
                res.append("(").append(fund.getKey()).append(", ")
                        .append(fund.getValue()).append(")");
            }
            return super.toString() + res.toString();
        }
    }
    
    private BtceApiTypes.Markets market;
    private BtceApiTypes.OrderType orderType; //Order type you are creating (Buy/Sell)
    private BigDecimal price; //Price per unit you are buying/selling at
    private BigDecimal amount; //Amount of units you are buying/selling in this order
    
    public BtceApiTrade(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "Trade";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected Boolean beforeQuery() {
        setParam("pair", market);
        setParam("amount", amount);
        setParam("rate", price);
        setParam("type", orderType);
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject object = (JsonObject)answer;
        result.orderId = object.getInt("order_id");
        result.received = object.getJsonNumber("received").bigDecimalValue();
        result.remains = object.getJsonNumber("remains").bigDecimalValue();
        JsonObject funds = object.getJsonObject("funds");
        for (Map.Entry<String, JsonValue> value: funds.entrySet()) {
            JsonNumber fund = (JsonNumber)value.getValue();
            result.funds.put(Coins.getEnum(value.getKey()), fund.bigDecimalValue());
        }
    }
    
    public BtceApiTrade setMarketId(BtceApiTypes.Markets market) {
        this.market = market;
        return this;
    }
    
    public BtceApiTrade setOrderType(BtceApiTypes.OrderType orderType) {
        this.orderType = orderType;
        return this;
    }
    
    public BtceApiTrade setQuantity(BigDecimal amount) {
        this.amount = amount;
        return this;
    }
    
    public BtceApiTrade setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }
}
