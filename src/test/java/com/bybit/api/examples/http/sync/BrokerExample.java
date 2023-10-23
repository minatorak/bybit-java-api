package com.bybit.api.examples.http.sync;

import com.bybit.api.client.domain.institution.BusinessType;
import com.bybit.api.client.domain.institution.InstitutionDataRequest;
import com.bybit.api.client.service.BybitApiClientFactory;

public class BrokerExample {
    public static void main(String[] args) {
        // Borker API key
        BybitApiClientFactory factory = BybitApiClientFactory.newInstance("YOUR_API_KEY", "YOUR_API_SECRET");
        var client = factory.newInstitutionRestClient();

        // Get Broker Earning
        var brokerEarningRequest = InstitutionDataRequest.builder().bizType(BusinessType.SPOT).build();
        var brokerEarningData = client.getBrokerEarningData(brokerEarningRequest);
        System.out.println(brokerEarningData);
    }
}
