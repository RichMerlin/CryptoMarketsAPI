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
import java.util.List;
import java.math.BigDecimal;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;

/**
 *
 * @author serg.merlin
 */
public class BtceApiDepth extends BtceApiPublic<BtceApiDepth.Result> {
    
    public static class Result extends BtceApiTypes.Result{
        
        public List<Depth> buy = new ArrayList<>();
        public List<Depth> sell = new ArrayList<>();
        
        public static class Depth {
            public BigDecimal price;
            public BigDecimal amount;
            
            @Override
            public String toString() {
                return "price=" + price + ", amount=" + amount;
            }
        }
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("\nbuy=");
            for (Depth depth : buy) {
                res.append("(").append(depth).append(")");
            }
            res.append("\nsell=");
            for (Depth depth : sell) {
                res.append("(").append(depth).append(")");
            }
            return super.toString() + res.toString();
        }
    }
    
    private Integer limit = null;
    
    @Override
    protected String getApiQueryName() {
        return "depth";
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
        JsonObject mainObj = (JsonObject)answer;
        JsonObject obj = (JsonObject)mainObj.getJsonObject(getMarket().toString());
        JsonArray array = (JsonArray)obj.getJsonArray("asks");
        for (JsonValue value : array) {
            JsonArray arrayValue = (JsonArray)value;
            Result.Depth depth = new Result.Depth();
            depth.price = arrayValue.getJsonNumber(0).bigDecimalValue();
            depth.amount = arrayValue.getJsonNumber(1).bigDecimalValue();
            result.sell.add(depth);
        }
        array = (JsonArray)obj.getJsonArray("bids");
        for (JsonValue value : array) {
            JsonArray arrayValue = (JsonArray)value;
            Result.Depth depth = new Result.Depth();
            depth.price = arrayValue.getJsonNumber(0).bigDecimalValue();
            depth.amount = arrayValue.getJsonNumber(1).bigDecimalValue();
            result.buy.add(depth);
        }
    }
    
    public BtceApiDepth setLimit(Integer limit) {
        this.limit = limit;
        return this;
    }
}
