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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.*;

/**
 *
 * @author Sergey Orlov <serg.merlin@gmail.com>
 */
public class BtceApiTransHistory extends BtceApiPrivate<BtceApiTransHistory.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public List<Transaction> transactions = new ArrayList<>();
        
        public static class Transaction {
            
            public Integer id;
            public Date time;
            public Coins currency;
            public TransactionType type;
            public BigDecimal amount;
            public TransactionState state;
            public String description;
            
            @Override
            public String toString() {
                return "id=" + id + ", time=" + time
                        + ", currency=" + currency + ", type=" + type
                        + ", amount=" + amount + ", state=" + state
                        + ", description=" + description;
            }
        }
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            for (Transaction trans : transactions) {
                res.append("(").append(trans).append(")\n");
            }
            return super.toString() + "\n" + res.toString();
        }
    }
    
    private Integer fromNumber = null; //номер сделки, с которой начинать вывод
    private Integer count = null; //количество сделок на вывод
    private Integer fromId = null; //id сделки, с которой начинать вывод
    private Integer endId = null; //id сделки, на которой заканчивать вывод
    private BtceApiTypes.SortingType sorting = null; //сортировка
    private Date sinceTime = null; //с какого времени начинать вывод
    private Date endTime = null; //на каком времени заканчивать вывод
    
    public BtceApiTransHistory(final String apiKey, final String apiSecret) {
        super(apiKey, apiSecret);
    }
    
    @Override
    protected String getApiQueryName() {
        return "TransHistory";
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
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject array = (JsonObject)answer;
        for (Map.Entry<String, JsonValue> value: array.entrySet()) {
            JsonObject obj = (JsonObject)value.getValue();
            Result.Transaction trans = new Result.Transaction();
            trans.id = Integer.parseInt(value.getKey());
            trans.currency = Coins.getEnum(obj.getString("currency"));
            trans.type = TransactionType.getEnum(obj.getInt("type"));
            trans.state = TransactionState.getEnum(obj.getInt("status"));
            trans.time = BtceApiTypes.getTimestampToDate(
                    obj.getJsonNumber("timestamp").longValue());
            trans.amount = obj.getJsonNumber("amount").bigDecimalValue();
            trans.description = obj.getString("desc");
            result.transactions.add(trans);
        }
    }
    
    public BtceApiTransHistory setFromNumber(Integer fromNumber) {
        this.fromNumber = fromNumber;
        return this;
    }
    
    public BtceApiTransHistory setCount(Integer count) {
        this.count = count;
        return this;
    }
    
    public BtceApiTransHistory setSorting(BtceApiTypes.SortingType sorting) {
        this.sorting = sorting;
        return this;
    }
    
    public BtceApiTransHistory setFromId(Integer fromId) {
        this.fromId = fromId;
        return this;
    }
    
    public BtceApiTransHistory setEndId(Integer endId) {
        this.endId = endId;
        return this;
    }
    
    public BtceApiTransHistory setSinceTime(Date sinceTime) {
        this.sinceTime = new Date(sinceTime.getTime());
        return this;
    }
    
    public BtceApiTransHistory setEndTime(Date endTime) {
        this.endTime = new Date(endTime.getTime());
        return this;
    }
}
