package com.fastscraping.scraper;

import org.openqa.selenium.WebDriver;

public class SeleniumSetup {

    private final WebDriver webDriver;

    public SeleniumSetup(WebDriver webDriver) {
        this.webDriver = webDriver;
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }
}
