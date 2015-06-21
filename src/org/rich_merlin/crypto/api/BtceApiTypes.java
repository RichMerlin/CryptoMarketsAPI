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

/**
 *
 * @author serg.merlin
 */
public class BtceApiTypes {
    
    public enum OrderType {
        BUY("buy"), SELL("sell");
        
        private final String value;

        OrderType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return getValue();
        }
        
        public static OrderType getEnum(String value) {
            for (OrderType v : values()) {
                if(v.getValue().equals(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    };
    
    public enum SortingType {
        ASC, DESC;
    }
    
    public enum Rights {
        INFO("info"),
        TRADE("trade"),
        WITHDRAW("withdraw"),
        COUPON("coupon");
        
        private final String value;
        
        Rights(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return getValue();
        }
        
        public static Rights getEnum(String value) {
            for (Rights v : values()) {
                if(v.getValue().equals(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    public enum Coins {
        BTC("btc"),
        LTC("ltc"),
        USD("usd"),
        RUR("rur"),
        EUR("eur"),
        NMC("nmc"),
        NVC("nvc"),
        PPC("ppc"),
        TRC("trc"),
        FTC("ftc"),
        XPM("xpm"),
        CNH("cnh"),
        GBP("gbp");
        
        private final String value;

        Coins(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
        
        @Override
        public String toString() {
            return getValue();
        }
        
        public static Coins getEnum(String value) {
            for (Coins v : values()) {
                if(v.getValue().equals(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    public enum Markets {
        BTC_USD("btc_usd", Coins.BTC, Coins.USD),
        BTC_RUR("btc_rur", Coins.BTC, Coins.RUR),
        BTC_EUR("btc_eur", Coins.BTC, Coins.EUR),
        LTC_BTC("ltc_btc", Coins.LTC, Coins.BTC),
        LTC_USD("ltc_usd", Coins.LTC, Coins.USD),
        LTC_RUR("ltc_rur", Coins.LTC, Coins.RUR),
        LTC_EUR("ltc_eur", Coins.LTC, Coins.EUR),
        NMC_BTC("nmc_btc", Coins.NMC, Coins.BTC),
        NMC_USD("nmc_usd", Coins.NMC, Coins.USD),
        NVC_BTC("nvc_btc", Coins.NVC, Coins.BTC),
        NVC_USD("nvc_usd", Coins.NVC, Coins.USD),
        USD_RUR("usd_rur", Coins.USD, Coins.RUR),
        EUR_USD("eur_usd", Coins.EUR, Coins.USD),
        EUR_RUR("eur_rur", Coins.EUR, Coins.RUR),
        PPC_BTC("ppc_btc", Coins.PPC, Coins.BTC),
        PPC_USD("ppc_usd", Coins.PPC, Coins.USD);
        
        private final String value;
        private final Coins primary, secondary;

        Markets(String value, Coins primary, Coins secondary) {
            this.value = value;
            this.primary = primary;
            this.secondary = secondary;
        }
        
        public String getValue() {
            return value;
        }
        
        public Coins getPrimaryCoin() {
            return primary;
        }
        
        public Coins getSecondaryCoin() {
            return secondary;
        }
        
        @Override
        public String toString() {
            return getValue();
        }
        
        public static Markets getEnum(String value) {
            for (Markets v : values()) {
                if(v.getValue().equals(value)) {
                    return v;
                }
            }
            throw new IllegalArgumentException();
        }
    }
    
    public static class Result {
        
        public Boolean success;
        public String error;
        
        {
            success = false;
        }
        
        @Override
        public String toString() {
            StringBuilder res = new StringBuilder();
            res.append("success=").append(success)
                    .append(", error=").append(error);
            return res.toString();
        }
    }
    
    public static Date getTimestampToDate(long value) {
        return new Date(value * 1000);
    }
}
