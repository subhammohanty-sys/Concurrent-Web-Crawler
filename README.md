# High-Performance Concurrent Web Crawler 🕸️

## Overview of the Project
A robust, multithreaded web crawler and data extraction engine built entirely in core Java. This project was built to demonstrate advanced Java software engineering principles, specifically focusing on high-performance concurrency, thread-safe collections, scalable I/O, and extensible OOP design patterns. It efficiently navigates web pages and extracts targeted data types while keeping memory usage minimal.

## Features
* **Concurrency Engine (`ExecutorService`)**: Utilizes a fixed thread pool to parse multiple web pages simultaneously.
* **Thread-Safe Collections (`ConcurrentHashMap`)**: Leverages concurrent collections for O(1) visited-URL lookups, entirely preventing infinite scraping loops and race conditions.
* **Strategy Design Pattern (7 Extractors)**: The core parsing engine is completely decoupled from the data extraction logic. Included extractors:
  - `LinkExtractionStrategy`: Discovers new pages to crawl.
  - `EmailExtractionStrategy`: Finds email addresses.
  - `TitleExtractionStrategy`: Grabs HTML `<title>` tags.
  - `ImageExtractionStrategy`: Extracts image source URLs.
  - `MetadataExtractionStrategy`: Gathers meta tags and Open Graph properties.
  - `PhoneNumberExtractionStrategy`: Identifies phone numbers.
  - `CleanTextExtractionStrategy`: Strips HTML and saves readable text snippets.
* **Resilient I/O Streaming**: Avoids JVM Memory Exhaustion (`OutOfMemoryError`) by utilizing synchronized buffered writers that stream extracted data directly to disk (`results.csv`) rather than holding massive datasets in RAM.
* **Graceful Exception Handling**: Built to survive the chaos of the internet. Catches and logs HTTP timeouts and malformed URLs without crashing the background worker threads.

## Technologies/Tools Used
- **Language**: Java 11+
- **Core APIs**: `java.net.http.HttpClient`, `java.util.concurrent`, `java.util.regex`
- **Build Tool**: None (Standard `javac` compiler)
- **Version Control**: Git

## Steps to Install & Run the Project

### 1. Prerequisites
Ensure you have the Java Development Kit (JDK 11 or higher) installed. You do not need Maven or Gradle.

### 2. Compile the Source Code
Navigate to the root directory of the project in your terminal and compile all source files:
```bash
javac -cp src src/crawler/Main.java src/crawler/WebCrawler.java src/crawler/io/ResultWriter.java src/crawler/state/CrawlState.java src/crawler/strategy/*.java
```

### 3. Execute the Crawler
Run the CLI program from the root directory by providing a Seed URL, the Maximum number of Threads, and the Maximum pages to crawl.

```bash
# Usage: java -cp src crawler.Main <SeedURL> <MaxThreads> <MaxPages>
java -cp src crawler.Main https://books.toscrape.com 10 50
```

### 4. View the Results
All extracted data is safely streamed and appended to `results.csv` in the root directory.

## Instructions for Testing
To test the crawler, you can use safe sandbox websites designed for scraping tests:
1. Run a test against a book store sandbox: 
   `java -cp src crawler.Main https://books.toscrape.com 10 20`
2. Run a test against a scraping sandbox: 
   `java -cp src crawler.Main https://www.scrapethissite.com/ 5 15`
3. After the run completes (check the console for execution time and statistics), open `results.csv` in Excel or a text editor to verify that Links, Images, and text snippets were correctly captured.

