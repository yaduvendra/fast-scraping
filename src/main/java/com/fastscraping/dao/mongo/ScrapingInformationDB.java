package com.fastscraping.dao.mongo;

import com.fastscraping.dao.InMemoryDaoInf;
import com.fastscraping.models.ScrapingInformation;
import com.mongodb.async.client.FindIterable;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
                .createIndex(Indexes.ascending("clientId", "jobId"), (result, t) -> {
                    System.out.println("Index " + scrapingInformationByClientidJobid + " created in db " + dbName);
                });
    }

    public void addScrapingInformation(ScrapingInformation information) {
        database.getCollection(scrapingInformationByClientidJobid, ScrapingInformation.class)
                .insertOne(information, (result, exception) -> {
                    System.out.println("The information has not been saved in the database.");
                });
    }

    public void addScrapingInformation(String jsonDoc, String clientId, String jobId) {
        MongoCollection<Document> col = database.getCollection(scrapingInformationByClientidJobid);

        List<Bson> clientIdJobIdFilter = new LinkedList<>();
        clientIdJobIdFilter.add(Filters.eq("clientId", clientId));
        clientIdJobIdFilter.add(Filters.eq("jobId", jobId));

        col.replaceOne(Filters.and(clientIdJobIdFilter),
                Document.parse(jsonDoc),
                ReplaceOptions.createReplaceOptions(new UpdateOptions().upsert(true)),
                (res, ex) -> {
                    System.out.println("There was exception while upserting... " + ex.getMessage());
                });
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

    public void getUnscrapedLinksInMemory(String clientId, String jobId, InMemoryDaoInf inMemoryDao) {

        List<Bson> filters = new LinkedList<>();

        filters.add(Filters.eq("clientId", clientId));
        filters.add(Filters.eq("jobId", jobId));

        Bson withFilters = Filters.and(filters);

        FindIterable<String> result = database.getCollection(linksToScrapebyClientIdJobId)
                .find(Filters.and(withFilters), String.class)
                .maxAwaitTime(2, TimeUnit.SECONDS);

        List<String> links = new LinkedList<>();

        result.forEach(links::add, (res, ex) -> {
                    System.out.println("Exception while getting unscraped links from Mongo. " + ex.getMessage());
                }
        );

        if(links.size() > 0) {
            inMemoryDao.addLinksToScrape(clientId, jobId, links);
        } else {
            //TODO: Start shutdown process when no link is found for scraping
        }
    }
}
