package com.bybit.api.examples.httpSync;

import com.bybit.api.client.BybitAnnouncementRestClient;
import com.bybit.api.client.domain.announcement.LanguageSymbol;
import com.bybit.api.client.domain.announcement.request.AnnouncementInfoRequest;
import com.bybit.api.client.impl.BybitApiClientFactory;

public class AnnounceExample {
    public static void main(String[] args) {
        BybitApiClientFactory factory = BybitApiClientFactory.newInstance();
        BybitAnnouncementRestClient client = factory.newAnnouncementRestClient();

        // Get Announcement
        var announcementInfoRequest = AnnouncementInfoRequest.builder().locale(LanguageSymbol.EN_US).build();
        var announceInfo = client.getAnnouncementInfo(announcementInfoRequest);
        System.out.println(announceInfo);
    }
}
