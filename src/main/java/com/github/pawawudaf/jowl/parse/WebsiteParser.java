package com.github.pawawudaf.jowl.parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.concurrent.ConcurrentMap;

@Component
public class WebsiteParser {

    private static final String LINK_VALIDATION_REGEX = "^(http://|https://)";

    private static final int MAX_CRAWLING_DEPTH = 2;

    private final Set<String> visitedUrls = new HashSet<String>();
    private final Queue<String> urlsToVisit = new LinkedList<String>();

    public ParsedData parse(String seedUrl, ParsedData parsedData, int currentDepth) {
        if (currentDepth > MAX_CRAWLING_DEPTH) {
            return parsedData;
        }

        urlsToVisit.add(seedUrl);
        String html = fetchHtml(seedUrl);
        parsedData.putObject(seedUrl, html);
        List<String> parsedLinks = parseLinks(html);
        storeResults(html);

        parsedLinks.stream()
            .filter(link -> !visitedUrls.contains(link))
            .peek(urlsToVisit::add)
            .forEach(link -> parse(link, parsedData, currentDepth + 1));
        return parsedData;
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
