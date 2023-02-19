package com.github.pawawudaf.jowl.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Component
public class WebsiteParser {

    // TODO: got rid of long regex
    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)[a-zA-Z0-9\\-\\.]+\\.[a-zA-Z]{2,}(:(\\d)+)?(/($|[a-zA-Z0-9\\-\\.\\?\\,\\'\\\\/+=&amp;%\\$#_\\*!]+))*$";

    private static final int MAX_CRAWLING_DEPTH = 2;

    // TODO: use Map (ConcurrentHashMap)
    private final Set<String> visitedUrls;
    private final Queue<String> urlsToVisit;

    public WebsiteParser() {
        this.visitedUrls = new HashSet<>();
        this.urlsToVisit = new LinkedList<>();
    }

    // TODO: you can use Map as parameter
    // TODO: add recursion
    // TODO: use stream API
    public void parse(String seedUrl) {
        int currentDepth = 0;
        urlsToVisit.add(seedUrl);
        while (!urlsToVisit.isEmpty() && currentDepth <= MAX_CRAWLING_DEPTH) {
            String url = urlsToVisit.remove();
            visitedUrls.add(url);
            String html = fetchHtml(url);
            List<String> links = parseLinks(html);
            for (String link : links) {
                if (!visitedUrls.contains(link)) {
                    urlsToVisit.add(link);
                }
            }
            currentDepth++;
            storeResults(html);
        }
    }

    private String fetchHtml(String url) {
        try {
            Document html = Jsoup.connect(url).get();
            return html.toString();
        } catch (IOException e) {
            return "";
        }
    }

    private List<String> parseLinks(String html) {
        List<String> links = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements linkElements = doc.select("a[href]");

        // TODO: use stream API
        for (Element linkElement : linkElements) {
            String link = linkElement.attr("href");
            if (isLinkValid(link)) {
                links.add(link);
            }
        }
        return links;
    }

    // TODO: simplify
    private boolean isLinkValid(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        return url.matches(LINK_VALIDATION_REGEX);
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
