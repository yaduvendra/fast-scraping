package com.fastscraping.scraper;

import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.HTMLTag.HTMLTagWithText;
import com.fastscraping.models.WebpageDetails;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataMiner {

    private final WebDriver driver;
    private final Actions seleniumActions;

    private DataMiner(WebDriver driver) {
        this.driver = driver;
        seleniumActions = new Actions(this.driver);
    }

    void mineData(List<WebpageDetails> webpageDetailsList,
                  ScraperDaoInf scraperDao,
                  String urlToScrape,
                  String clientId,
                  String jobId) {

        System.out.println("Starting mining");

        Map<String, Map<String, Object>> collection = new HashMap<>(); //[Collection] contains {key -> value} documents

        filterActionsAndData(webpageDetailsList).forEach(actionsAndData -> {

            System.out.println("Filtered one actionsAndData successfully!!");

            String parentSelector = actionsAndData.getSelector();

            actionsAndData.getActions().forEach(action -> {
                System.out.println("Going to execute the action - " + action + " for URL - " + urlToScrape);
                switch (action) {
                    case HOVER:
                        hoverElement(parentSelector);
                        break;
                    case CLICK:
                        clickElement(parentSelector);
                        break;
                    case DELETE_ELEMENT:
                        deleteElememt(parentSelector);
                        break;
                    case GRAB_LINKS_TO_SCRAPE:
                        System.out.println("*** Grabbing new links to scrape ***");
                        scraperDao.addLinksToScrape(clientId, jobId, grabLinksToScrape(parentSelector));
                        break;
                    case GRAB_LINKS_IN_GRID_TO_SCRAPE:
                        scraperDao.addLinksToScrape(clientId, jobId, grabLinksFromGridToScrape(parentSelector));
                        break;
                }
            });

            Map<String, Object> keyValueDoc = new HashMap<>(); //Object is taken to be compatible with Mongo DB's API

            actionsAndData.getDataToExtract().forEach(dataToExtract -> {
                System.out.println("Going to extract data for the URL " + urlToScrape);

                String collectionName = dataToExtract.getCollection();

                if (!collection.containsKey(collectionName)) {
                    collection.put(collectionName, keyValueDoc);
                }

                //This will be the name of the key for single text or image mined from the element
                String storageKeyName = dataToExtract.getStorageKeyName();

                //Selector of the child element from which the data is supposed to be scraped
                String selectorOfChildWithData = dataToExtract.getSelector();

                //The attributes of the child element which need to be extracted
                List<String> attributesToScrape = dataToExtract.getAttributes();

                boolean dataIsText = dataToExtract.isText();
                boolean dataIsImage = dataToExtract.isImage();

                if (dataIsText) {
                    keyValueDoc.put(storageKeyName, scrapeText(parentSelector, selectorOfChildWithData));
                } else if (dataIsImage) {
                    //Download the image. And put the local storage path to {key -> value} mapping
                }

                attributesToScrape.forEach(attribute -> {
                    keyValueDoc.put(attribute, scrapeAttribute(parentSelector, selectorOfChildWithData, attribute));
                });
            });
        });

        if (collection.size() > 0) {
            scraperDao.addScrapedData(clientId, jobId, collection);
        }
    }

    private List<ActionsAndData> filterActionsAndData(List<WebpageDetails> webpageDetailsList) {
        System.out.println("Filtering the actions and data based on " + webpageDetailsList.size() + "webpageDetails");
        String currentURL = driver.getCurrentUrl();
        System.out.println("Current URL to filter actions and data is " + currentURL);
        return webpageDetailsList
                .stream()
                .filter(webpageDetails -> {
                    System.out.println("Taking decisions");
                    return isUrlRegexMatches(webpageDetails, currentURL) ||
                            isUniqueElementExists(webpageDetails) ||
                            isPageContainsUniqueString(webpageDetails);
                })
                .flatMap(webpageDetails -> webpageDetails.getActionsAndData().stream())
                .collect(Collectors.toList());
    }

    /**
     * Is UrlRegex is given in the webpage information then match it with current webpage's URL
     **/
    private boolean isUrlRegexMatches(WebpageDetails webpageDetails, String currentURL) {
        String givenUrlRegex = webpageDetails.getUrlRegex();

        System.out.println("Current URL : " + currentURL);
        System.out.println("URL regex : " + givenUrlRegex);

        return givenUrlRegex != null &&
                !givenUrlRegex.trim().equals("") &&
                currentURL.matches(givenUrlRegex);
    }

    /**
     * If a unique element exists on the webpage
     */
    private boolean isUniqueElementExists(WebpageDetails webpageDetails) {
        HTMLTagWithText uniqueTag = webpageDetails.getUniqueTag();
        WebElement uniqueElement = driver.findElement(By.cssSelector(uniqueTag.getSelector()));

        System.out.println("Given Unique tag is not null : " + uniqueTag.isNotNull());
        System.out.println("Given unique tag text : " + uniqueTag.getText());
        System.out.println("Found uninque tag text : " + uniqueElement.getText());

        return uniqueTag.isNotNull() && uniqueElement != null &&
                uniqueElement.getText().equals(uniqueTag.getText());
    }

    /**
     * If the page contains a unique URL
     */
    private boolean isPageContainsUniqueString(WebpageDetails webpageDetails) {

        System.out.println("Does page contain unique string " + webpageDetails.getUniqueStringOnPage() + " : " +
                driver.findElement(By.tagName("body")).getText().contains(webpageDetails.getUniqueStringOnPage()));

        return driver.findElement(By.tagName("body"))
                .getText()
                .contains(webpageDetails.getUniqueStringOnPage());
    }

    /**
     * Data scraping of the elements depending on the CSS selector
     */

    private String scrapeAttribute(String parentSelector, String selectorOfChildWithData, String attribute) {
        return driver
                .findElement(By.cssSelector(parentSelector))
                .findElement(By.cssSelector(selectorOfChildWithData))
                .getAttribute(attribute);
    }

    private String scrapeText(String parentSelector, String selectorOfChildWithData) {
        return driver
                .findElement(By.cssSelector(parentSelector))
                .findElement(By.cssSelector(selectorOfChildWithData))
                .getText();
    }

    private String scrapeImage(String selector) {
        return "";
    }

    /**
     * Actions to be executed on the DOM elements
     * <p>
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
