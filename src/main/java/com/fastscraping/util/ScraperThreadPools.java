package com.fastscraping.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class ScraperThreadPools {

    public static final ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10);
    public static final ExecutorService fixedThreadPoolExecutor = Executors.newFixedThreadPool(4);
    public static final ExecutorService persistDBThreadPool = Executors.newCachedThreadPool();

}
