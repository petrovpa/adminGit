package com.bivgroup.seaweedfs.benchmark;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.routing.RoundRobinRouter;

// если не понятно читаем http://doc.akka.io/docs/akka/current/additional/faq.html?_ga=1.176890981.1707996468.1467787591

public class LoadTest {

    static final ConcurrentHashMap<String, byte[]> fsDb = new ConcurrentHashMap<>(100000);

    static Random random = new Random();

    static final URL MASTER_URL;
    static {
        try {
            MASTER_URL = new URL("http://172.16.100.123:9333");
        } catch (MalformedURLException u) {
            throw new RuntimeException(u);
        }
    }

    public static void main(String[] args) throws Exception {
        ActorSystem s = ActorSystem.create();

        ActorRef stats = s.actorOf(StatsCollector.mkProps(), "stats");
        ActorRef upload = s.actorOf(UploadRandomFile.mkProps(stats).withRouter(new RoundRobinRouter(100)), "upload");
        ActorRef read = s.actorOf(ReadAndCheckFile.mkProps(stats).withRouter(new RoundRobinRouter(100)), "read");
        ActorRef maestro = s.actorOf(Maestro.mkProps(upload, read), "maestro");

        long t = System.currentTimeMillis();
        while (true) {
            Thread.sleep(random.nextInt(10));

            if (System.currentTimeMillis() - t > 1000) {
                stats.tell(StatsCollector.Event.printStats, ActorRef.noSender());
                t = System.currentTimeMillis();
            }

            maestro.tell(new Object(), ActorRef.noSender());

        }

    }

}
