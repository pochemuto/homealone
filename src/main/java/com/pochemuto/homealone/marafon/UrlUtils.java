package com.pochemuto.homealone.marafon;

import org.springframework.web.util.UriComponentsBuilder;

public class UrlUtils {
    private UrlUtils() {
    }

    public static UriComponentsBuilder url(String base) {
        return UriComponentsBuilder.fromHttpUrl(base);
    }
}
