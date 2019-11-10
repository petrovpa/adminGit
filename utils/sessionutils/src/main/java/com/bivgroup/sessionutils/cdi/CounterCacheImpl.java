package com.bivgroup.sessionutils.cdi;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

public class CounterCacheImpl implements CounterCache {

    @Inject
    private SessionCounter sc;

    @Override
    public SessionCounter getSessionCounter() {
        return sc;
    }

    public CounterCacheImpl() {
    }

    @PostConstruct
    void init() {
    }

}


