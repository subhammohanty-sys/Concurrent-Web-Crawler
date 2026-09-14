package crawler.strategy;

import crawler.io.ResultWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strategy to extract image URLs (<img src="...">) and write them to disk.
 */
public class ImageExtractionStrategy implements ExtractionStrategy {
    private static final Pattern IMG_PATTERN = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>", Pattern.CASE_INSENSITIVE);
    private final ResultWriter writer;

    public ImageExtractionStrategy(ResultWriter writer) {
        this.writer = writer;
    }

    @Override
    public List<String> extract(String url, String htmlContent) {
        List<String> images = new ArrayList<>();
        if (htmlContent == null) return images;

        Matcher matcher = IMG_PATTERN.matcher(htmlContent);
        while (matcher.find()) {
            String imgSrc = matcher.group(1);
            images.add(imgSrc);
            try {
                writer.writeResult("URL: " + url + " | Image: " + imgSrc);
            } catch (IOException e) {
                System.err.println("Error writing image to file: " + e.getMessage());
            }
        }
        return images;
    }
}
