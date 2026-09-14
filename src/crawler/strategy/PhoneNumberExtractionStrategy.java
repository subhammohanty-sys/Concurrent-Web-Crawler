package crawler.strategy;

import crawler.io.ResultWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy to extract phone numbers using regex.
 */
public class PhoneNumberExtractionStrategy implements ExtractionStrategy {
    // Basic phone number pattern matching various formats (e.g., +1-234-567-8900, (123) 456-7890, 1234567890)
    // Avoid matching typical year numbers like 2023 or simple ID numbers
    private static final Pattern PHONE_PATTERN = Pattern.compile("(?:\\+?\\d{1,3}[-.\\s]?)?\\(?\\d{3}\\)?[-.\\s]?\\d{3}[-.\\s]?\\d{4}");
    private final ResultWriter writer;

    public PhoneNumberExtractionStrategy(ResultWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> phones = new ArrayList<>();
        if (htmlContent == null) return phones;

        Matcher matcher = PHONE_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String phone = matcher.group();
            // Basic length check to avoid matching random small numbers
            if (phone.replaceAll("[^\\d]", "").length() >= 7) {
                phones.add(phone);
                try {
                    writer.writeResult("URL: " + url + " | Phone: " + phone);
                } catch (IOException e) {
                    System.err.println("Error writing phone to file: " + e.getMessage());
                }
            }
        }
        return phones;
    }
}
