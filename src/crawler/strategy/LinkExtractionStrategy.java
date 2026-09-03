package crawler.strategy;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy to extract all links (absolute and relative) from HTML content.
 */
public class LinkExtractionStrategy implements ExtractionStrategy {
    // Basic regex to find URLs in href attributes
    private static final Pattern LINK_PATTERN = Pattern.compile("href=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> links = new ArrayList<>();
        if (htmlContent == null) return links;
        
        Matcher matcher = LINK_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String link = matcher.group(1);
            try {
                // Resolve relative URLs based on the current page's URL
                URI baseUri = new URI(url);
                URI resolvedUri = baseUri.resolve(link).normalize();
                
                // Only follow HTTP/HTTPS links
                String scheme = resolvedUri.getScheme();
                if (scheme != null && (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
                    // Strip fragments/queries for cleaner crawling
                    String finalUrl = resolvedUri.getScheme() + "://" + resolvedUri.getHost() + resolvedUri.getPath();
                    links.add(finalUrl);
                }
            } catch (Exception e) {
                // Ignore malformed links silently
            }
        }
        return links;
    }
}
