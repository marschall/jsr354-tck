/*
 * Copyright (c) 2012, 2013, Werner Keil, Credit Suisse (Anatole Tresch). Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License. Contributors: Anatole Tresch - initial version.
 */
package org.javamoney.tck.tests.internal;

import org.javamoney.tck.tests.conversion.TestExchangeRate;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.Monetary;
import javax.money.convert.ConversionContext;
import javax.money.convert.ConversionContextBuilder;
import javax.money.convert.ConversionQuery;
import javax.money.convert.ConversionQueryBuilder;
import javax.money.convert.CurrencyConversion;
import javax.money.convert.ExchangeRate;
import javax.money.convert.ExchangeRateProvider;
import javax.money.convert.ProviderContext;
import javax.money.convert.ProviderContextBuilder;
import javax.money.convert.RateType;
import java.util.Objects;

/**
 * Test ExchangeProvider. Created by Anatole on 26.04.2014.
 */
public class TestRateProvider02 implements ExchangeRateProvider {

    public static final double FACTOR = 0.2;
    private static final ProviderContext PC = ProviderContextBuilder.of("TestRateProvider02", RateType.OTHER).build();
    private static final ConversionContext CC = ConversionContextBuilder.create(PC, RateType.OTHER).build();

    private static final class Conversion implements CurrencyConversion {

        private CurrencyUnit term;

        private Conversion(CurrencyUnit term) {
            Objects.requireNonNull(term);
            this.term = term;
        }

        @Override
        public CurrencyUnit getCurrency() {
            return term;
        }

        @Override
        public ConversionContext getContext() {
            return CC;
        }

        @Override
        public ExchangeRate getExchangeRate(MonetaryAmount sourceAmount) {
            return new TestExchangeRate.Builder(CC).setFactor(new TestNumberValue(FACTOR))
                    .setBase(sourceAmount.getCurrency()).setTerm(term).build();
        }

        @Override
        public MonetaryAmount apply(MonetaryAmount value) {
            return value.multiply(FACTOR).getFactory().setCurrency(term).create();
        }

        @Override
        public ExchangeRateProvider getExchangeRateProvider() {
            return null;
        }
    }

    @Override
    public ProviderContext getContext() {
        return PC;
    }

    @Override
    public boolean isAvailable(ConversionQuery conversionQuery) {
        Objects.requireNonNull(conversionQuery);
        Objects.requireNonNull(conversionQuery.getCurrency());
        return true;
    }

    @Override
    public ExchangeRate getExchangeRate(ConversionQuery conversionQuery) {
        Objects.requireNonNull(conversionQuery.getBaseCurrency());
        if (isAvailable(conversionQuery)) {
            return new TestExchangeRate.Builder(getContext().getProviderName(), RateType.OTHER)
                    .setFactor(new TestNumberValue(FACTOR)).setBase(conversionQuery.getBaseCurrency())
                    .setTerm(conversionQuery.getCurrency()).build();
        }
        return null;
    }


    @Override
    public CurrencyConversion getCurrencyConversion(CurrencyUnit term) {
        return new Conversion(term);
    }

    @Override
    public boolean isAvailable(CurrencyUnit base, CurrencyUnit term) {
        return isAvailable(ConversionQueryBuilder.of().setBaseCurrency(base).setTermCurrency(term).build());
    }

    @Override
    public boolean isAvailable(String baseCode, String termCode) {
        return isAvailable(ConversionQueryBuilder.of().setBaseCurrency(Monetary.getCurrency(baseCode))
                .setTermCurrency(Monetary.getCurrency(termCode)).build());
    }

    @Override
    public ExchangeRate getReversed(ExchangeRate rate) {
        ConversionQuery reverseQuery = rate.getContext().toQueryBuilder().setBaseCurrency(rate.getCurrency())
                .setTermCurrency(rate.getBaseCurrency()).build();
        if(isAvailable(reverseQuery)){
            return getExchangeRate(reverseQuery);
        }
        return null;
    }

    @Override
    public CurrencyConversion getCurrencyConversion(String termCode) {
        return new Conversion(Monetary.getCurrency(termCode));
    }

    @Override
    public CurrencyConversion getCurrencyConversion(ConversionQuery conversionQuery) {
        Objects.requireNonNull(conversionQuery);
        Objects.requireNonNull(conversionQuery.getCurrency());
        return new Conversion(conversionQuery.getCurrency());
    }

    @Override
    public ExchangeRate getExchangeRate(CurrencyUnit base, CurrencyUnit term) {
        return getExchangeRate(ConversionQueryBuilder.of().setBaseCurrency(base).setTermCurrency(term).build());
    }

    @Override
    public ExchangeRate getExchangeRate(String baseCode, String termCode) {
        return getExchangeRate(ConversionQueryBuilder.of().setBaseCurrency(Monetary.getCurrency(baseCode))
                .setTermCurrency(Monetary.getCurrency(termCode)).build());
    }


}
