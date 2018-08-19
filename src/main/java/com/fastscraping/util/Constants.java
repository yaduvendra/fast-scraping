package com.fastscraping.util;

public class Constants {
    /*public static final String scrapingInformationMapName = "scrapingInformationMapName";
    public static final String scrapingInfoURLRegexSet = "scrapingInfoURLRegexSet";
    public static final String scrapingInfoUniqueTagSet = "scrapingInfoUniqueTagSet";
    public static final String scrapingInfoUniqueStringSet = "scrapingInfoUniqueStringSet";
    public static final String scrapingInformationWrapperSet = "scrapingInformationWrapperSet";*/

    private static final String urlRegexMapName = "urlRegexMapName";
    private static final String uniqueTagMapName = "uniqueTagMap";
    private static final String uniqueStringMapName = "uniqueStringMap";

    public static synchronized String getUrlRegexMapName(String uniqueIdentifier) {
        return urlRegexMapName + uniqueIdentifier;
    }

    public static synchronized String getUniqueTagMapName(String uniqueIdentifier) {
        return uniqueTagMapName + uniqueIdentifier;
    }

    public static synchronized String getUniqueStringMapName(String uniqueIdentifier) {
        return uniqueStringMapName + uniqueIdentifier;
    }

}
