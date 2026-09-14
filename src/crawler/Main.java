package crawler;

import crawler.io.ResultWriter;
import crawler.state.CrawlState;
import crawler.strategy.CleanTextExtractionStrategy;
import crawler.strategy.EmailExtractionStrategy;
import crawler.strategy.ExtractionStrategy;
import crawler.strategy.ImageExtractionStrategy;
import crawler.strategy.LinkExtractionStrategy;
import crawler.strategy.MetadataExtractionStrategy;
import crawler.strategy.PhoneNumberExtractionStrategy;
import crawler.strategy.TitleExtractionStrategy;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Main entry point for the Concurrent Web Crawler CLI Application.
 */
public class Main {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java crawler.Main <SeedURL> <MaxThreads> <MaxPages>");
            System.out.println("Example: java crawler.Main https://books.toscrape.com 10 50");
            return;
        }

        String seedUrl = args[0];
        int maxThreads;
        int maxPages;

        try {
            maxThreads = Integer.parseInt(args[1]);
            maxPages = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            System.err.println("Error: MaxThreads and MaxPages must be integers.");
            return;
        }

        String outputFile = "results.csv";

        // Try-with-resources to ensure the writer is closed properly
        try (ResultWriter writer = new ResultWriter(outputFile)) {
            System.out.println("Initialized ResultWriter. Extracted data will be saved to " + outputFile);

            CrawlState state = new CrawlState();

            // Instantiate strategies (Modules 1 & 2)
            List<ExtractionStrategy> strategies = Arrays.asList(
                    new LinkExtractionStrategy(),
                    new EmailExtractionStrategy(writer),
                    new TitleExtractionStrategy(writer), // Added to extract Page Titles!
                    new ImageExtractionStrategy(writer),
                    new MetadataExtractionStrategy(writer),
                    new PhoneNumberExtractionStrategy(writer),
                    new CleanTextExtractionStrategy(writer)
            );

            WebCrawler crawler = new WebCrawler(maxThreads, maxPages, state, strategies);
            
            // Start the crawl process
            long startTime = System.currentTimeMillis();
            crawler.startCrawling(seedUrl);
            long endTime = System.currentTimeMillis();
            
            System.out.println("Total Execution Time: " + (endTime - startTime) + " ms");

        } catch (IOException e) {
            System.err.println("Fatal Error: Could not initialize ResultWriter - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Fatal Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
