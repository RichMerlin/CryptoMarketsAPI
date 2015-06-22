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

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.math.BigDecimal;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author serg.merlin
 */
public class BtceApiTradeHistory extends BtceApiPrivate<BtceApiTradeHistory.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public List<Trade> trades = new ArrayList<>();
        
        public static class Trade {
            
            public Integer id; //Trade ID
            public Markets market;
            public Date time;
            public Integer orderId;
            public Boolean isYourOrder;
            public BigDecimal price;
            public BigDecimal amount;
            public OrderType type;
            
            @Override
            public String toString() {
                return "id=" + id + ", time=" + time
                        + ", market=" + market + ", orderId=" + orderId
                        + ", isYourOrder=" + isYourOrder + ", amount=" + amount
                        + ", price=" + price + ", type=" + type;
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
    
    private Integer fromNumber = null; //номер сделки, с которой начинать вывод
    private Integer count = null; //количество сделок на вывод
    private Integer fromId = null; //id сделки, с которой начинать вывод
    private Integer endId = null; //id сделки, на которой заканчивать вывод
    private SortingType sorting = null; //сортировка
    private Date sinceTime = null; //с какого времени начинать вывод
    private Date endTime = null; //на каком времени заканчивать вывод
    private Markets market = null;
    
    public BtceApiTradeHistory(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "TradeHistory";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected Boolean beforeQuery() {
        if (fromNumber != null)
            setParam("from", fromNumber);
        if (count != null)
            setParam("count", count);
        if (fromId != null)
            setParam("from_id", fromId);
        if (endId != null)
            setParam("end_id", endId);
        if (sorting != null)
            setParam("order", sorting);
        if (sinceTime != null)
            setParam("since", sinceTime.getTime() / 1000);
        if (endTime != null)
            setParam("end", endTime.getTime() / 1000);
        if (market != null)
            setParam("pair", market);
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject array = (JsonObject)answer;
        for (Map.Entry<String, JsonValue> value: array.entrySet()) {
            JsonObject obj = (JsonObject)value.getValue();
            Result.Trade trade = new Result.Trade();
            trade.id = Integer.parseInt(value.getKey());
            trade.type = OrderType.getEnum(obj.getString("type"));
            trade.price = obj.getJsonNumber("rate").bigDecimalValue();
            trade.amount = obj.getJsonNumber("amount").bigDecimalValue();
            trade.time = BtceApiTypes.getTimestampToDate(
                    obj.getJsonNumber("timestamp").longValue());
            trade.isYourOrder = obj.getInt("is_your_order") == 1;
            trade.orderId = obj.getInt("order_id");
            trade.market = Markets.getEnum(obj.getString("pair"));
            result.trades.add(trade);
        }
    }
    
    public BtceApiTradeHistory setMarketId(Markets market) {
        this.market = market;
        return this;
    }
    
    public BtceApiTradeHistory setFromNumber(Integer fromNumber) {
        this.fromNumber = fromNumber;
        return this;
    }
    
    public BtceApiTradeHistory setCount(Integer count) {
        this.count = count;
        return this;
    }
    
    public BtceApiTradeHistory setSorting(SortingType sorting) {
        this.sorting = sorting;
        return this;
    }
    
    public BtceApiTradeHistory setFromId(Integer fromId) {
        this.fromId = fromId;
        return this;
    }
    
    public BtceApiTradeHistory setEndId(Integer endId) {
        this.endId = endId;
        return this;
    }
    
    public BtceApiTradeHistory setSinceTime(Date sinceTime) {
        this.sinceTime = new Date(sinceTime.getTime());
        return this;
    }
    
    public BtceApiTradeHistory setEndTime(Date endTime) {
        this.endTime = new Date(endTime.getTime());
        return this;
    }
}
