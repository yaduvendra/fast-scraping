package com.fastscraping.scraper;

import com.fastscraping.util.MaxBrowsersExceededException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SeleniumSetup {

    private final ConcurrentLinkedQueue<WebDriver> webDrivers;
    private final int maxBrowsers;

    public SeleniumSetup(final int maxBrowsers) {
        this.maxBrowsers = maxBrowsers;
        this.webDrivers = new ConcurrentLinkedQueue<>();

        for(int i = 0; i < maxBrowsers; i++) {
            webDrivers.add(new FirefoxDriver());
        }
    }

    public int getMaxBrowsers() {
        return maxBrowsers;
    }

    public boolean addWebDriver(WebDriver driver) {
        if(webDrivers.size() < maxBrowsers) {
            return webDrivers.add(driver);
        } else {
            throw new MaxBrowsersExceededException("The number of browsers " + maxBrowsers + " already exist.");
        }
    }

    public Optional<WebDriver> getWebDriver() {
        if(webDrivers.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(webDrivers.remove());
        }
    }
}
