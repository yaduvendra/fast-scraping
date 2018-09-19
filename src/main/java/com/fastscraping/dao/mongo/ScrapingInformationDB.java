package com.fastscraping.dao.mongo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fastscraping.models.ScrapingInformation;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.async.client.MongoIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.fastscraping.util.JsonHelper.getScrapingInformationFromJson;
import static com.fastscraping.util.JsonHelper.toJsonString;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ScrapingInformationDB {
    /**
     * Database name
     */
    private final String dbName = "scraping_information";

    /**
     * Collection names
     */
    private final String scrapingInformationByClientidJobid = "scraping_information_by_clientid_jobid";
    private final String linksToScrapebyClientIdJobId = "links_to_scrape_by_clientid_jobid";

    private MongoDatabase database;

    public ScrapingInformationDB(MongoClient mongoClient) {
        database = mongoClient.getDatabase(dbName); //TODO: Mention db name in properties file?
        database.getCollection(scrapingInformationByClientidJobid)
                .createIndex(Indexes.compoundIndex(Indexes.descending("clientId"),
                        Indexes.descending("jobId")), (res, ex) -> {
                    ex.printStackTrace();
                    System.out.println("Exception while creating the index in Mongo DB");
                });
    }

    public void addScrapingInformation(ScrapingInformation information) {

        Document doc = Document.parse(toJsonString(information));

        String clientId = information.getClientId();
        String jobId = information.getJobId();

        database.getCollection(scrapingInformationByClientidJobid)
                .replaceOne(and(eq("clientId", clientId), eq("jobId", jobId)),
                        doc,
                        new UpdateOptions().upsert(true),
                        (result, ex) -> System.out.println("The information has not been saved in the Mongo database. "
                                + ex.getMessage()));
    }

    public MongoIterable<ScrapingInformation> getScrapingInformation(String clientId, String jobId) {
        List<ScrapingInformation> scrapingInformation = new LinkedList<>();

        FindIterable<Document> scrapingInfo = database.getCollection(scrapingInformationByClientidJobid)
                .find(and(eq("clientId", clientId), eq("jobId", jobId)))
                .maxAwaitTime(2, TimeUnit.SECONDS);
    }

    public void addLinksToScrape(String clientId, String jobId, List<String> links) {

        Map<String, Object> mapToInsert = new HashMap<>();
        mapToInsert.put("clientId", clientId);
        mapToInsert.put("jobId", jobId);
        mapToInsert.put("links", links);

        database.getCollection(linksToScrapebyClientIdJobId)
                .insertOne(new Document(mapToInsert), (result, ex) -> {
                    System.out.println("There was error while trying to insert links to scrape.");
                });
    }

    public List<String> getLinksToScrape(String clientId, String jobId) {
        List<String> links = new LinkedList<>();
        database.getCollection(linksToScrapebyClientIdJobId)
                .find(filterWithClientIdJobId(clientId, jobId), String.class)
                .forEach(links::add, ((result, ex) -> {
                    System.out.println("Error while trying to find the links to scrape from the " +
                            "Mongo DB. " + ex.getMessage());
                }));
        return links;
    }

    public List<String> getUnscrapedLinks(final String clientId, final String jobId) {

        FindIterable<String> result = database.getCollection(linksToScrapebyClientIdJobId)
                .find(filterWithClientIdJobId(clientId, jobId), String.class)
                .maxAwaitTime(2, TimeUnit.SECONDS);

        List<String> links = new LinkedList<>();

        result.forEach(links::add, (res, ex) -> System.out.println("Exception while getting unscraped " +
                "links from Mongo. " + ex.getMessage())
        );

        return links;
    }

    public void saveScrapedData(String clientId, String jobId, Map<String, Map<String, Object>> collections) {
        Set<String> collectionNames = collections.keySet();

        collectionNames.forEach(collectionName -> {
            database.getCollection(collectionName)
                    .insertOne(new Document(collections.get(collectionName)), (result, t) -> {
                        System.out.println("The data got inserted for client - " + clientId + " and job - " + jobId);
                    });
        });
    }

    private Bson filterWithClientIdJobId(String clientId, String jobId) {
        List<Bson> filters = new LinkedList<>();

        System.out.println("Filtering for clientId : " + clientId + " jobId : " + jobId);

        filters.add(eq("clientId", clientId));
        filters.add(eq("jobId", jobId));

        Bson withFilters = Filters.and(filters);
        return Filters.and(withFilters);
    }
}
