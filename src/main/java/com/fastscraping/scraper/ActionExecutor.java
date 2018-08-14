package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.models.ElementWithActions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

public class ActionExecutor {

    private final WebDriver driver;
    private final JavascriptExecutor jsExecutor;
    private final ScraperDaoInf scraperDao;
    private final String rootURL;

    private final Actions seleniumActions;

    private ActionExecutor(String rootURL, WebDriver driver, JavascriptExecutor jsExecutor, ScraperDaoInf scraperDao) {
        this.rootURL = rootURL;
        this.driver = driver;
        this.jsExecutor = jsExecutor;
        this.scraperDao = scraperDao;

        seleniumActions = new Actions(this.driver);
    }

    void executeAction(ElementWithActions elementWithActions) {
        String selector = elementWithActions.getSelector();

        elementWithActions.getActions().forEach(action -> {

            System.out.println("Going to execute the action - " + action + " for root URL - " + rootURL);

            switch (action) {
                case HOVER: hoverElement(selector); break;
                case CLICK: clickElement(selector); break;
                case DELETE_ELEMENT: deleteElememt(selector); break;
                case GRAB_LINKS_TO_SCRAPE: scraperDao.saveLinksToScrape(rootURL, grabLinksToScrape(selector)); break;
            }
        });
    }

    /**
     * Actions to be executed on the DOM elements
     */

    private void hoverElement(String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        seleniumActions.moveToElement(element);
    }

    private void clickElement(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }

    private void deleteElememt(String selector) {
        String scriptToDelete = " document.querySelector('"+ selector +"').remove();";
        jsExecutor.executeScript("return" + scriptToDelete);
    }

    private List<String> grabLinksToScrape(String selector) {
        List<WebElement> linkElements = getAllDescendents(driver.findElement(By.cssSelector(selector)), "a");

        return linkElements
                .stream()
                .map(linkWebElement -> linkWebElement.getAttribute("href"))
                .collect(Collectors.toList());
    }

    private List<WebElement> getAllDescendents(WebElement parent, String descendentTag) {
        return parent.findElements(By.xpath(".//" + descendentTag));
    }


    /**
     * The builder for the ActionExecutor class
     */
    public static class ActionExecutorBuilder {
        private WebDriver driver;
        private JavascriptExecutor jsExecutor;
        private ScraperDaoInf scraperDao;
        private String rootURL;

        public ActionExecutorBuilder setDriver(WebDriver driver) {
            this.driver = driver;
            return this;
        }

        public ActionExecutorBuilder setRootURL(String rootURL) {
            this.rootURL = rootURL;
            return this;
        }

        public ActionExecutorBuilder setJSExecutor(JavascriptExecutor jsExecutor) {
            this.jsExecutor = jsExecutor;
            return this;
        }

        public ActionExecutorBuilder setScraperDao(ScraperDaoInf scraperDao) {
            this.scraperDao = scraperDao;
            return this;
        }

        public ActionExecutor build() {
            return new ActionExecutor(this.rootURL, this.driver, this.jsExecutor, this.scraperDao);
        }
    }
}
