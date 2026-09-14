# Problem Statement
In the modern digital landscape, data is abundant but widely distributed across millions of web pages. Researchers, analysts, and developers often need to aggregate specific information (like links, emails, page titles, images, and metadata) from various websites. However, manually extracting this data is time-consuming and inefficient. Existing scraping tools can be bloated, difficult to configure, or prone to crashing due to memory exhaustion when crawling large domains. There is a need for a lightweight, high-performance, and extensible concurrent web crawler that can efficiently extract targeted data while managing memory and connection failures gracefully.

# Scope of the Project
This project involves building a robust, multithreaded web crawler from scratch using core Java (Java 11+). The scope includes:
- Navigating web pages starting from a given seed URL.
- Concurrently processing multiple pages using a thread pool.
- Extracting specific data points: Links, Emails, Titles, Images, Metadata, Phone Numbers, and Clean Text.
- Streaming extracted data directly to disk to maintain a low memory footprint.
- Tracking visited URLs and domains to prevent infinite loops and provide crawling statistics.

## Out of Scope
- **JavaScript Execution:** The crawler will not execute JavaScript or render Single Page Applications (SPAs) like React or Angular (it only parses raw HTML).
- **Authentication:** Logging into websites, managing session cookies, or bypassing CAPTCHAs.
- **Database Integration:** Saving results directly to a SQL/NoSQL database (data is solely output to a CSV file).
- **Media Processing:** The crawler identifies and extracts image URLs but does not download, transcode, or process actual media files (e.g., video, audio, or large documents).

# Target Users
- **Data Analysts and Researchers:** Who need to gather specific datasets from the web quickly.
- **Software Developers:** Who need an extensible, basic crawler to integrate into larger data pipelines.
- **Digital Marketers and SEO Specialists:** Who require automated tools to quickly audit metadata, tags, and page links across various domains.

# High-Level Features
- **Concurrent Crawling Engine:** Utilizes an `ExecutorService` thread pool to maximize throughput.
- **Pluggable Data Extractors:** Uses the Strategy design pattern, allowing users to easily add new extraction logic.
- **Resilient I/O:** Safely streams data to disk (`results.csv`) via synchronized buffered writers, preventing `OutOfMemoryError`.
- **Thread-Safe State Management:** Uses `ConcurrentHashMap` to track visited URLs with O(1) lookups, completely eliminating race conditions.
- **Graceful Error Handling:** Survives HTTP timeouts, malformed URLs, and connection drops without crashing worker threads.
