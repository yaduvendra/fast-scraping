package com.fastscraping.dao.mongo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fastscraping.models.ScrapingInformation;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.io.IOException;
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
                        Indexes.descending("jobId")));
    }

    public void addScrapingInformation(ScrapingInformation information) {

        Document doc = Document.parse(toJsonString(information));

        String clientId = information.getClientId();
        String jobId = information.getJobId();

        database.getCollection(scrapingInformationByClientidJobid)
                .replaceOne(and(eq("clientId", clientId), eq("jobId", jobId)),
                        doc,
                        new ReplaceOptions().upsert(true));
    }

    public List<Optional<ScrapingInformation>> getScrapingInformation(String clientId, String jobId) {

        FindIterable<Document> documents = database.getCollection(scrapingInformationByClientidJobid)
                .find(and(eq("clientId", clientId), eq("jobId", jobId)));

        List<Optional<ScrapingInformation>> scrapingInformation = new ArrayList<>();

        for(Document document: documents) {
            try {
                scrapingInformation.add(Optional.of(getScrapingInformationFromJson(document.toJson(MongoConfig.settings),
                        new TypeReference<ScrapingInformation>() {
                        })));
                System.out.println("......................................" + scrapingInformation.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Populated the linked with documents " + scrapingInformation.size());

        return scrapingInformation;
    }

    public void addLinksToScrape(String clientId, String jobId, List<String> links) {

        Map<String, Object> mapToInsert = new HashMap<>();
        mapToInsert.put("clientId", clientId);
        mapToInsert.put("jobId", jobId);
        mapToInsert.put("links", links);

        database.getCollection(linksToScrapebyClientIdJobId)
                .insertOne(new Document(mapToInsert));
    }

    public List<String> getLinksToScrape(String clientId, String jobId) {
        List<String> links = new LinkedList<>();
        database.getCollection(linksToScrapebyClientIdJobId)
                .find(filterWithClientIdJobId(clientId, jobId), String.class)
                .forEach((Block<String>) links::add);
        return links;
    }

    public List<String> getUnscrapedLinks(final String clientId, final String jobId) {

        FindIterable<String> result = database.getCollection(linksToScrapebyClientIdJobId)
                .find(filterWithClientIdJobId(clientId, jobId), String.class)
                .maxAwaitTime(2, TimeUnit.SECONDS);

        List<String> links = new LinkedList<>();

        result.forEach((Block<String>) links::add);
        return links;
    }

    public void saveScrapedData(String clientId, String jobId, Map<String, Map<String, Object>> collections) {
        Set<String> collectionNames = collections.keySet();

        collectionNames.forEach(collectionName -> {
            database.getCollection(collectionName)
                    .insertOne(new Document(collections.get(collectionName)));
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
