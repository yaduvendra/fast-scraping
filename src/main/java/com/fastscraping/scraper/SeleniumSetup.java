package com.fastscraping.scraper;

import com.fastscraping.util.MaxBrowsersExceededException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public class SeleniumSetup {

    private final ConcurrentLinkedQueue<WebDriver> webDrivers;
    private final int maxDrivers;

    public SeleniumSetup(final int maxDrivers) {
        this.maxDrivers = maxDrivers;
        this.webDrivers = new ConcurrentLinkedQueue<>();
        System.out.println("ConcurrentLinkedQueue has been created for SeleniumSetup.");
        for(int i = 0; i < maxDrivers; i++) {
            webDrivers.add(new FirefoxDriver());
        }
        System.out.println(maxDrivers + " drivers of FirefoxDriver have benn added.");
    }

    public int getMaxDrivers() {
        return maxDrivers;
    }

    public boolean addWebDriver(WebDriver driver) {
        if(webDrivers.size() < maxDrivers) {
            return webDrivers.add(driver);
        } else {
            throw new MaxBrowsersExceededException("The number of browsers " + maxDrivers + " already exist.");
        }
    }

    public Optional<WebDriver> getWebDriver() {
        if(webDrivers.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(webDrivers.remove());
        }
    }

    public boolean clearAll() {
        webDrivers.clear();
        return webDrivers.size() == 0;
    }
}
