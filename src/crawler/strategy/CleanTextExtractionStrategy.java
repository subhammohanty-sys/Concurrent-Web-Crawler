package crawler.strategy;

import crawler.io.ResultWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Strategy to extract clean readable text by stripping HTML tags.
 */
public class CleanTextExtractionStrategy implements ExtractionStrategy {
    private final ResultWriter writer;

    public CleanTextExtractionStrategy(ResultWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> textResult = new ArrayList<>();
        if (htmlContent == null) return textResult;

        // Strip HTML tags and scripts/styles
        String cleanText = htmlContent.replaceAll("(?is)<script.*?>.*?</script>", " ")
                                      .replaceAll("(?is)<style.*?>.*?</style>", " ")
                                      .replaceAll("<[^>]*>", " ");
        // Normalize whitespace
        cleanText = cleanText.replaceAll("\\s+", " ").trim();
        
        // We write the first 150 characters as a snippet to avoid huge CSV entries per page.
        if (cleanText.length() > 0) {
            textResult.add(cleanText);
            String snippet = cleanText.length() > 150 ? cleanText.substring(0, 150) + "..." : cleanText;
            try {
                writer.writeResult("URL: " + url + " | Text Snippet: " + snippet);
            } catch (IOException e) {
                System.err.println("Error writing text to file: " + e.getMessage());
            }
        }
        return textResult;
    }
}
