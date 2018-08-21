package com.fastscraping.scraper;

import com.fastscraping.dao.redis.RedisDao;
import com.fastscraping.models.ElementWithActions;
import com.fastscraping.util.JsonHelper;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ActionFilter {

    private final RedisDao redisDao;

    public ActionFilter(RedisDao redisDao){
        this.redisDao = redisDao;
    }

    public List<ElementWithActions> getActionsByLink(String link) throws MalformedURLException {

        return redisDao.getElementsWithActionsByLink(link)
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}
