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

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.math.BigDecimal;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author serg.merlin
 */
public class BtceApiTrades extends BtceApiPublic<BtceApiTrades.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public List<Trade> trades = new ArrayList<>();
        
        public static class Trade {
            public Integer tradeId;
            public Date time; 
            public BigDecimal price;
            public BigDecimal amount;
            public OrderType tradeType;
            
            @Override
            public String toString() {
                return "tradeId=" + tradeId + ", time=" + time
                        + ", price=" + price + ", amount=" + amount
                        + ", tradeType=" + tradeType;
            }
        }
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (Trade trade : trades) {
                res.append("(").append(trade).append(")\n");
            }
            return super.toString() + "\n" + res.toString();
        }
    }
    
    private Integer limit = null;
    
    @Override
    protected String getApiQueryName() {
        return "trades";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected Boolean beforeQuery() {
        if (limit != null) {
            setParam("limit", limit);
        }
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject obj = (JsonObject)answer;
        JsonArray array = (JsonArray)obj.getJsonArray(getMarket().toString());
        for (JsonValue value : array) {
            JsonObject tradeValue = (JsonObject)value;
            Result.Trade trade = new Result.Trade();
            switch (tradeValue.getString("type")) {
                case "ask":
                    trade.tradeType = OrderType.SELL;
                    break;
                case "bid":
                    trade.tradeType = OrderType.BUY;
                    break;
                default:
                    throw new RuntimeException();
            }
            trade.tradeId = tradeValue.getInt("tid");
            trade.price = tradeValue.getJsonNumber("price").bigDecimalValue();
            trade.amount = tradeValue.getJsonNumber("amount").bigDecimalValue();
            trade.time = BtceApiTypes.getTimestampToDate(tradeValue.getJsonNumber("timestamp").longValue());
            result.trades.add(trade);
        }
    }
    
    public BtceApiTrades setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }
}
