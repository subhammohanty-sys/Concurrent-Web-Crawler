# High-Performance Concurrent Web Crawler 🕸️

A robust, multithreaded web crawler and data extraction engine built entirely in core Java. 

This project was built to demonstrate advanced Java software engineering principles, specifically focusing on high-performance concurrency, thread-safe collections, scalable I/O, and extensible OOP design patterns.

## 🚀 Features & Architecture

* **Concurrency Engine (`ExecutorService`)**: Utilizes a fixed thread pool to parse multiple web pages simultaneously.
* **Thread-Safe Collections (`ConcurrentHashMap`)**: Leverages concurrent collections for O(1) visited-URL lookups, entirely preventing infinite scraping loops and race conditions.
* **Strategy Design Pattern**: The core parsing engine is completely decoupled from the data extraction logic. New extraction rules (e.g., finding emails, scraping titles, downloading images) can be plugged in by creating a simple class that implements `ExtractionStrategy`.
* **Resilient I/O Streaming**: Avoids JVM Memory Exhaustion (`OutOfMemoryError`) by utilizing synchronized buffered writers that stream extracted data directly to disk (`results.csv`) rather than holding massive datasets in RAM.
* **Graceful Exception Handling**: Built to survive the chaos of the internet. Catches and logs HTTP timeouts and malformed URLs without crashing the background worker threads.

## 🛠️ Usage

This project uses standard Java 11+ (specifically `java.net.http.HttpClient`) and requires zero external dependencies (No Maven/Gradle required).

### 1. Compile the Source Code
Navigate to the root directory of the project and compile the source files:
```bash
javac -cp src src/crawler/Main.java src/crawler/WebCrawler.java src/crawṭler/io/ResultWriter.java src/crawler/state/CrawlState.java src/crawler/strategy/ExtractionStrategy.java src/crawler/strategy/EmailExtractionStrategy.java src/crawler/strategy/LinkExtractionStrategy.java src/crawler/strategy/TitleExtractionStrategy.java
```

### 2. Execute the Crawler
Run the CLI program by providing a Seed URL, the Maximum number of Threads, and the Maximum pages to crawl.

```bash
# Usage: java -cp src crawler.Main <SeedURL> <MaxThreads> <MaxPages>

java -cp src crawler.Main https://books.toscrape.com 10 50
```

### 3. View the Results
All extracted data is safely streamed and appended to `results.csv` in the root directory.

## 📂 Project Structure

```
├── src/
│   └── crawler/
│       ├── Main.java               # CLI Entry Point
│       ├── WebCrawler.java         # Thread Pool & Orchestration Engine
│       ├── state/
│       │   └── CrawlState.java     # Synchronized tracking of visited URLs & Domains
│       ├── io/
│       │   └── ResultWriter.java   # Synchronized Buffered File I/O
│       └── strategy/
│           ├── ExtractionStrategy.java
│           ├── LinkExtractionStrategy.java
│           ├── EmailExtractionStrategy.java
│           └── TitleExtractionStrategy.java
├── .gitignore                      # Prevents committing .class files and local results
└── README.md                       # This file!
```
