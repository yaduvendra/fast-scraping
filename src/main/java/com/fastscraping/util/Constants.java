package com.fastscraping.util;

public class Constants {
    /*public static final String scrapingInformationMapName = "scrapingInformationMapName";
    public static final String scrapingInfoURLRegexSet = "scrapingInfoURLRegexSet";
    public static final String scrapingInfoUniqueTagSet = "scrapingInfoUniqueTagSet";
    public static final String scrapingInfoUniqueStringSet = "scrapingInfoUniqueStringSet";
    public static final String scrapingInformationWrapperSet = "scrapingInformationWrapperSet";*/

    private static final String linksToScrapeSet = "linksToScrapeSet";
    private static final String scrapedLinkSet = "scrapedLinkSet";
    public static final String scrapingInformationMap = "scrapingInformationMap";

    public static synchronized String linksToScrapeSetName(String uniqueIdentifier) {
        return RedisUtils.encodeRedisKey(linksToScrapeSet + uniqueIdentifier);
    }

    public static synchronized String scrapedLinkSetName(String uniqueIdentifier) {
        return RedisUtils.encodeRedisKey(scrapedLinkSet + uniqueIdentifier);
    }

}
