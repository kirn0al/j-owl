package com.example.jowl;

import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JOwlApplication {

    private final WebCrawler webCrawler = new WebCrawler();

    public static void main(String[] args) {
        JOwlApplication jOwl = new JOwlApplication();
        jOwl.webCrawler.crawl("https://alibaba-cloud.medium.com/analysis-of-lucene-basic-concepts-5ff5d8b90a53");
    }
}
