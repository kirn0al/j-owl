package com.github.pawawudaf.jowl.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebsiteParser {

    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)";

    private static final int MAX_CRAWLING_DEPTH = 2;

    // TODO: use Map (ConcurrentHashMap)
    private final ConcurrentHashMap<String, String> links = new ConcurrentHashMap<>();
    private final Set<String> visitedUrls = new HashSet<String>();
    private final Queue<String> urlsToVisit = new LinkedList<String>();

    // TODO: you can use Map as parameter
    public Map<String, String> parse(String seedUrl, Map<String, String> map, int currentDepth) {
        if (currentDepth > MAX_CRAWLING_DEPTH) {
            return map;
        }

        urlsToVisit.add(seedUrl);
        String html = fetchHtml(seedUrl);
        List<String> parsedLinks = parseLinks(html);
        storeResults(html);

        parsedLinks.stream()
            .filter(link -> !visitedUrls.contains(link))
            .peek(urlsToVisit::add)
            .forEach(link -> parse(link, map, currentDepth + 1));
        return map;
    }
//    public void parse(String seedUrl, Map<String, String>) {
//        int currentDepth = 0;
//        urlsToVisit.add(seedUrl);
//        while (!urlsToVisit.isEmpty() && currentDepth <= MAX_CRAWLING_DEPTH) {
//            String url = urlsToVisit.remove();
//            visitedUrls.add(url);
//            String html = fetchHtml(url);
//            List<String> links = parseLinks(html);
//
//            for (String link : links) {
//                if (!visitedUrls.contains(link)) {
//                    urlsToVisit.add(link);
//                }
//            }
//            currentDepth++;
//            storeResults(html);
//        }
//    }

    private String fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();
            return html.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private List<String> parseLinks(String html) {
        Document doc = Jsoup.parse(html);
        Elements linkElements = doc.select("a[href]");

        return linkElements.stream()
            .map(linkElement -> linkElement.attr("href"))
            .filter(this::isLinkValid)
            .toList();
    }

    private boolean isLinkValid(String url) {
        return url.contains(LINK_VALIDATION_REGEX);
    }

    // TODO: use Lucene, extract into separate class
    private void storeResults(String html) {
        try (PrintWriter out = new PrintWriter(new FileWriter("results.txt", true))) {
            out.println(html);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
