package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.models.ActionsAndData;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

public class DataMiner {

    private final WebDriver driver;
    private final Actions seleniumActions;

    private DataMiner(WebDriver driver) {
        this.driver = driver;
        seleniumActions = new Actions(this.driver);
    }

    void mineData(ActionsAndData actionsAndData,
                  ScraperDaoInf scraperDao,
                  String urlToScrape,
                  String clientId,
                  String jobId) {

        String selector = actionsAndData.getSelector();

        actionsAndData.getActions().forEach(action -> {
            System.out.println("Going to execute the action - " + action + " for URL - " + urlToScrape);
            switch (action) {
                case HOVER:
                    hoverElement(selector);
                    break;
                case CLICK:
                    clickElement(selector);
                    break;
                case DELETE_ELEMENT:
                    deleteElememt(selector);
                    break;
                case GRAB_LINKS_TO_SCRAPE:
                    scraperDao.addLinksToScrape(clientId, jobId, grabLinksToScrape(selector));
                    break;
                case GRAB_LINKS_IN_GRID_TO_SCRAPE:
                    scraperDao.addLinksToScrape(clientId, jobId, grabLinksFromGridToScrape(selector));
                    break;
            }
        });

        actionsAndData.getDataToExtract().forEach(dataToExtract -> {

            System.out.println("Going to extract data for the URL " + urlToScrape);

            String storageKeyName = dataToExtract.getStorageKeyName();
            String selectorOfData = dataToExtract.getSelector();
            List<String> attributesToScrape = dataToExtract.getAttributes();
            boolean dataIsText = dataToExtract.isText();
            boolean dataIsImage = dataToExtract.isImage();

            String scrapedText = "";
            String imageUrl = "";

            if(dataIsText) {
                scrapedText += scrapeText(selector);
                scraperDao.addScrapedData(dataToExtract.getDatabase(), storageKeyName, scrapedText);
            } else if(dataIsImage) {
                //Download the image.
            }

        });
    }

    /**
     * Data scraping of the elements depending on the CSS selector
     */

    private String scrapeText(String selector) {
        WebElement webElement = driver.findElement(By.cssSelector(selector));
        return webElement.getText();
    }

    private String scrapeImage(String selector) {
        return "";
    }

    /**
     * Actions to be executed on the DOM elements
     *
     * TODO:: Use jQuery to hover over elements.
     */

    private void hoverElement(String selector) {
        WebElement element = driver.findElement(By.cssSelector(selector));
        seleniumActions.moveToElement(element);
    }

    private void clickElement(String selector) {
        driver.findElement(By.cssSelector(selector)).click();
    }

    private void deleteElememt(String selector) {
        String scriptToDelete = " document.querySelector('" + selector + "').remove();";
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        jsExecutor.executeScript("return" + scriptToDelete);
    }

    private List<String> grabLinksFromGridToScrape(String selector) {

        return driver.findElements(By.cssSelector(selector)).stream().flatMap(webElement -> {
            List<WebElement> linkElements = getAllDescendents(webElement, "a");

            return linkElements.stream()
                    .map(linkWebElement -> linkWebElement.getAttribute("href"));

        }).collect(Collectors.toList());
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
     * The builder for the DataMiner class
     */
    public static class ActionExecutorBuilder {
        private WebDriver driver;

        public ActionExecutorBuilder setDriver(WebDriver driver) {
            this.driver = driver;
            return this;
        }

        public DataMiner build() {
            return new DataMiner(this.driver);
        }
    }
}
