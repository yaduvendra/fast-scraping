package com.fastscraping.scraper;

import org.openqa.selenium.WebDriver;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebDriverKeeper {
    private final ConcurrentHashMap<String, SeleniumSetup> keeper = new ConcurrentHashMap<>();

    public WebDriverKeeper() {
    }

    public void addWebDrivers(final String clientId, final String jobId, final int maxInstances) {
        String clientIdJobId = clientId + "/" + jobId;
        keeper.put(clientIdJobId, new SeleniumSetup(maxInstances));
    }

    public Optional<WebDriver> getWebDriver(final String clientId, final String jobId) {
        String clientIdJobId = clientId + "/" + jobId;
        if (keeper.containsKey(clientIdJobId)) {
            return keeper.get(clientIdJobId).getWebDriver();
        } else {
            return Optional.empty();
        }
    }
}
