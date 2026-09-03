package crawler;

import crawler.state.CrawlState;
import crawler.strategy.ExtractionStrategy;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Module 5: Concurrency
 * The core engine that distributes crawling tasks across a thread pool.
 */
public class WebCrawler {
    private final int maxThreads;
    private final int maxPages;
    private final CrawlState state;
    private final List<ExtractionStrategy> strategies;
    
    // Thread-safe queue for pending URLs
    private final ConcurrentLinkedQueue<String> urlQueue;
    
    // Counter to track currently executing tasks
    private final AtomicInteger activeTasks;
    
    // Built-in Java 11+ HttpClient
    private final HttpClient httpClient;

    public WebCrawler(int maxThreads, int maxPages, CrawlState state, List<ExtractionStrategy> strategies) {
        this.maxThreads = maxThreads;
        this.maxPages = maxPages;
        this.state = state;
        this.strategies = strategies;
        this.urlQueue = new ConcurrentLinkedQueue<>();
        this.activeTasks = new AtomicInteger(0);
        
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public void startCrawling(String seedUrl) {
        urlQueue.add(seedUrl);
        state.markVisited(seedUrl);

        // Create the thread pool
        ExecutorService executor = Executors.newFixedThreadPool(maxThreads);

        System.out.println("Starting crawler with " + maxThreads + " threads. Max pages: " + maxPages);

        // Main orchestrator loop
        while (state.getVisitedCount() < maxPages) {
            String url = urlQueue.poll();

            if (url != null) {
                activeTasks.incrementAndGet();
                executor.submit(() -> crawlPage(url));
            } else {
                // If queue is empty but tasks are still active, wait a bit
                if (activeTasks.get() == 0) {
                    System.out.println("Queue empty and no active tasks. Stopping.");
                    break;
                }
                try {
                    Thread.sleep(100); // Sleep briefly to prevent busy waiting
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        // Graceful shutdown
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\nCrawling finished. Crawled " + state.getVisitedCount() + " pages.");
        state.printDomainStats();
    }

    private void crawlPage(String url) {
        try {
            if (state.getVisitedCount() >= maxPages) {
                return; // Stop processing if we've reached the limit
            }

            System.out.println("[" + Thread.currentThread().getName() + "] Crawling: " + url);
            state.recordDomainVisit(url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String html = response.body();

                // Apply all strategies (Module 1 & 2)
                for (ExtractionStrategy strategy : strategies) {
                    List<String> extracted = strategy.extract(url, html);
                    
                    // If it's a link strategy, add new valid links to the queue
                    if (strategy.getClass().getSimpleName().equals("LinkExtractionStrategy")) {
                        for (String link : extracted) {
                            // Only add if we haven't visited it and haven't hit the limit
                            if (state.getVisitedCount() < maxPages && state.markVisited(link)) {
                                urlQueue.add(link);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Module 3: Exception Handling
            // We catch generic Exception here to handle IOExceptions from HttpClient
            // and URISyntaxExceptions from bad URLs without crashing the crawler.
            System.err.println("Failed to crawl " + url + " - " + e.getMessage());
        } finally {
            activeTasks.decrementAndGet();
        }
    }
}
