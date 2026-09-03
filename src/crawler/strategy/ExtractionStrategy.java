package crawler.strategy;

import java.util.List;

/**
 * Module 1 & 2: OOP Strategy Pattern
 * Interface defining the strategy for extracting data from HTML content.
 */
public interface ExtractionStrategy {
    List<String> extract(String url, String htmlContent);
}
