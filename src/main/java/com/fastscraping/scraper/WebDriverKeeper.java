package com.fastscraping.scraper;

import org.openqa.selenium.WebDriver;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class WebDriverKeeper {
    private static final ConcurrentHashMap<String, SeleniumSetup> keeper = new ConcurrentHashMap<>();

    private WebDriverKeeper() {
    }

    public synchronized static int addWebDrivers(final String clientId, final String jobId, final int maxInstances) {
        System.out.println("Adding the web drivers to the web driver keeper.");
        SeleniumSetup seleniumSetup = new SeleniumSetup(maxInstances);
        WebDriverKeeper.keeper.put(clientId + "/" + jobId, seleniumSetup);
        System.out.println("SeleniumSetup has been added to the WebDriverKeeper with " + maxInstances + " drivers.");
        return seleniumSetup.getMaxDrivers();
    }

    public synchronized static boolean addBackWebDriver(final String clientId, final String jobId,
                                                        final WebDriver driver) {
        System.out.println("Returning the driver back to the cache");
        return keeper.get(clientId + "/" + jobId).addWebDriver(driver);
    }

    public synchronized static Optional<WebDriver> getWebDriver(final String clientId, final String jobId) {
        System.out.println("Getting the driver from the keeper.");
        String clientIdJobId = clientId + "/" + jobId;
        if (keeper.containsKey(clientIdJobId)) {
            return keeper.get(clientIdJobId).getWebDriver();
        } else {
            return Optional.empty();
        }
    }

    public synchronized static boolean clearEntries(final String clientId, final String jobId) {
        if (!keeper.containsKey(clientId + "/" + jobId)) {
            return true;
        } else {
            int initialSize = keeper.size();
            keeper.get(clientId + "/" + jobId).clearAll();
            keeper.remove(clientId + "/" + jobId);
            int newSize = keeper.size();
            return initialSize - newSize == 1;
        }
    }
}
