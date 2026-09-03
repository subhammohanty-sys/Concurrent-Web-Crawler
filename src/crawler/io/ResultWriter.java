package crawler.io;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Module 3: Exception Handling & I/O
 * Streams extracted datasets directly to disk to prevent Memory exhaustion (OOM).
 * Thread-safe writing capability.
 */
public class ResultWriter implements AutoCloseable {
    private final BufferedWriter writer;

    public ResultWriter(String filePath) throws IOException {
        // Append mode so data is appended over time
        this.writer = new BufferedWriter(new FileWriter(filePath, true));
    }

    /**
     * Synchronized to allow multiple threads to safely write to the same file.
     */
    public synchronized void writeResult(String result) throws IOException {
        writer.write(result);
        writer.newLine();
        writer.flush(); // Flush to avoid losing data if a crash happens
    }

    @Override
    public void close() throws Exception {
        if (writer != null) {
            writer.close();
        }
    }
}
