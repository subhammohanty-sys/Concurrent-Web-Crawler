package crawler.strategy;

import crawler.io.ResultWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy to extract email addresses and write them to disk.
 */
public class EmailExtractionStrategy implements ExtractionStrategy {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}");
    private final ResultWriter writer;

    public EmailExtractionStrategy(ResultWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> emails = new ArrayList<>();
        if (htmlContent == null) return emails;

        Matcher matcher = EMAIL_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String email = matcher.group();
            emails.add(email);
            try {
                // Write the found email to disk immediately
                writer.writeResult("URL: " + url + " | Email: " + email);
            } catch (IOException e) {
                System.err.println("Error writing email to file: " + e.getMessage());
            }
        }
        return emails; // Return for debugging or other purposes if needed
    }
}
