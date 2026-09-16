package crawler.state;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Module 4: Collections
 * Manages the state of the crawler using Thread-Safe Collections.
 */
public class CrawlState {
    // A thread-safe HashSet (backed by ConcurrentHashMap) to track visited URLs in O(1) time
    private final Set<String> visitedUrls = ConcurrentHashMap.newKeySet();
    
    // A ConcurrentHashMap to track how many pages we've crawled per domain
    private final ConcurrentHashMap<String, Integer> domainCounts = new ConcurrentHashMap<>();

    // Counter to track how many pages were actually crawled
    private final AtomicInteger crawledCount = new AtomicInteger(0);

    /**
     * Checks if a URL has been discovered/queued. If not, adds it and returns true.
     * This operation is thread-safe.
     * 
     * @param url The URL to check and add.
     * @return true if the URL was NOT already in the set (i.e., we should visit it).
     */
    public boolean markVisited(String url) {
        return visitedUrls.add(url);
    }

    /**
     * Increments the count for the domain of the given URL.
     */
    public void recordDomainVisit(String url) {
        crawledCount.incrementAndGet();
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            if (domain != null) {
                // Thread-safe increment
                domainCounts.merge(domain, 1, Integer::sum);
            }
        } catch (URISyntaxException e) {
            // Ignore malformed URLs for domain tracking
        }
    }

    public int getVisitedCount() {
        return crawledCount.get();
    }

    public void printDomainStats() {
        System.out.println("\n--- Domain Crawl Statistics ---");
        domainCounts.forEach((domain, count) -> {
            System.out.println(domain + " : " + count + " pages");
        });
        System.out.println("-------------------------------\n");
    }
}
