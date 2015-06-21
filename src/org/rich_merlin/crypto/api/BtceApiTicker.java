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

/**
 *
 * @author Sergey Orlov <serg.merlin@gmail.com>
 */
public class BtceApiTicker extends BtceApiPublic<BtceApiTicker.Result> {
    
    public static class Result extends BtceApiTypes.Result {
        
        public BigDecimal high; //макcимальная цена
        public BigDecimal low; //минимальная цена
        public BigDecimal average; //средняя цена
        public BigDecimal volume; //объем торгов
        public BigDecimal volumeCurrency; //объем торгов в валюте
        public BigDecimal lastTrade; //цена последней сделки
        public BigDecimal buy; //цена покупки
        public BigDecimal sell; //цена продажи
        public Date updated; //последнее обновление кэша
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("updated=").append(updated)
                    .append(", high=").append(high).append(", low=").append(low)
                    .append(", average=").append(average).append(", volume=").append(volume)
                    .append(", volumeCurrency=").append(volumeCurrency)
                    .append(", lastTrade=").append(lastTrade)
                    .append(", buy=").append(buy).append(", sell=").append(sell);
            return super.toString() + "\n" + res.toString();
        }
    }
    
    @Override
    protected String getApiQueryName() {
        return "ticker";
    }
    
    @Override
    protected Result createResult() {
        return new Result();
    }
    
    @Override
    protected Boolean beforeQuery() {
        return super.beforeQuery();
    }
    
    @Override
    protected void parseAnswer(JsonValue answer, Result result) {
        JsonObject obj = (JsonObject)answer;
        JsonObject ticker = (JsonObject)obj.getJsonObject(getMarket().toString());
        result.high = ticker.getJsonNumber("high").bigDecimalValue();
        result.low = ticker.getJsonNumber("low").bigDecimalValue();
        result.average = ticker.getJsonNumber("avg").bigDecimalValue();
        result.volume = ticker.getJsonNumber("vol").bigDecimalValue();
        result.volumeCurrency = ticker.getJsonNumber("vol_cur").bigDecimalValue();
        result.lastTrade = ticker.getJsonNumber("last").bigDecimalValue();
        result.buy = ticker.getJsonNumber("buy").bigDecimalValue();
        result.sell = ticker.getJsonNumber("sell").bigDecimalValue();
        result.updated = BtceApiTypes.getTimestampToDate(
                ticker.getJsonNumber("updated").longValue());
    }
}
