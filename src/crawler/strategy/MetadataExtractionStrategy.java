package crawler.strategy;

import crawler.io.ResultWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy to extract meta descriptions and keywords.
 */
public class MetadataExtractionStrategy implements ExtractionStrategy {
    private static final Pattern META_PATTERN = Pattern.compile("<meta[^>]+name\\s*=\\s*['\"](description|keywords|author)['\"][^>]+content\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
    private final ResultWriter writer;

    public MetadataExtractionStrategy(ResultWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> metadataList = new ArrayList<>();
        if (htmlContent == null) return metadataList;

        Matcher matcher = META_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String name = matcher.group(1);
            String content = matcher.group(2);
            metadataList.add(name + ": " + content);
            try {
                writer.writeResult("URL: " + url + " | Meta " + name + ": " + content);
            } catch (IOException e) {
                System.err.println("Error writing metadata to file: " + e.getMessage());
            }
        }
        
        // Open Graph tags (og:title, og:description, etc.)
        Pattern ogPattern = Pattern.compile("<meta[^>]+property\\s*=\\s*['\"](og:[^'\"]+)['\"][^>]+content\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
        Matcher ogMatcher = ogPattern.matcher(htmlContent);
        while (ogMatcher.find()) {
            String property = ogMatcher.group(1);
            String content = ogMatcher.group(2);
            metadataList.add(property + ": " + content);
            try {
                writer.writeResult("URL: " + url + " | " + property + ": " + content);
            } catch (IOException e) {
                System.err.println("Error writing Open Graph metadata to file: " + e.getMessage());
            }
        }
        
        return metadataList;
    }
}
