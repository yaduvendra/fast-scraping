package com.fastscraping.scraper;

import org.openqa.selenium.WebDriver;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebDriverKeeper {
    private static final ConcurrentHashMap<String, SeleniumSetup> keeper = new ConcurrentHashMap<>();

    public WebDriverKeeper() {
    }

    public static int addWebDrivers(final String clientId, final String jobId, final int maxInstances) {
        return keeper.put(clientId + "/" + jobId, new SeleniumSetup(maxInstances)).getMaxBrowsers();
    }

    public static boolean addBackWebDriver(final String clientId, final String jobId, final WebDriver driver) {
        return keeper.get(clientId + "/" + jobId).addWebDriver(driver);
    }

    public static Optional<WebDriver> getWebDriver(final String clientId, final String jobId) {
        String clientIdJobId = clientId + "/" + jobId;
        if (keeper.containsKey(clientIdJobId)) {
            return keeper.get(clientIdJobId).getWebDriver();
        } else {
            return Optional.empty();
        }
    }
}
