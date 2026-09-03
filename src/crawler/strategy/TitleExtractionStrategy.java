package crawler.strategy;

import crawler.io.ResultWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy to extract the HTML <title> of the page and write it to disk.
 */
public class TitleExtractionStrategy implements ExtractionStrategy {
    // Regex to capture the text inside the <title> tags
    private static final Pattern TITLE_PATTERN = Pattern.compile("<title>(.*?)</title>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private final ResultWriter writer;

    public TitleExtractionStrategy(ResultWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> titles = new ArrayList<>();
        if (htmlContent == null) return titles;

        Matcher matcher = TITLE_PATTERN.matcher(htmlContent);
        if (matcher.find()) {
            // .trim() removes excess whitespace/newlines
            String title = matcher.group(1).trim();
            titles.add(title);
            try {
                // Write the found title to the CSV file!
                writer.writeResult("URL: " + url + " , TITLE: " + title);
            } catch (IOException e) {
                System.err.println("Error writing title to file: " + e.getMessage());
            }
        }
        return titles;
    }
}
