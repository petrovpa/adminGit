package com.bivgroup.querybuilder.common.idcache;

import java.util.concurrent.atomic.AtomicLong;

public class IdCacheItem {
    private volatile AtomicLong currentValue = new AtomicLong(0L);
    private volatile Long maxValue = 0L;
    private volatile Long threshold = 0L;
    private final String tableName;
    private final Integer bufferSize;
    private final Object monitor = new Object();
    private final Object thresholdMonitor = new Object();
    private Boolean refreshing = false;
    private static Boolean error = false;

    public IdCacheItem(String tableName, Integer bufferSize) {
        this.tableName = tableName;
        this.bufferSize = bufferSize;
        this.refresh();
    }

    public Long getNextId(Integer quantity) throws IdObtainedException {
        Object var2;
        if (this.currentValue.get() + (long)quantity >= this.threshold && !this.refreshing) {
            var2 = this.thresholdMonitor;
            synchronized(this.thresholdMonitor) {
                if (this.currentValue.get() + (long)quantity >= this.threshold && !this.refreshing) {
                    this.refresh(quantity);
                }
            }
        }

        if (this.currentValue.get() + (long)quantity >= this.maxValue) {
            var2 = this.monitor;
            synchronized(this.monitor) {
                Long var10000;
                try {
                    this.monitor.wait();
                    if (error) {
                        throw new IdObtainedException("Error getting new ID. Probably database problem occured");
                    }

                    var10000 = this.getNextId(quantity);
                } catch (InterruptedException var6) {
                    return this.currentValue.getAndAdd((long)quantity);
                }

                return var10000;
            }
        } else {
            return this.currentValue.getAndAdd((long)quantity);
        }
    }

    public Long getNextId() throws IdObtainedException {
        return this.getNextId(1);
    }

    private void refresh() {
        this.refresh(0);
    }

    private void refresh(Integer quantity) {
        this.refreshing = true;
        error = false;
        IdCacheItem.IdCacheUpdater idCacheUpdater = new IdCacheItem.IdCacheUpdater(quantity);
        (new Thread(idCacheUpdater)).start();
    }

    private class IdCacheUpdater implements Runnable {
        private Integer quantity = 0;

        public IdCacheUpdater(Integer quantity) {
            this.quantity = quantity;
        }

        public void run() {
            Integer bufSize = IdCacheItem.this.bufferSize > this.quantity ? IdCacheItem.this.bufferSize : IdCacheItem.this.bufferSize + this.quantity;

            try {
                if (IdCacheItem.this.currentValue.get() == 0L && this.quantity == 0) {
                    IdCacheItem.this.currentValue = new AtomicLong(ExtIdLoader.load(IdCacheItem.this.tableName, IdCacheItem.this.bufferSize));
                    IdCacheItem.this.maxValue = IdCacheItem.this.currentValue.get() + (long)IdCacheItem.this.bufferSize;
                } else {
                    IdCacheItem.this.maxValue = ExtIdLoader.load(IdCacheItem.this.tableName, bufSize) + (long)bufSize;
                }
            } catch (Exception var5) {
                IdCacheItem.error = true;
            }

            if (!IdCacheItem.error) {
                IdCacheItem.this.threshold = IdCacheItem.this.maxValue - (long)((double)IdCacheItem.this.bufferSize * 0.1D);
                IdCacheItem.this.currentValue.set(IdCacheItem.this.maxValue - (long)bufSize);
            }

            IdCacheItem.this.refreshing = false;
            synchronized(IdCacheItem.this.monitor) {
                IdCacheItem.this.monitor.notifyAll();
            }
        }
    }
}

