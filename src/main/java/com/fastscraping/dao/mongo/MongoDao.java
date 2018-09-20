package com.fastscraping.dao.mongo;


import com.fastscraping.dao.PersistentDaoInf;
import com.fastscraping.models.ScrapingInformation;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.fastscraping.util.ScraperThreadPools.persistDBThreadPool;

public class MongoDao implements PersistentDaoInf {

    /**
     * Keep only one mongo client whole application wide
     */
    private final MongoClient mongoClient;
    private final ScrapingInformationDB scrapingInformationDB;

    public MongoDao(final List<ServerAddress> mongoNodes) {

        /** Mention all the connection settings in the properties file */

        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(mongoNodes).build();

        ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                .minSize(2)
                .maxSize(250)
                .maxWaitQueueSize(1000)
                .maxConnectionIdleTime(5, TimeUnit.SECONDS)
                .build();

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.applySettings(clusterSettings))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
                .build();

        mongoClient = MongoClients.create(settings);
        scrapingInformationDB = new ScrapingInformationDB(mongoClient);
    }

    public final void addScrapingInforamtion(ScrapingInformation information) {
        persistDBThreadPool.submit(() -> {
            scrapingInformationDB.addScrapingInformation(information);
        });
    }

    public void addLinksToScrape(String clientId, String jobId, List<String> links) {
        persistDBThreadPool.execute(() -> {
            scrapingInformationDB.addLinksToScrape(clientId, jobId, links);
        });
    }

    public final void closeDBConnection() {
        mongoClient.close();
    }

    @Override
    public boolean addToScrapedLinks(String link, String clientId, String jobId) {
        return false;
    }

    @Override
    public boolean addSrapedData(String clientId, String jobId, Map<String, Map<String, Object>> collection) {
        persistDBThreadPool.submit(() -> {
            scrapingInformationDB.saveScrapedData(clientId, jobId, collection);
        });
        return false;
    }

    public Future<List<String>> getUnscrapedLinks(final String clientId, final String jobId) {
        return persistDBThreadPool.submit(() -> scrapingInformationDB.getUnscrapedLinks(clientId, jobId));
    }

    @Override
    public Future<List<String>> getLinksToScrape(String clientId, String jobId) {
        System.out.println("Getting the links to scrape from the Mongo DB");
        return persistDBThreadPool.submit(() -> scrapingInformationDB.getLinksToScrape(clientId, jobId));
    }

    @Override
    public Future<List<ScrapingInformation>> getScrapingInformation(String clientId, String jobId) {
        System.out.println("Submiting job to executor to get scraping information.");
        return persistDBThreadPool.submit(() -> scrapingInformationDB.getScrapingInformation(clientId, jobId)
                .stream()
                .filter(info -> info.isPresent() &&
                        info.get().getClientId().equals(clientId) &&
                        info.get().getJobId().equals(jobId))
                .map(Optional::get) // The present scraping information has already been filtered so can call .get()
                .collect(Collectors.toList()));
    }

    @Override
    public void updateScrapedTrue(String clientId, String jobId, String job) {

    }

}
