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
import java.util.Map;
import java.util.Date;
import java.util.HashMap;
import javax.json.JsonObject;
import javax.json.JsonValue;

import org.rich_merlin.crypto.api.BtceApiTypes.Markets;

/**
 *
 * @author Sergey Orlov <serg.merlin@gmail.com>
 */
public class BtceApiInfo extends BtceApiPublic<BtceApiInfo.Result> {
    
    public static class Result extends BtceApiTypes.Result{
        
        public Date serverTime;
        public Map<Markets, Pair> pairs = new HashMap<>();
        
        public static class Pair {
            
            public Integer decimalPlaces; //количество знаков после запятой разрешенные при торгах
            public BigDecimal minPrice; //минимальная цена разрешенная при торгах
            public BigDecimal maxPrice; //максимальная цена разрешенная при торгах
            public BigDecimal minAmount; //минимальное количество разрешенное для покупки/продажи
            public BigDecimal fee; //комиссия пары
            public Boolean hidden; //скрыта ли пара
            
            @Override
            public String toString() {
                return "hidden=" + hidden + ", decimalPlaces=" + decimalPlaces
                        + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice
                        + ", minAmount=" + minAmount + ", fee=" + fee;
            }
        }
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("\nserverTime=").append(serverTime);
            res.append("\npairs=");
            for (Map.Entry<Markets, Pair> pair : pairs.entrySet()) {
                res.append("(").append(pair.getKey()).append(":").append(pair.getValue()).append(")");
            }
            return super.toString() + res.toString();
        }
    }
    
    @Override
    protected String getApiQueryName() {
        return "info";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject obj = (JsonObject)answer;
        JsonObject pairs = (JsonObject)obj.getJsonObject("pairs");
        for (Map.Entry<String, JsonValue> value : pairs.entrySet()) {
            Markets market = Markets.getEnum(value.getKey()); 
            JsonObject pairObj = (JsonObject)value.getValue();
            Result.Pair pair = new Result.Pair();
            pair.hidden = pairObj.getInt("hidden") == 1;
            pair.decimalPlaces = pairObj.getInt("decimal_places");
            pair.minPrice = pairObj.getJsonNumber("min_price").bigDecimalValue();
            pair.maxPrice = pairObj.getJsonNumber("max_price").bigDecimalValue();
            pair.minAmount = pairObj.getJsonNumber("min_amount").bigDecimalValue();
            pair.fee = pairObj.getJsonNumber("fee").bigDecimalValue();
            result.pairs.put(market, pair);
        }
        result.serverTime = BtceApiTypes.getTimestampToDate(
                obj.getJsonNumber("server_time").longValue());
    }
}
