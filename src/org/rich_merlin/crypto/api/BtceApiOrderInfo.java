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

import java.math.BigDecimal;
import java.util.Date;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author Sergey Orlov <serg.merlin@gmail.com>
 */
public class BtceApiOrderInfo extends BtceApiPrivate<BtceApiOrderInfo.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
	public Markets pair;
        public OrderType orderType;
        public BigDecimal startAmount;
        public BigDecimal remainsAmount;
        public BigDecimal price;
        public Date created;
        public OrderState state;
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("pair=").append(pair)
                    .append(", orderType=").append(orderType)
                    .append(", created=").append(created)
                    .append(", startAmount=").append(startAmount)
                    .append(", remainsAmount=").append(remainsAmount)
                    .append(", price=").append(price)
                    .append(", state=").append(state);
            return super.toString() + "\n" + res.toString();
        }
    }
    
    private Integer orderId;
    
    public BtceApiOrderInfo(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "OrderInfo";
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
        JsonObject obj = (JsonObject)answer;
        JsonObject order = (JsonObject)obj.getJsonObject(String.valueOf(orderId));
        result.pair = Markets.getEnum(order.getString("pair"));
        result.orderType = OrderType.getEnum(order.getString("type"));
        result.state = OrderState.getEnum(order.getInt("status"));
        result.created = BtceApiTypes.getTimestampToDate(order.getJsonNumber("timestamp_created").longValue());
        result.startAmount = order.getJsonNumber("start_amount").bigDecimalValue();
        result.remainsAmount = order.getJsonNumber("amount").bigDecimalValue();
        result.price = order.getJsonNumber("rate").bigDecimalValue();
    }
    
    public BtceApiOrderInfo setOrderId(int orderId) {
        this.orderId = orderId;
        return this;
    }
}
