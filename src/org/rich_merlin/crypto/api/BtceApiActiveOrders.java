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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author serg.merlin
 */
public class BtceApiActiveOrders extends BtceApiPrivate<BtceApiActiveOrders.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public List<Order> orders = new ArrayList<>();
        
        public static class Order {
            
            public Integer orderId;
            public BtceApiTypes.Markets market;
            public Date created;
            public BigDecimal price;
            public BigDecimal amount;
            public BtceApiTypes.OrderType orderType;
            
            @Override
            public String toString() {
                return "orderId=" + orderId + ", created=" + created
                        + ", market=" + market + ", status=" + ", amount=" + amount
                        + ", price=" + price + ", orderType=" + orderType;
            }
        }
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (Order order : orders) {
                res.append("(").append(order).append(")\n");
            }
            return super.toString() + "\n" + res.toString();
        }
    }
    
    private Markets market;
    
    public BtceApiActiveOrders(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "ActiveOrders";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected Boolean beforeQuery() {
        if (market != null)
            setParam("pair", market);
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject array = (JsonObject)answer;
        for (Map.Entry<String, JsonValue> value: array.entrySet()) {
            JsonObject obj = (JsonObject)value.getValue();
            Result.Order order = new Result.Order();
            switch (obj.getString("type")) {
                case "sell":
                    order.orderType = BtceApiTypes.OrderType.SELL;
                    break;
                case "buy":
                    order.orderType = BtceApiTypes.OrderType.BUY;
                    break;
                default:
                    throw new RuntimeException();
            }
            order.orderId = Integer.parseInt(value.getKey());
            order.price = obj.getJsonNumber("rate").bigDecimalValue();
            order.amount = obj.getJsonNumber("amount").bigDecimalValue();
            order.created = BtceApiTypes.getTimestampToDate(
                    obj.getJsonNumber("timestamp_created").longValue());
            order.market = BtceApiTypes.Markets.getEnum(obj.getString("pair"));
            result.orders.add(order);
        }
    }
    
    public BtceApiActiveOrders setMarketId(BtceApiTypes.Markets market) {
        this.market = market;
        return this;
    }
}
