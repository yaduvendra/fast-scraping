package com.fastscraping.dao.mongo;


import com.fastscraping.dao.ScraperDaoInf;
import com.fastscraping.dao.PersistentDaoInf;
import com.fastscraping.models.ActionsAndData;
import com.fastscraping.models.ScrapingInformation;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.connection.ClusterSettings;
import com.mongodb.connection.ConnectionPoolSettings;
import com.mongodb.connection.netty.NettyStreamFactoryFactory;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static com.fastscraping.util.ScraperThreadPools.persistDBThreadPool;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDao implements PersistentDaoInf {

    /**
     * Keep only one mongo client whole application wide
     */
    private final MongoClient mongoClient;
    private final ScrapingInformationDB scrapingInformationDB;
    private final ScraperDaoInf inMemoryDao;

    public MongoDao(final List<ServerAddress> mongoNodes, final ScraperDaoInf inMemoryDao) {

        /** Mention all the connection settings in the properties file */

        ClusterSettings clusterSettings = ClusterSettings.builder().hosts(mongoNodes).build();

        ConnectionPoolSettings poolSettings = ConnectionPoolSettings.builder()
                .minSize(2)
                .maxSize(250)
                .maxWaitQueueSize(1000)
                .maxConnectionIdleTime(5, TimeUnit.SECONDS)
                .build();

        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClients.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyToClusterSettings(builder -> builder.applySettings(clusterSettings))
                .applyToConnectionPoolSettings(builder -> builder.applySettings(poolSettings))
                .streamFactoryFactory(NettyStreamFactoryFactory.builder().build())
                .codecRegistry(pojoCodecRegistry)
                .build();

        mongoClient = MongoClients.create(settings);
        scrapingInformationDB = new ScrapingInformationDB(mongoClient);
        this.inMemoryDao = inMemoryDao;
    }

    public final void addScrapingInforamtion(ScrapingInformation information) {
        persistDBThreadPool.submit(() -> {
            System.out.println("Adding the scraping information to the Mongo DB");
            scrapingInformationDB.addScrapingInformation(information);
        });
    }

    public final void addScrapingInforamtion(String jsonDoc, String clientId, String jobId) {
        persistDBThreadPool.execute(() -> {
            scrapingInformationDB.addScrapingInformation(jsonDoc, clientId, jobId);
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
    public boolean saveSrapedData(String database, String key, String data) {
        return false;
    }

    public void getUnscrapedLinksInMemory(String clientId, String jobId) {
        persistDBThreadPool.submit(() -> {
            scrapingInformationDB.getUnscrapedLinksInMemory(clientId, jobId, inMemoryDao);
        });
    }

    @Override
    public List<String> getLinksToScrape(String clientId, String jobId) {
        return null;
    }

    @Override
    public List<Optional<ActionsAndData>> getElementsWithActionsByLink(String link) throws MalformedURLException {
        return null;
    }

    @Override
    public void updateScrapedTrue(String clientId, String jobId, String job) {

    }

}
